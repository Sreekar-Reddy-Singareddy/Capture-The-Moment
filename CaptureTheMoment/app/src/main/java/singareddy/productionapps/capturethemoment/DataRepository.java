package singareddy.productionapps.capturethemoment;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import singareddy.productionapps.capturethemoment.book.AddBookListener;
import singareddy.productionapps.capturethemoment.book.AddBookWebService;
import singareddy.productionapps.capturethemoment.book.BookDataListener;
import singareddy.productionapps.capturethemoment.models.Book;
import singareddy.productionapps.capturethemoment.models.SecondaryOwner;
import singareddy.productionapps.capturethemoment.models.ShareInfo;
import singareddy.productionapps.capturethemoment.models.User;

import static singareddy.productionapps.capturethemoment.AppUtilities.Book.*;
import static singareddy.productionapps.capturethemoment.AppUtilities.User.*;

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
    private BookDataListener mBookDataListener;
    private LocalDB mLocalDB;
    private Context mContext;
    private LiveData<List<Book>> mAllBooksLive;

    // Current user details are stored in this
    private User user;

    private DataRepository (Context context) {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDB = FirebaseDatabase.getInstance();
        mCurrentUser = mFirebaseAuth.getCurrentUser();
        mLocalDB = LocalDB.getInstance(context);
        mContext = context;
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

    // MARK: Methods that communicate with AddBookWebService.java
    /**
     * This method validates using 2 sources of data.
     * Book name is validated from Room DB.
     * Sec owners are validated from Firebase web service.
     * @param bookName
     * @param secOwners
     */
    public void createThisBook (final String bookName, List<SecondaryOwner> secOwners) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... name) {
                // Create book id with the given name
                String bookId = CURRENT_USER.getUid() + "__" + name[0].toLowerCase().trim();
                Book duplicateBook = mLocalDB.getBookDao().getBookWithId(bookId);
                Log.i(TAG, "doInBackground: "+duplicateBook);
                if (duplicateBook == null) {
                    // No duplicate book exists
                    return BOOK_NAME_VALID;
                }
                else {
                    // Duplicate book exists
                    return BOOK_EXISTS;
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s.equals(BOOK_EXISTS)) {
                    // Tell the UI that book with this name already exists
                    mAddBookListener.onBookNameInvalid(s);
                }
                else if (s.equals(BOOK_NAME_VALID)) {
                    // Proceed further and validate secondary owners
                    if (mAddBookWebService == null) { mAddBookWebService = new AddBookWebService(mContext); }
                    mAddBookWebService.setAddBookListener(DataRepository.this);
                    mAddBookWebService.createThisBook(bookName, secOwners);
                }
            }
        }.execute(bookName);
    }

    /**
     * Cleans up all the unused variables created for
     * adding a new book.
     */
    public void cleanUpVariables() {
        mContext = null;
//        mAddBookWebService.cleanUpVariables();
        mAddBookWebService = null;
    }

    // MARK: Methods that communicate with BookDataWebService.java
    /** STATUS - WORKING
     * This method inserts book into Room DB.
     * @param book: Book to be inserted
     */
    public void insertBookInRoom(final Book book) {
        Log.i(TAG, "insertBookInRoom: *");
        // Redirect control to insert shared info if it an owned book.
        if (CURRENT_USER.getUid().equals(book.getOwner())) {
            // This is an owned book.
            insertAllSharedInfosOfOwnedBook(book);
        }
        // Insert book in Room.
        new InsertBookTask().execute(book);
    }

    /** STATUS - NOT WORKING
     * This method inserts shared info of an owned book into Room.
     * @param book
     */
    private void insertAllSharedInfosOfOwnedBook (Book book) {
        // Owned book may have more than one shared infos
        insertSharedInfo(book.getSecOwners());
    }

    /** STATUS - NOT WORKING
     * Takes a list of shared info objects and inserts them in the Room DB
     * @param shareInfos
     */
    public void insertSharedInfo(final List<ShareInfo> shareInfos) {
        if (shareInfos == null) {
            return;
        }
        // Insert shared info into Room.
        new InsertSharedInfosTask().execute(shareInfos);
    }

    /**
     * Gets all the owned books of the current user.
     */
    public void getAllOwnedBooksOfCurrentUser () {
        Log.i(TAG, "getAllOwnedBooksOfCurrentUser: *");
        Runnable selectRunnable = new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "run: Selecting books on: "+Thread.currentThread().getName());
                List<Book> allBooks = mLocalDB.getBookDao().getAllOwnedBooks(CURRENT_USER.getUid());
                for (Book b : allBooks) {
                    Log.i(TAG, "run: Book ID: "+b.getBookId()+" Owner: "+b.getOwner());
                }
                Log.i(TAG, "run: Books available in local db: "+allBooks.size());
            }
        };
        new Thread(selectRunnable).start();
    }

    /**
     * Gets all the sharedinfos in the local DB.
     */
    public void getAllSharedInfos() {
        Runnable getInfos = new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "run: Getting all infos on: "+Thread.currentThread().getName());
                List<ShareInfo> shareInfos = mLocalDB.getSharedInfoDao().getAllShareInfos();
                for(ShareInfo info:shareInfos){
                    Log.i(TAG, "run: Info: "+info.toString());
                }
            }
        };
        new Thread(getInfos).start();
    }

    /**
     * This method simply erases entire data from Room DB.
     * This happens everytime user logs in or logs out.
     */
    public void eraseRoom() {
        Runnable deleteTask = new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "run: Deleting all data from local DB on: "+Thread.currentThread().getName());
                long code = mLocalDB.getBookDao().deleteAllData();
                long code1 = mLocalDB.getSharedInfoDao().deleteAllData();
                Log.i(TAG, "run: Deleted Books: "+code);
                Log.i(TAG, "run: Deleted Infos: "+code1);
            }
        };
        new Thread(deleteTask).start();
    }

    // MARK:  Methods that communicate with Room DB
    public void getAllBooks () {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                LiveData<List<Book>> bookLiveData = mLocalDB.getBookDao().getAllBooks();
                mBookDataListener.onBookLiveDataRecieved(bookLiveData);
            }
        };
        new Thread(runnable).start();
    }

    // MARK: Setter methods and other listener methods
    public void setAddBookListener(AddBookListener mAddBookListener) {
        this.mAddBookListener = mAddBookListener;
    }

    public void setmBookDataListener(BookDataListener mBookDataListener) {
        this.mBookDataListener = mBookDataListener;
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

    // MARK: Async Tasks
    public class InsertBookTask extends AsyncTask<Book, Void, Long> {
        @Override
        protected Long doInBackground(Book... books) {
            long dbCode = mLocalDB.getBookDao().insert(books[0]);
            return dbCode;
        }

        @Override
        protected void onPostExecute(Long dbCode) {
            super.onPostExecute(dbCode);
            // If the code is valid, then clean up variables
            if (dbCode > 0 && mAddBookWebService != null) {
                mAddBookWebService.cleanUpVariables();
                mAddBookListener.onNewBookCreated();
            }
        }
    }

    public class InsertSharedInfosTask extends AsyncTask<List<ShareInfo>, Void, Integer> {
        @Override
        protected Integer doInBackground(List<ShareInfo>... lists) {
            try {
                for (ShareInfo info : lists[0]) {
                    mLocalDB.getSharedInfoDao().insertShareInfo(info);
                }
                return 1;
            }
            catch (Exception e) {
                return -1;
            }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer == 1 && mAddBookListener != null) {
                mAddBookListener.onNewBookCreated();
            }
            else if (integer == -1 && mAddBookListener != null) {
                mAddBookListener.onBookNameInvalid(BOOK_DB_ERROR);
            }
        }
    }
}
