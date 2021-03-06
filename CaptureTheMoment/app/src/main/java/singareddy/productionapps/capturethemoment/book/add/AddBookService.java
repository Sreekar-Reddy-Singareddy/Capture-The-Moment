package singareddy.productionapps.capturethemoment.book.add;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import singareddy.productionapps.capturethemoment.utils.AppUtilities;
import singareddy.productionapps.capturethemoment.DataRepository;
import singareddy.productionapps.capturethemoment.models.Book;
import singareddy.productionapps.capturethemoment.models.SecondaryOwner;

import static singareddy.productionapps.capturethemoment.utils.AppUtilities.*;

public class AddBookService {
    private static String TAG = "AddBookService";

    private FirebaseDatabase mfirebaseDB;
    private HashMap<String, Boolean> validSecOwnersMap;
    private Integer mOwnersValidated;
    private AddBookListener mAddBookListener;
    private Boolean mIsBookNameValid = false;
    private Boolean mAreSecOwnersValid = false;
    private String mBookName;
    private List<SecondaryOwner> mSecOwnersList;
    private MutableLiveData<Integer> ownerValidated;
    private Observer<Integer> ownersObserver;

    public AddBookService () {
        mfirebaseDB = FirebaseDatabase.getInstance();
    }

    /** STATUS - NOT WORKING
     * This method interacts with the firebase to validate
     * the secondary owners.
     * @param bookName
     * @param secOwners - List of secondary owners and their edit access
     */
    public void createThisBook(final String bookName, final List<SecondaryOwner> secOwners) {
        // Update the member variables to newly given name and owners
        mBookName = bookName;
        mSecOwnersList = secOwners;
        mAreSecOwnersValid = false;
        mIsBookNameValid = false;
        ownerValidated =  new MutableLiveData<>();
        ownersObserver = new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                // UI must be told once all items have been checked.
                // This is done ONLY once for every click on the UI button.
                // That button is enabled again ONLY after this notification is sent to UI.
                if (integer == mSecOwnersList.size()) {
                    mAddBookListener.onAllSecOwnersValidated();
                    mOwnersValidated = 0;
                }

                // Once done, check if all the sec owners are valid
                if (mSecOwnersList.size() == validSecOwnersMap.size()) {
                    mAreSecOwnersValid = true;
                    saveNewBookInFirebase();
                    return;
                }
            }
        };
        ownerValidated.observeForever(ownersObserver);

        // Verify the book name if it already exists
        validateBookNameInFirebase();

        // Verify the secondary owners
        validateSecOwnersInFirebase();
    }

    /**
     * Validated secondary owners from the firebase database
     */
    private void validateSecOwnersInFirebase() {
        // Get the usernames cache from repository
        SharedPreferences usernamesCache = mAddBookListener instanceof DataRepository ?
                ((DataRepository) mAddBookListener).getUsernamesCache() : null;

        // Get reference to the registeredUsers node
        DatabaseReference targetDataNode = mfirebaseDB.getReference().child(AppUtilities.Firebase.ALL_REGISTERED_USERS_NODE);
        mOwnersValidated = 0;
        validSecOwnersMap = new HashMap<>();

        if (mSecOwnersList.size() == 0) {
            mAreSecOwnersValid = true;
            saveNewBookInFirebase();
        }

        // Run a loop for every secondary owner
        for (final SecondaryOwner secOwner : mSecOwnersList) {
            // Check this owner in cache, if available
            String uid = usernameExistsInCache(secOwner, usernamesCache);
            if (uid != null) {
                secOwner.setValidated(1);
                validSecOwnersMap.put(uid, secOwner.getCanEdit());
                mAddBookListener.onThisSecOwnerValidated();
                mOwnersValidated++; ownerValidated.setValue(mOwnersValidated);
                continue;
            }

            // Is username is not there in cache, then maybe it exists in Firebase
            // Check in firebase for this username.
            Query query = targetDataNode.orderByValue().equalTo(secOwner.getUsername().toLowerCase().trim());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Exists or not, this user has been checked for.
                    // So increase the count.
                    mOwnersValidated++; ownerValidated.setValue(mOwnersValidated);
                    // Snapshot NOT NULL means username exists
                    if (dataSnapshot.getValue() != null) {
                        Map<String, String> userMap = (HashMap<String, String>) dataSnapshot.getValue();
                        secOwner.setValidated(AppUtilities.Book.SEC_OWNER_VALID);
                        // Add a key value pair (UID: Access) for this secondary user
                        validSecOwnersMap.put((String) userMap.keySet().toArray()[0], secOwner.getCanEdit());
                        // Save the uid: username pair in cache
                        mAddBookListener.hasToSaveUidInCache(userMap);
                    }
                    else {
                        // User is not valid
                        secOwner.setValidated(AppUtilities.Book.SEC_OWNER_INVALID);
                    }
                    mAddBookListener.onThisSecOwnerValidated();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Even though it is an error, the user has been validated once
                    // So, update the count
                    mOwnersValidated++;
                    secOwner.setValidated(AppUtilities.Book.SEC_OWNER_INVALID);
                    mAddBookListener.onThisSecOwnerValidated();
                }
            });
        }
    }

    private String usernameExistsInCache(SecondaryOwner secondaryOwner, SharedPreferences cache) {
        // If the user is not in cache then return null
        String username = secondaryOwner.getUsername().toLowerCase().trim();
        if (!cache.getAll().values().contains(username)) return null;
        // Find the UID of this user
        for (Map.Entry<String, ?> user : cache.getAll().entrySet()) {
            if (user.getValue().equals(username)) {
                return user.getKey();
            }
        }
        return null;
    }

    /**
     * Validates book name from Firebase DB.
     * It checks for duplicate books if already exists.
     */
    private void validateBookNameInFirebase () {
        // Validate the book name from Firebase
        mfirebaseDB.getReference().child(AppUtilities.Firebase.ALL_BOOKS_NODE)
                .orderByChild(AppUtilities.Firebase.KEY_USER_OWNER).equalTo(AppUtilities.User.CURRENT_USER.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) {
                            // There are no owned books for this user.
                            // User is creating their first book.
                            mIsBookNameValid = true;
                            saveNewBookInFirebase();
                            return;
                        }

                        // GetBookListener the data from snapshot
                        HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();

                        // Check each book for dupliate name
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            ObjectMapper mapper = new ObjectMapper();
                            Book b = mapper.convertValue(entry.getValue(), Book.class);
                            if (b.getName().toLowerCase().trim().equals(mBookName.toLowerCase().trim())) {
                                mAddBookListener.onBookNameInvalid(AppUtilities.Book.BOOK_EXISTS);
                                return;
                            }
                        }

                        // No duplicate found. Book can be created
                        mIsBookNameValid = true;
                        saveNewBookInFirebase();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    /**
     * This method takes the book name and secondary owners
     * and adds them as follows.
     * Save the data in Room DB.
     */
    private void saveNewBookInFirebase() {
        Log.i(TAG, "saveNewBookInFirebase: Owner Name: "+ User.CURRENT_USER.getDisplayName());
        // This works only if both book name and sec owners are valid
        if (!mIsBookNameValid || !mAreSecOwnersValid) return;
        // Since we are done using observer, it has to be removed
        ownerValidated.removeObserver(ownersObserver);
        // Create new book with the given details
        String newBookId = generateRandomBookId();
        Book newBook = new Book();
        newBook.setBookId(newBookId);
        newBook.setName(mBookName);
        newBook.setOwner(AppUtilities.User.CURRENT_USER.getUid()); long timeNow = new Date().getTime();
        newBook.setOwnerName(User.CURRENT_USER.getDisplayName());
        newBook.setCreatedDate(timeNow);
        newBook.setLastUpdatedDate(timeNow);
        newBook.setSecOwners(validSecOwnersMap);
        // Save this book in the Firebase
        mfirebaseDB.getReference().child(AppUtilities.Firebase.ALL_BOOKS_NODE) // books
                .child(newBookId)  // new bookId
                .setValue(newBook)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            updateOwnerInFirebase(newBook);
                        }
                    }
                });


    }

    /**
     * This method updates the owner's ownedBooks in Firebase.
     * Called only after
     * 1. Book is created.
     */
    private void updateOwnerInFirebase(Book newBook) {
        DatabaseReference ownerOwnedBooksRef = mfirebaseDB.getReference()
                .child(AppUtilities.Firebase.ALL_USERS_NODE) // users
                .child(AppUtilities.User.CURRENT_USER.getUid()) // owner UID
                .child(Firebase.KEY_USER_PROFILE) // profile
                .child(Firebase.KEY_USER_OWNED_BOOKS); // ownedBooks
        ownerOwnedBooksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) { // Array of Strings (Book IDs)
                // Gets the current owned books list
                ArrayList<String> ownedBooks;
                if (dataSnapshot.getValue() == null) ownedBooks = new ArrayList<>();
                else ownedBooks = (ArrayList<String>) dataSnapshot.getValue();
                ownedBooks.add(newBook.getBookId());

                // Set this updated list now
                ownerOwnedBooksRef.setValue(ownedBooks).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            updateSecOwnersInFirebase(newBook);
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Updates the sharedBooks node of every secondary owner in Firebase.
     * Called only after
     * 1. Book is updated and
     * 2. Owner node is updated
     * @param newBook
     */
    private void updateSecOwnersInFirebase(Book newBook) {
        HashMap<String, Boolean> secOwnersMap = newBook.getSecOwners();
        for (Map.Entry<String, Boolean> entry : secOwnersMap.entrySet()) {
            mfirebaseDB.getReference()
                    .child(AppUtilities.Firebase.ALL_USERS_NODE) // users
                    .child(entry.getKey()) // UID
                    .child(Firebase.KEY_USER_SHARED_BOOKS) // sharedBooks
                    .child(newBook.getBookId()) // NEW KEY - Created book ID
                    .setValue(entry.getValue())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Now, the book has been totally saved in Firebase
                                mAddBookListener.onNewBookCreated();
                                // Save this same book in the cache (Room DB) as well
                                mAddBookListener.hasToSaveBookInCache(newBook);
                            }
                        }
                    });
        }
    }

    /**
     * Generates a random book id for a new book
     * @return
     */
    private String generateRandomBookId() {
        return mfirebaseDB.getReference().child(AppUtilities.Firebase.ALL_BOOKS_NODE).push().getKey();
    }

    public void setBookListener(AddBookListener addBookListener) {
        this.mAddBookListener = addBookListener;
    }
}
