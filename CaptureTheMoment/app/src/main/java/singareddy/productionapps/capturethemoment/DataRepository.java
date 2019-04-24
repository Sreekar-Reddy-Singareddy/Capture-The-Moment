package singareddy.productionapps.capturethemoment;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import singareddy.productionapps.capturethemoment.book.addbook.AddBookListener;
import singareddy.productionapps.capturethemoment.book.details.UpdateBookListener;
import singareddy.productionapps.capturethemoment.book.details.UpdateBookService;
import singareddy.productionapps.capturethemoment.book.getbooks.GetBookListener;
import singareddy.productionapps.capturethemoment.book.getbooks.GetBooksService;
import singareddy.productionapps.capturethemoment.book.addbook.AddBookService;
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
public class DataRepository implements AddBookListener, GetBookListener {
    private static String TAG = "DataRepository";
    private static DataRepository DATA_REPOSITORY;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDB;
    private FirebaseUser mCurrentUser;
    private AddBookService mAddBookService;
    private GetBooksService mGetBooksService;
    private UpdateBookService mUpdateBookService;
    private AddBookListener mAddBookListener;
    private GetBookListener mBookGetBookListenerListener;
    private UpdateBookListener updateBookListener;
    private LocalDB mLocalDB;
    private Context mContext;
    private LiveData<List<Book>> mAllBooksLive;
    private ExecutorService mExecutor;

    // Current user details are stored in this
    private User user;

    private DataRepository(Context context, ExecutorService executorService) {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDB = FirebaseDatabase.getInstance();
        mCurrentUser = mFirebaseAuth.getCurrentUser();
        mLocalDB = LocalDB.getInstance(context);
        mContext = context;
        mExecutor = executorService;
    }

    /**
     * Returns a singleton instance of this class
     * @return
     * @param context
     */
    public static DataRepository getInstance(Context context){
        if (DATA_REPOSITORY == null) {
            Log.i(TAG, "getInstance: Creating Instance...");
            DATA_REPOSITORY = new DataRepository(context, Executors.newSingleThreadExecutor());
        }
        return DATA_REPOSITORY;
    }

    // MARK: Methods that communicate with GetBooksService.java
    /**
     * This method validates using 2 sources of data.
     * Book name is validated from Room DB.
     * Sec owners are validated from Firebase web service.
     * @param bookName
     * @param secOwners
     */
    public void createThisBook (final String bookName, List<SecondaryOwner> secOwners) {
        if (mAddBookService == null) {
            mAddBookService = new AddBookService();
            mAddBookService.setBookListener(this);
        }
        mAddBookService.createThisBook(bookName, secOwners);
    }

    public void getAllBooks(){
        if (mGetBooksService == null) {
            mGetBooksService = new GetBooksService(mContext);
            mGetBooksService.setGetBookListener(this);
        }
        mGetBooksService.getAllBooksFromFirebase();
    }

    public Book getBookDetailsFor(String bookId) {
        Log.i(TAG, "getBookDetailsFor: BOOK_DETAILS_FOR: "+bookId);
        try {
            return mExecutor.submit(() -> mLocalDB.getBookDao().getBookWithId(bookId)).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<SecondaryOwner> getUsernamesFor(String bookId) {
        try {
            List<ShareInfo> infos = mExecutor.submit(()->mLocalDB.getSharedInfoDao().getShareInfoForBookWithId(bookId)).get();
            List<SecondaryOwner> ownerList = new ArrayList<>();
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(AppUtilities.FileNames.UIDS_CACHE, Context.MODE_PRIVATE);
            // Use this file to get the info of usernames
            infos.forEach((info) -> {
                Log.i(TAG, "getUsernamesFor: GET_SEC_OWNER");
                SecondaryOwner owner = new SecondaryOwner();
                owner.setUsername(sharedPreferences.getString(info.getUid(), ""));
                owner.setValidated(1);
                owner.setCanEdit(info.getCanEdit());
                ownerList.add(owner);
            });
            return ownerList;
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * Cleans up all the unused variables created for
     * adding a new book.
     */
    public void cleanUpVariables() {
        mContext = null;
        mAddBookService = null;
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
//        insertSharedInfo(book.getSecOwners());
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

    // MARK: Setter methods and other listener methods
    public void setAddBookListener(AddBookListener mAddBookListener) {
        this.mAddBookListener = mAddBookListener;
    }

    public void setBookGetListener(GetBookListener getBookListener) {
        this.mBookGetBookListenerListener = getBookListener;
    }

    public void setUpdateBookListener(UpdateBookListener updateBookListener) {
        this.updateBookListener = updateBookListener;
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

    @Override
    public void onNewBookCreated() {
        mAddBookListener.onNewBookCreated();
    }

    @Override
    public void onBookDownloaded(Book downloadedBook) {
        mBookGetBookListenerListener.onBookDownloaded(downloadedBook);
    }

    @Override
    public void onBookRemoved(String removedBookId) {
        mBookGetBookListenerListener.onBookRemoved(removedBookId);
    }

    @Override
    public void hasToSaveBookInCache(Book book) {
        // This book has to be saved in Room DB
        new InsertBookTask().execute(book);
    }

    @Override
    public void hasToSaveUidInCache(Map<String, String> userMap) {
        Log.i(TAG, "hasToSaveUidInCache: *");
        // Save UIDs in shared preferences
        SharedPreferences preferences = mContext.getSharedPreferences(AppUtilities.FileNames.UIDS_CACHE, Context.MODE_PRIVATE);
        Log.i(TAG, "hasToSaveUidInCache: Preferences: "+preferences);
        SharedPreferences.Editor editor = preferences.edit();
        userMap.forEach((uid, username) -> {
            Log.i(TAG, "hasToSaveUidInCache: **");
            editor.putString(uid, username);
            editor.commit();
        });
    }

    // MARK: Async Tasks
    public class InsertBookTask extends AsyncTask<Book, Void, Void> {
        @Override
        protected Void doInBackground(Book... books) {
            Book book = books[0];
            mLocalDB.getBookDao().insert(book);
            book.getSecOwners().forEach((uid, editAccess) -> {
                ShareInfo info = new ShareInfo();
                info.setBookId(book.getBookId());
                info.setUid(uid);
                info.setCanEdit(editAccess);
                mLocalDB.getSharedInfoDao().insertShareInfo(info);
            });
            return null;
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
