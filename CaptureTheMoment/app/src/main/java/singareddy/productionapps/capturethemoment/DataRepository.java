package singareddy.productionapps.capturethemoment;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import singareddy.productionapps.capturethemoment.book.AddBookListener;
import singareddy.productionapps.capturethemoment.book.AddBookWebService;
import singareddy.productionapps.capturethemoment.models.Book;
import singareddy.productionapps.capturethemoment.models.SecondaryOwner;
import singareddy.productionapps.capturethemoment.models.User;

/**
 * This class is the single reliable source of
 * entire data communication for the app.
 * Its job is only to deal with the data communication.
 * The logical processing of data is not done here.
 */
public class DataRepository implements AddBookListener {
    private static String TAG = "DataRepository";
    private static DataRepository DATA_REPOSITORY;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDB;
    private FirebaseUser mCurrentUser;
    private AddBookWebService mAddBookWebService;
    private AddBookListener mAddBookListener;
    private LocalDB mLocalDB;

    // Current user details are stored in this
    private User user;

    private DataRepository (Context context) {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDB = FirebaseDatabase.getInstance();
        mCurrentUser = mFirebaseAuth.getCurrentUser();
        mLocalDB = LocalDB.getInstance(context);
    }

    /**
     * Returns a singleton instance of this class
     * @return
     * @param context
     */
    public static DataRepository getInstance(Context context){
        if (DATA_REPOSITORY == null) {
            Log.i(TAG, "getInstance: Creating Instance...");
            DATA_REPOSITORY = new DataRepository(context);
        }
        return DATA_REPOSITORY;
    }

    /**
     * This method validates using 2 sources of data.
     * Book name is validated from Room DB.
     * Sec owners are validated from Firebase web service.
     * @param bookName
     * @param secOwners
     */
    public void createThisBook (String bookName, List<SecondaryOwner> secOwners) {
        // TODO: Validate book name from Room DB

        // Check if the list is empty or null
        if (secOwners == null || secOwners.size() == 0) {
            Log.i(TAG, "createThisBook: The list is empty.");
            return;
        }
        mAddBookWebService = new AddBookWebService();
        mAddBookWebService.setAddBookListener(this);
        mAddBookWebService.createThisBook(secOwners);

    }

    // MARK: Methods that communicate with BookDataWebService.java

    /**
     * This method inserts book into Room DB.
     * @param book: Book to be inserted
     */
    public void insertBookInRoom(Book book) {
        Log.i(TAG, "insertBookInRoom: *");
        // TODO: Insert the book into Room DB
//        mLocalDB.getBookDao().insert(book);
    }

    // MARK: Setter methods and other listener methods
    public void setAddBookListener(AddBookListener mAddBookListener) {
        this.mAddBookListener = mAddBookListener;
    }

    @Override
    public void onBookNameInvalid(String code) {
        mAddBookListener.onBookNameInvalid(code);
    }

    @Override
    public void onAllSecOwnersValidated() {
        Log.i(TAG, "onAllSecOwnersValidated: *");
        mAddBookListener.onAllSecOwnersValidated();
    }

    @Override
    public void onThisSecOwnerValidated() {
        Log.i(TAG, "onThisSecOwnerValidated: *");
        mAddBookListener.onThisSecOwnerValidated();
    }
}
