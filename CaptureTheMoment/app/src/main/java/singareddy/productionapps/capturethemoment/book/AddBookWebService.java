package singareddy.productionapps.capturethemoment.book;

import android.content.Context;
import android.support.annotation.NonNull;
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

import singareddy.productionapps.capturethemoment.AppUtilities;
import singareddy.productionapps.capturethemoment.DataRepository;
import singareddy.productionapps.capturethemoment.models.Book;
import singareddy.productionapps.capturethemoment.models.SecondaryOwner;

public class AddBookWebService {
    private static String TAG = "AddBookWebService";

    private static AddBookWebService SERVICE;
    private FirebaseDatabase mfirebaseDB;
    private HashMap<String, Boolean> validSecOwnersMap;
    private Integer mOwnersValidated;
    private BookListener mBookListener;
    private Boolean mIsBookNameValid = false;
    private Boolean mAreSecOwnersValid = false;
    private DataRepository mDataRepo;
    private String mNewBookName;
    private List<SecondaryOwner> mSecOwnersList;

    // Members used in book retrieval
    private List<Book> mAllBooks;
    private BookListener.Retrieve mBookRetrieveListener;

    public AddBookWebService(Context context) {
        mfirebaseDB = FirebaseDatabase.getInstance();
        mDataRepo = DataRepository.getInstance(context);
    }

    /** STATUS - NOT WORKING
     * This method interacts with the firebase to validate
     * the secondary owners.
     * @param bookName
     * @param secOwners - List of secondary owners and their edit access
     */
    public void createThisBook(final String bookName, final List<SecondaryOwner> secOwners) {
        // Update the member variables to newly given name and owners
        mNewBookName = bookName;
        mSecOwnersList = secOwners;
        mAreSecOwnersValid = false;
        mIsBookNameValid = false;

        // Verify the book name if it already exists
        validateBookNameInFirebase();

        // Verify the secondary owners
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
            saveNewBookInFirebase();
        }

        // Run a loop for every secondary owner
        for (final SecondaryOwner secOwner : mSecOwnersList) {
            // Check if this owner has registered in the app or not
            Log.i(TAG, "createThisBook: Username: "+secOwner.getUsername());

//            // If the owner is already validated, then skip that owner and continue
//            if (secOwner.getValidated() == 1) {
//                mOwnersValidated++;
//                mBookListener.onThisSecOwnerValidated();
//                continue;
//            }

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
                        // Add a key value pair (UID: Access) for this secondary user
                        validSecOwnersMap.put((String) userMap.keySet().toArray()[0], secOwner.getCanEdit());
                    }
                    else {
                        // User is not valid
                        secOwner.setValidated(-1);
                    }
                    mBookListener.onThisSecOwnerValidated();

                    // UI must be told once all items have been checked.
                    // This is done ONLY once for every click on the UI button.
                    // That button is enabled again ONLY after this notification is sent to UI.
                    if (mOwnersValidated == mSecOwnersList.size()) {
                        mBookListener.onAllSecOwnersValidated();
                        mOwnersValidated = 0;
                    }

                    // Once done, check if all the sec owners are valid
                    if (mSecOwnersList.size() == validSecOwnersMap.size()) {
                        mAreSecOwnersValid = true;
                        saveNewBookInFirebase();
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
                    mBookListener.onThisSecOwnerValidated();
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
                            saveNewBookInFirebase();
                            return;
                        }

                        // Get the data from snapshot
                        HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();

                        // Check each book for dupliate name
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            ObjectMapper mapper = new ObjectMapper();
                            Book b = mapper.convertValue(entry.getValue(), Book.class);
                            if (b.getName().toLowerCase().trim().equals(mNewBookName.toLowerCase().trim())) {
                                mBookListener.onBookNameInvalid(AppUtilities.Book.BOOK_EXISTS);
                                return;
                            }
                        }

                        // No duplicate found. Book can be created
                        mIsBookNameValid = true;
                        saveNewBookInFirebase();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.i(TAG, "onCancelled: Message: "+databaseError.getMessage());
                        Log.i(TAG, "onCancelled: Details: "+databaseError.getDetails());
                    }
                });
    }

    /**
     * This method takes the book name and secondary owners
     * and adds them as follows.
     * Save the data in Room DB.
     */
    private void saveNewBookInFirebase() {
        // This works only if both book name and sec owners are valid
        if (!mIsBookNameValid || !mAreSecOwnersValid) {
            return;
        }
        String newBookId = generateRandomBookId(); Log.i(TAG, "saveNewBookInFirebase: Generated Book ID: "+newBookId);
        Book newBook = new Book();
        newBook.setBookId(newBookId);
        newBook.setName(mNewBookName);
        newBook.setOwner(AppUtilities.User.CURRENT_USER.getUid()); long timeNow = new Date().getTime();
        newBook.setCreatedDate(timeNow);
        newBook.setLastUpdatedDate(timeNow);
        newBook.setSecOwners(validSecOwnersMap);
        // TODO: Upload the created book in Firebase
        mfirebaseDB.getReference().child(AppUtilities.Firebase.ALL_BOOKS_NODE) // books
                .child(newBookId)  // new bookId
                .setValue(newBook)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Book saved in firebase
                            mBookListener.onNewBookCreated();
                            updateOwnerInFirebase(newBook);
                        }
                        else {
                            /* Book not saved in firebase */ Log.i(TAG, "onComplete: Error: "+task.getException().getMessage());
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
                .child("profile") // profile
                .child("ownedBooks"); // ownedBooks
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
                                    Log.i(TAG, "onComplete: Owner Data Updated");
                                    updateSecOwnersInFirebase(newBook);
                                }
                                else Log.i(TAG, "onComplete: Owner data not updated: "+task.getException().getMessage());
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
                    .child("sharedBooks") // sharedBooks
                    .child(newBook.getBookId()) // NEW KEY - Created book ID
                    .setValue(entry.getValue())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) Log.i(TAG, "onComplete: Sec Owners Data updated");
                            else Log.i(TAG, "onComplete: Sec owners data not updated: "+task.getException().getMessage());
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

    /**
     * This method retrieved all the book from firebase and
     * displays them in UI
     */
    public void retrievedAllBooksFromFirebase () {
        Log.i(TAG, "retrievedAllBooksFromFirebase: *");
        retrieveOwnedBooks();
        retrievesharedBooks();
    }

    /**
     * Retrieved ONLY owned books of the current user
     * from Firebase
     */
    private void retrieveOwnedBooks() {
        mfirebaseDB.getReference()
                .child(AppUtilities.Firebase.ALL_USERS_NODE) // users
                .child(FirebaseAuth.getInstance().getUid()) // owned UID
                .child("profile") // profile
                .child("ownedBooks") // ownedBooks
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            // Owned books available
                            List<String> ownedBookIds = (ArrayList<String>) dataSnapshot.getValue();
                            ownedBookIds.forEach((bookId) -> {
                                downloadBookWithId(bookId);
                            });
                        }
                        else {
                            // Owned books not available
                            // TODO: Tell UI about no data
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void retrievesharedBooks() {
        mfirebaseDB.getReference()
                .child(AppUtilities.Firebase.ALL_USERS_NODE) // users
                .child(FirebaseAuth.getInstance().getUid()) // owned UID
                .child("sharedBooks") // sharedBooks
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            // Shared books available
                            HashMap<String, Boolean> sharedBooksMap =
                                    (HashMap<String, Boolean>) dataSnapshot.getValue();
                            sharedBooksMap.forEach((bookId, access) -> {
                                downloadBookWithId(bookId);
                            });
                        }
                        else {
                            // Shared books not available
                            // TODO: Tell UI about no data
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    /**
     * Fetches a book from firebase given its book id
     * @param bookId
     */
    private void downloadBookWithId(String bookId) {
        // Fetch the book and add it to shared books list
        mfirebaseDB.getReference()
                .child(AppUtilities.Firebase.ALL_BOOKS_NODE) // books
                .child(bookId) // bookId
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            Book fetchedBook = dataSnapshot.getValue(Book.class);
                            mBookRetrieveListener.onBookDownloaded(fetchedBook);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.i(TAG, "onCancelled: "+databaseError.getMessage());
                    }
                });
    }

    /**
     * Clean up all the member variables and reset
     * their default values.
     */
    public void cleanUpVariables() {
        mIsBookNameValid = false;
        mAreSecOwnersValid = false;
        validSecOwnersMap = null;
        mOwnersValidated = 0;
        mDataRepo = null;
        mBookListener = null;
    }

    // MARK: Setters and listeners
    public void setAddBookListener(BookListener mBookListener) {
        this.mBookListener = mBookListener;
    }

    public void setmIsBookNameValid(Boolean mIsBookNameValid) {
        this.mIsBookNameValid = mIsBookNameValid;
    }

    public void setmBookRetrieveListener(BookListener.Retrieve mBookRetrieveListener) {
        this.mBookRetrieveListener = mBookRetrieveListener;
    }
}
