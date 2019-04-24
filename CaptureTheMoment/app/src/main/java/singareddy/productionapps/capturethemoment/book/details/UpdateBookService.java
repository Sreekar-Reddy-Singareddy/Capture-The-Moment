package singareddy.productionapps.capturethemoment.book.details;

import android.support.annotation.NonNull;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import singareddy.productionapps.capturethemoment.AppUtilities;
import static singareddy.productionapps.capturethemoment.AppUtilities.Firebase.*;
import singareddy.productionapps.capturethemoment.book.addbook.AddBookListener;
import singareddy.productionapps.capturethemoment.book.addbook.AddBookService;
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
    private List<SecondaryOwner> mSecOwnersList;
    private UpdateBookListener updateBookListener;

    public UpdateBookService () {
        mfirebaseDB = FirebaseDatabase.getInstance();
    }

    public void updateThisBook(String bookId, String newName, List<SecondaryOwner> secOwners) {
        mBookIdToUpdate = bookId;
        mNewBookName = newName;
        mSecOwnersList = secOwners;
        mAreSecOwnersValid = false;
        mIsBookNameValid = false;

        validateBookNameInFirebase();
        validateSecOwnersInFirebase();
    }

    /**
     * Validated secondary owners from the firebase database
     */
    private void validateSecOwnersInFirebase() {
        DatabaseReference targetDataNode = mfirebaseDB.getReference().child(AppUtilities.Firebase.ALL_REGISTERED_USERS_NODE);
        mOwnersValidated = 0;
        validSecOwnersMap = new HashMap<>();

        if (mSecOwnersList.size() == 0) {
            mAreSecOwnersValid = true;
            updateBookInFirebase();
        }

        // Run a loop for every secondary owner
        for (final SecondaryOwner secOwner : mSecOwnersList) {
            // Check if this owner has registered in the app or not
            Log.i(TAG, "createThisBook: Username: "+secOwner.getUsername());

            // If not validated, only then the control reaches here
            Query query = targetDataNode.orderByValue().equalTo(secOwner.getUsername().toLowerCase().trim());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mOwnersValidated++;

                    // This snapshot MUST be non null.
                    if (dataSnapshot.getValue() != null) {
                        // User is valid
                        secOwner.setValidated(1);
                        Map<String, String> userMap = (HashMap<String, String>) dataSnapshot.getValue();
                        // Save the valid user in cache
                        updateBookListener.hasToSaveUidInCache(userMap);
                        // Add a key value pair (UID: Access) for this secondary user
                        validSecOwnersMap.put((String) userMap.keySet().toArray()[0], secOwner.getCanEdit());
                    }
                    else {
                        // User is not valid
                        secOwner.setValidated(-1);
                    }
                    updateBookListener.onThisSecOwnerValidated();

                    // UI must be told once all items have been checked.
                    // This is done ONLY once for every click on the UI button.
                    // That button is enabled again ONLY after this notification is sent to UI.
                    if (mOwnersValidated == mSecOwnersList.size()) {
                        updateBookListener.onAllSecOwnersValidated();
                        mOwnersValidated = 0;
                    }

                    // Once done, check if all the sec owners are valid
                    if (mSecOwnersList.size() == validSecOwnersMap.size()) {
                        mAreSecOwnersValid = true;
                        updateBookInFirebase();
                        return;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.i(TAG, "onCancelled: Error: "+databaseError.getMessage());
                    // Even though it is an error, the user has been validated once
                    // So, update the count
                    mOwnersValidated++;
                    secOwner.setValidated(-1);
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
        mfirebaseDB.getReference().child("books")
                .orderByChild("owner").equalTo(AppUtilities.User.CURRENT_USER.getUid())
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
                        Log.i(TAG, "onCancelled: Message: "+databaseError.getMessage());
                        Log.i(TAG, "onCancelled: Details: "+databaseError.getDetails());
                    }
                });
    }

    private void updateBookInFirebase() {
        if (!mAreSecOwnersValid || !mIsBookNameValid) return;
        // Create a map to update
        HashMap<String, Object> mapOfDataToUpdate = new HashMap<>();
        mapOfDataToUpdate.put("name", mNewBookName);
        mapOfDataToUpdate.put("lastUpdatedDate", new Date().getTime());
        mapOfDataToUpdate.put("secOwners", validSecOwnersMap);

        // If both book name and owners are valid, they must be updated in the firebase
        mfirebaseDB.getReference()
                .child(ALL_BOOKS_NODE)
                .child(mBookIdToUpdate)
                .updateChildren(mapOfDataToUpdate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "onSuccess: Book Updated");
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

    private void updateSharedBooksOfSecOwnersInFirebase() {
        validSecOwnersMap.forEach((uid, canEdit) -> {
            mfirebaseDB.getReference()
                    .child(ALL_USERS_NODE)
                    .child(uid)
                    .child("sharedBooks")
                    .child(mBookIdToUpdate).setValue(canEdit)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i(TAG, "onSuccess: Secondary Owners Shared Books Updated");
                            updateBookListener.onBookUpdated();
                        }
                    });
        });
    }

    public void setUpdateBookListener(UpdateBookListener updateBookListener) {
        this.updateBookListener = updateBookListener;
    }
}
