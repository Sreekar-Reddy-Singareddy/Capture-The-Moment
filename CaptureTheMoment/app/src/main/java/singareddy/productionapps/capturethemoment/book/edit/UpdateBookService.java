package singareddy.productionapps.capturethemoment.book.edit;

import android.support.annotation.NonNull;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import singareddy.productionapps.capturethemoment.DataRepository;
import singareddy.productionapps.capturethemoment.DataSyncListener;
import singareddy.productionapps.capturethemoment.book.BookListener;
import singareddy.productionapps.capturethemoment.utils.AppUtilities;
import static singareddy.productionapps.capturethemoment.utils.AppUtilities.Firebase.*;
import static singareddy.productionapps.capturethemoment.utils.AppUtilities.Book.*;
import static singareddy.productionapps.capturethemoment.utils.AppUtilities.User.*;

import singareddy.productionapps.capturethemoment.models.Book;
import singareddy.productionapps.capturethemoment.models.SecondaryOwner;

public class UpdateBookService {
    private static String TAG = "AddBookService";

    private FirebaseDatabase mfirebaseDB;
    private HashMap<String, Boolean> validSecOwnersMap;
    private Integer mOwnersValidated;
    private Boolean mIsBookNameValid = false;
    private Boolean mAreSecOwnersValid = false;
    private String mNewBookName;
    private String mBookIdToUpdate;
    private List<SecondaryOwner> activeOwners;
    private List<SecondaryOwner> removedOwners;
    private Boolean sameBookName;
    private UpdateBookListener updateBookListener;
    private DataSyncListener dataSyncListener;
    private BookListener bookListener;

    public UpdateBookService () {
        mfirebaseDB = FirebaseDatabase.getInstance();
    }

    public void updateThisBook(String bookId, Boolean sameBookName, String newName, List<SecondaryOwner> activeOwners, List<SecondaryOwner> removedOwners) {
        mBookIdToUpdate = bookId;
        mNewBookName = newName;
        this.activeOwners = activeOwners;
        this.removedOwners = removedOwners;
        this.sameBookName = sameBookName;
        mAreSecOwnersValid = false;
        mIsBookNameValid = sameBookName;

        if (!sameBookName) validateBookNameInFirebase();
        validateSecOwnersInFirebase();
        deleteSecOwnersInFirebase();
    }

    private void deleteSecOwnersInFirebase() {
        DatabaseReference allRegUsers = mfirebaseDB.getReference().child(ALL_REGISTERED_USERS_NODE);
        for (SecondaryOwner removedOwner: removedOwners) {
            Query query = allRegUsers.orderByValue().equalTo(removedOwner.getUsername().toLowerCase().trim());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        Log.i(TAG, "onDataChange: USERMAP: "+dataSnapshot.getValue());
                        HashMap<String, String> userMap = (HashMap<String, String>) dataSnapshot.getValue();
                        String uid = (String) userMap.keySet().toArray()[0];
                        deleteSharedBookFromThisUser(uid);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    /**
     * Deletes ref of this book in the removed sec owners.
     * Users -> UID -> SharedBooks -> This BookID
     * @param uid
     */
    private void deleteSharedBookFromThisUser(String uid) {
        DatabaseReference sharedBooksUidNode = mfirebaseDB.getReference()
                .child(ALL_USERS_NODE)
                .child(uid)
                .child(KEY_USER_SHARED_BOOKS)
                .child(mBookIdToUpdate);
        sharedBooksUidNode.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dataSyncListener.hasToRemoveSecOwnerFromRoomDB(mBookIdToUpdate, uid);
                    }
                });
    }

    /**
     * Validated secondary owners from the firebase database
     */
    private void validateSecOwnersInFirebase() {
        DatabaseReference targetDataNode = mfirebaseDB.getReference()
                .child(AppUtilities.Firebase.ALL_REGISTERED_USERS_NODE);
        mOwnersValidated = 0;
        validSecOwnersMap = new HashMap<>();

        if (activeOwners.size() == 0) {
            mAreSecOwnersValid = true;
            updateBookInFirebase();
        }

        // Run a loop for every secondary owner
        for (final SecondaryOwner secOwner : activeOwners) {
            Query query = targetDataNode.orderByValue().equalTo(secOwner.getUsername().toLowerCase().trim());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mOwnersValidated++;

                    // This snapshot MUST be non null.
                    if (dataSnapshot.getValue() != null) {
                        // User is valid
                        secOwner.setValidated(SEC_OWNER_VALID);
                        Map<String, String> userMap = (HashMap<String, String>) dataSnapshot.getValue();
                        // Save the valid user in cache
                        updateBookListener.hasToSaveUidInCache(userMap);
                        // Add a key value pair (UID: Access) for this secondary user
                        validSecOwnersMap.put((String) userMap.keySet().toArray()[0], secOwner.getCanEdit());
                    }
                    else {
                        // User is not valid
                        secOwner.setValidated(SEC_OWNER_INVALID);
                    }
                    updateBookListener.onThisSecOwnerValidated();

                    // UI must be told once all items have been checked.
                    // This is done ONLY once for every click on the UI button.
                    // That button is enabled again ONLY after this notification is sent to UI.
                    if (mOwnersValidated == activeOwners.size()) {
                        updateBookListener.onAllSecOwnersValidated();
                        mOwnersValidated = 0;
                    }

                    // Once done, check if all the sec owners are valid
                    if (activeOwners.size() == validSecOwnersMap.size()) {
                        mAreSecOwnersValid = true;
                        updateBookInFirebase();
                        return;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Even though it is an error, the user has been validated once
                    // So, update the count
                    mOwnersValidated++;
                    secOwner.setValidated(SEC_OWNER_INVALID);
                    updateBookListener.onThisSecOwnerValidated();
                }
            });
        }
    }

    /**
     * Validates book name from Firebase DB.
     * It checks for duplicate books if already exists.
     */
    private void validateBookNameInFirebase () {
        // Validate the book name from Firebase
        mfirebaseDB.getReference()
                .child(ALL_BOOKS_NODE)
                .orderByChild(KEY_USER_OWNER)
                .equalTo(AppUtilities.User.CURRENT_USER.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) {
                            // There are no owned books for this user.
                            // User is creating their first book.
                            mIsBookNameValid = true;
                            updateBookInFirebase();
                            return;
                        }

                        // GetBookListener the data from snapshot
                        HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();

                        // Check each book for dupliate name
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            ObjectMapper mapper = new ObjectMapper();
                            Book b = mapper.convertValue(entry.getValue(), Book.class);
                            if (b.getName().toLowerCase().trim().equals(mNewBookName.toLowerCase().trim())) {
                                updateBookListener.onBookNameInvalid(AppUtilities.Book.BOOK_EXISTS);
                                return;
                            }
                        }

                        // No duplicate found. Book can be created
                        mIsBookNameValid = true;
                        updateBookInFirebase();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void updateBookInFirebase() {
        if (!mAreSecOwnersValid || !mIsBookNameValid) return;
        // Create a map to update
        HashMap<String, Object> mapOfDataToUpdate = new HashMap<>();
        mapOfDataToUpdate.put(KEY_BOOK_NAME, mNewBookName);
        mapOfDataToUpdate.put(KEY_BOOK_LAST_UPDATED, new Date().getTime());
        mapOfDataToUpdate.put(KEY_BOOK_SEC_OWNERS, validSecOwnersMap);

        // If both book name and owners are valid, they must be updated in the firebase
        mfirebaseDB.getReference()
                .child(ALL_BOOKS_NODE)
                .child(mBookIdToUpdate)
                .updateChildren(mapOfDataToUpdate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // On successful update, save the book in Room DB
                        mfirebaseDB.getReference()
                                .child(ALL_BOOKS_NODE).child(mBookIdToUpdate)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Book book = dataSnapshot.getValue(Book.class);
                                        updateBookListener.hasToSaveBookInCache(book);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                        // Update the secondary owners
                        updateSharedBooksOfSecOwnersInFirebase();
                    }
                });
    }

    /**
     * This method updates the edit access of the active
     * secondary owners. This is done because the access might
     * have either been revoked or newly given.
     * For example, U123 has 'false' before. But now, U123 might have 'true'.
     */
    private void updateSharedBooksOfSecOwnersInFirebase() {
        for (Map.Entry<String, Boolean> entry: validSecOwnersMap.entrySet()) {
            String uid = entry.getKey(); Boolean canEdit = entry.getValue();
            mfirebaseDB.getReference()
                    .child(ALL_USERS_NODE)
                    .child(uid)
                    .child(KEY_USER_SHARED_BOOKS)
                    .child(mBookIdToUpdate).setValue(canEdit)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            updateBookListener.onBookUpdated();
                        }
                    });
        }
    }

    /**
     * This method takes UID of a user and
     * gets the corresponding username to store in the
     * device cache.
     * @param uid
     */
    public void getUsernameOfUid(String uid) {
        mfirebaseDB.getReference()
                .child(ALL_REGISTERED_USERS_NODE)
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            // This is the corresponding username of the given UID
                            String usernameOfUid = dataSnapshot.getValue(String.class);
                            // Once recieved, tell the data sync listener to store
                            // it in the device cache
                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put(uid, usernameOfUid);
                            bookListener.hasToSaveUidInCache(userMap);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void setUpdateBookListener(UpdateBookListener updateBookListener) {
        this.updateBookListener = updateBookListener;
    }

    public void setDataSyncListener(DataSyncListener dataSyncListener) {
        this.dataSyncListener = dataSyncListener;
    }

    public void setBookListener(BookListener bookListener) {
        this.bookListener = bookListener;
    }
}
