package singareddy.productionapps.capturethemoment.book;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.dynamic.IFragmentWrapper;
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
import singareddy.productionapps.capturethemoment.models.ShareInfo;

public class AddBookWebService {
    private static String TAG = "AddBookWebService";

    private static AddBookWebService SERVICE;
    private FirebaseDatabase mfirebaseDB;
    private List<ShareInfo> validSecOwnersArray = new ArrayList<>();
    private Integer mOwnersValidated;
    private BookListener mBookListener;
    private Boolean mIsBookNameValid = false;
    private Boolean mAreSecOwnersValid = false;
    private DataRepository mDataRepo;

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
        // Get reference of the users node
        DatabaseReference targetDataNode = mfirebaseDB.getReference().child(AppUtilities.Firebase.ALL_REGISTERED_USERS_NODE);
        mOwnersValidated = 0;

        if (secOwners.size() == 0) {
            saveNewBook(bookName, validSecOwnersArray);
        }

        // Run a loop
        for (final SecondaryOwner secOwner : secOwners) {
            // Check if this owner has registered in the app or not
            Log.i(TAG, "createThisBook: Username: "+secOwner.getUsername());

            // If the owner is already validated, then skip that owner and continue
            if (secOwner.getValidated() == 1) {
                mOwnersValidated++;
                continue;
            }

            // If not validated, only then the control reaches here
            Query query = targetDataNode.orderByValue().equalTo(secOwner.getUsername().toLowerCase().trim());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.i(TAG, "onDataChange: Value: "+dataSnapshot.getValue());
                    mOwnersValidated++;

                    // This snapshot MUST be non null.
                    if (dataSnapshot.getValue() != null) {
                        // User is valid
                        Log.i(TAG, "onDataChange: Sec Owner: "+secOwner.getUsername());
                        Map<String, String> userMap = (HashMap<String, String>) dataSnapshot.getValue();
                        ShareInfo info = new ShareInfo();
                        info.setCanEdit(secOwner.getCanEdit());
                        info.setUid((String) userMap.keySet().toArray()[0]);
                        info.setBookId(getBookIdFor(bookName));
                        secOwner.setValidated(1);
                        validSecOwnersArray.add(info);
                        Log.i(TAG, "onDataChange: Sec Owner: "+secOwner.getUsername());
                        Log.i(TAG, "onDataChange: Sec Owner Map: "+userMap);
                    }
                    else {
                        // User is not valid
                        secOwner.setValidated(-1);
                    }
                    mBookListener.onThisSecOwnerValidated();

                    // UI must be told once all items have been checked.
                    // This is done ONLY once for every click on the UI button.
                    // That button is enabled again ONLY after this notification is sent to UI.
                    if (mOwnersValidated == secOwners.size()) {
                        mBookListener.onAllSecOwnersValidated();
                        mOwnersValidated = 0;
                    }
                    
                    // Once done, check if all the sec owners are valid
                    Log.i(TAG, "onDataChange: Original Array : "+secOwners.size());
                    Log.i(TAG, "onDataChange: Valid Owner Arr: "+validSecOwnersArray.size());
                    if (secOwners.size() == validSecOwnersArray.size()) {
                        Log.i(TAG, "onDataChange: ALL OWNERS VALID");
                        saveNewBook(bookName, validSecOwnersArray);
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

//        // Validate book name
//        validateBookNameInFirebase(bookName);
    }

    /**
     * Validates book name from Firebase DB.
     * It checks for duplicate books if already exists.
     * @param bookName
     */
    private void validateBookNameInFirebase (String bookName) {
        // Validate the book name from Firebase
        String bookId = getBookIdFor(bookName);
        DatabaseReference allBooksNode = mfirebaseDB.getReference()
                .child(AppUtilities.Firebase.ALL_BOOKS_NODE)
                .child(bookId);
        ValueEventListener newBookValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    // Book already exists
                    mBookListener.onBookNameInvalid(AppUtilities.Book.BOOK_EXISTS);
                    mIsBookNameValid = false;
                }
                else {
                    // TODO: Book name valid, so proceed further
                    Log.i(TAG, "onDataChange: BOOK NAME VALID");
                    mIsBookNameValid = true;
                    saveNewBook(bookName, validSecOwnersArray);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        allBooksNode.addListenerForSingleValueEvent(newBookValueListener);
    }

    /**
     * Create and returns automatic book id for the new book
     * @param bookName
     * @return
     */
    private String getBookIdFor(String bookName) {
        String bookId = AppUtilities.User.CURRENT_USER.getUid() + "__" + bookName.toLowerCase().trim();
        return bookId;
    }

    /**
     * This method takes the book name and secondary owners
     * and adds them as follows.
     * Save the data in Room DB.
     * @param bookName
     * @param sharedInfos
     */
    public void saveNewBook(String bookName, List<ShareInfo> sharedInfos) {
        // This works only if both book name and sec owners are valid
        String bookId = getBookIdFor(bookName);
        Book newBook = new Book();
        newBook.setBookId(bookId);
        newBook.setName(bookName.trim());
        newBook.setOwner(AppUtilities.User.CURRENT_USER.getUid());
        newBook.setCreatedTime(new Date().getTime());
        newBook.setLastOpenedTime(new Date().getTime());
        newBook.setSecOwners(sharedInfos);
        mDataRepo.insertBookInRoom(newBook);
    }

    /**
     * Clean up all the member variables and reset
     * their default values.
     */
    public void cleanUpVariables() {
        mIsBookNameValid = false;
        mAreSecOwnersValid = false;
        validSecOwnersArray = null;
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
}
