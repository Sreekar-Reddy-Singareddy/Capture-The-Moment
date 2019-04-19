package singareddy.productionapps.capturethemoment.book;

import static singareddy.productionapps.capturethemoment.AppUtilities.Book.*;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import singareddy.productionapps.capturethemoment.AppUtilities;
import singareddy.productionapps.capturethemoment.DataRepository;
import singareddy.productionapps.capturethemoment.models.Book;
import singareddy.productionapps.capturethemoment.models.SecondaryOwner;

public class BookCRUDViewModel extends AndroidViewModel implements BookListener, BookListener.UpdateBook, BookListener.Retrieve {
    private static String TAG = "BookCRUDViewModel";

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mCurrentUser;
    private BookListener mBookListener;
    private BookListener.Retrieve mBookRetrieveListener;
    private DataRepository mDataRepo;
    private String mOldBookId;
    private String mNewBookId;

    public BookCRUDViewModel(Application application) {
        super(application);
        mDataRepo = DataRepository.getInstance(application);
        mFirebaseAuth = FirebaseAuth.getInstance();
        handleAuthState();
    }

    /** STATUS - WORKING
     * This method adds a auth state listener to know
     * the login status of current user, if any.
     * Any initial or clean up actions can be done from here
     */
    private void handleAuthState () {
        FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.i(TAG, "onAuthStateChanged: User is either logged in or logged out");
                mCurrentUser = firebaseAuth.getCurrentUser();
                // Check if user is logged in or logged out
                if (mCurrentUser == null) {
                    // User NOT logged in
                    // TODO: Any initial loading must be done from here
                    Log.i(TAG, "onAuthStateChanged: User logged out");
                }
                else {
                    // User logged in
                    // TODO: Any data erase must be done from here
                    Log.i(TAG, "onAuthStateChanged: User provider: "+mCurrentUser.getProviders().get(0));
                }
            }
        };
        mFirebaseAuth.addAuthStateListener(authStateListener);
    }

    /**
     * This method takes book details from the UI and
     * validates them with the existing data in the Room DB for duplicate book.
     * Also validates all the secondary owner details with the Firebase data.
     * @param bookName: Name of the book given in the UI
     * @param secOwners: List of secondary owner details put in a java object.
     */
    public void createThisBook (String bookName, List<SecondaryOwner> secOwners) {
        // Here I will not do any data related validations.
        // I will redirect that to a special web service.
        // Only simple logical validation will be done here
        String bookValidationCode = isBookNameValid(bookName);
        if (!bookValidationCode.equals(BOOK_NAME_VALID)) {
            mBookListener.onBookNameInvalid(bookValidationCode);
            return;
        }

        // Redirect the other validations to the data repo
        mDataRepo.createThisBook(bookName, secOwners);
        mDataRepo.setAddBookListener(this);

    }

    public void retrieveAllBooks() {
        Log.i(TAG, "retrieveAllBooks: *");
        mDataRepo.setmBookRetrieveListener(this);
        mDataRepo.retrieveAllBooks();
    }

    /**
     * This book validates the updated book details
     * and redirects the control to other classes.
     */
    public void updateThisBook (String oldBookName, String newBookName, List<SecondaryOwner> secOwners) {
        // Initially save the old book's book id for future reference
        mOldBookId = AppUtilities.User.CURRENT_USER.getUid() + "__" + oldBookName.toLowerCase().trim();

        if (oldBookName.toLowerCase().trim().equals(newBookName.toLowerCase().trim())) {
            // Old book name and new book are same
            mBookListener.onBookNameInvalid(BOOK_NAME_SAME_AS_OLD_NAME);
            return;
        }

        // Redirect the control so that other validation can be done
        createThisBook(newBookName, secOwners);
    }

    /**
     * Cleans up all the unused variables created for
     * adding a new book.
     */
    public void cleanUpVariables() {
        mDataRepo.cleanUpVariables();
    }

    /** STATUS - WORKING
     * This method only validates if the book name is valid.
     * It does NOT validate for duplicate book names. That is
     * the job of some other method
     * @return
     */
    private String isBookNameValid (String bookName) {
        bookName = bookName.trim();
        if (bookName == null || bookName.equals("")) {
            return BOOK_NAME_EMPTY;
        }
        else if (!bookName.matches("[a-zA-Z0-9_ ]+")) {
            return BOOK_NAME_INVALID;
        }
        else {
            return BOOK_NAME_VALID;
        }
    }

    // MARK: Setters and listener methods
    public void setAddBookListener(BookListener mBookListener) {
        this.mBookListener = mBookListener;
    }

    public void setmBookRetrieveListener(Retrieve mBookRetrieveListener) {
        this.mBookRetrieveListener = mBookRetrieveListener;
    }

    @Override
    public void onBookNameInvalid(String code) {
        mBookListener.onBookNameInvalid(code);
    }

    @Override
    public void onAllSecOwnersValidated() {
        Log.i(TAG, "onAllSecOwnersValidated: *");
        mBookListener.onAllSecOwnersValidated();
    }

    @Override
    public void onThisSecOwnerValidated() {
        Log.i(TAG, "onThisSecOwnerValidated: *");
        mBookListener.onThisSecOwnerValidated();
    }

    @Override
    public void onNewBookCreated() {
        mBookListener.onNewBookCreated();
    }

    @Override
    public void onBookUpdatedWithNewId(String newBookId) {
        Log.i(TAG, "onBookUpdatedWithNewId: New Book ID: "+newBookId);
        mNewBookId = newBookId;
        // Once the new book has been saved, the old book has to be removed
        // TODO: Ask the data repository to delete the old book from Book DB
        // TODO: Ask the data repo to insert the old book id in TrashBooks DB
    }

    @Override
    public void onBookDownloaded(Book downloadedBook) {
        mBookRetrieveListener.onBookDownloaded(downloadedBook);
    }
}
