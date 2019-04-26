package singareddy.productionapps.capturethemoment;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import singareddy.productionapps.capturethemoment.auth.AuthService;
import singareddy.productionapps.capturethemoment.book.BookListener;
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
import singareddy.productionapps.capturethemoment.auth.AuthenticationListener;

import static singareddy.productionapps.capturethemoment.AppUtilities.Book.*;

/**
 * This class is the single reliable source of
 * entire data communication for the app.
 * Its job is only to deal with the data communication.
 * The logical processing of data is not done here.
 */
public class DataRepository implements AddBookListener, GetBookListener, UpdateBookListener, AuthenticationListener.EmailLogin, AuthenticationListener.Mobile {
    private static String TAG = "DataRepository";
    private static DataRepository DATA_REPOSITORY;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDB;
    private FirebaseUser mCurrentUser;

    private AddBookService mAddBookService;
    private GetBooksService mGetBooksService;
    private UpdateBookService mUpdateBookService;
    private AuthService mAuthService;

    private BookListener mBookListener;
    private GetBookListener mBookGetBookListenerListener;
    private AuthenticationListener.EmailLogin emailLoginListener;
    private AuthenticationListener.Mobile mobileAuthListener;

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

    public SharedPreferences getUsernamesCache(){
        return mContext.getSharedPreferences(AppUtilities.FileNames.UIDS_CACHE, Context.MODE_PRIVATE);
    }

    // =================================================================== Book Module

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

    /**
     * Get all the books of this owner as live data
     * and use it to update the UI.
     * @return
     */
    public LiveData<List<Book>> getAllBooksOfThisUser(){
        try {
            return mExecutor.submit(()-> mLocalDB.getBookDao().getAllBooks(FirebaseAuth.getInstance().getCurrentUser().getUid())).get();
        } catch (ExecutionException | InterruptedException e) {
            Log.i(TAG, "getAllBooksOfThisUser: Error: "+e.getLocalizedMessage());
            return new MutableLiveData<>();
        }
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

    public void updateBook(String bookId, String newName, List<SecondaryOwner> secOwners) {
        if (mUpdateBookService == null) {
            mUpdateBookService = new UpdateBookService();
            mUpdateBookService.setUpdateBookListener(this);
        }
        mUpdateBookService.updateThisBook(bookId, newName, secOwners);
    }

    // =================================================================== Authentication Module

    public void loginUserWithEmail(String email, String password) {
        if (mAuthService == null) {
            mAuthService = new AuthService();
            mAuthService.setEmailLoginListener(this);
        }
        mAuthService.loginUserWithEmail(email, password);
    }

    public void authorizePhoneCredentials(String mobile, String otpCode) {
        if (mAuthService == null) {
            mAuthService = new AuthService();
            mAuthService.setMobileAuthListener(this);
        }
        mAuthService.authorizePhoneCredentials(mobile, otpCode);
    }




    // MARK: Setter methods and other listener methods

    public void setAddBookListener(AddBookListener mAddBookListener) {
        this.mBookListener = mAddBookListener;
    }

    public void setBookGetListener(GetBookListener getBookListener) {
        this.mBookGetBookListenerListener = getBookListener;
    }

    public void setUpdateBookListener(UpdateBookListener updateBookListener) {
        this.mBookListener = updateBookListener;
    }

    public void setEmailLoginListener(AuthenticationListener.EmailLogin emailLoginListener) {
        this.emailLoginListener = emailLoginListener;
    }

    public void setMobileAuthListener(AuthenticationListener.Mobile mobileAuthListener) {
        this.mobileAuthListener = mobileAuthListener;
    }

    // ===================

    @Override
    public void onBookNameInvalid(String code) {
        Log.i(TAG, "onBookNameInvalid: *");
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

    // ===================

    @Override
    public void onNewBookCreated() {
        Log.i(TAG, "onNewBookCreated: *");
        ((AddBookListener) mBookListener).onNewBookCreated();
        // After new book is created there is no need for the service
    }

    // ===================

    @Override
    public void onBookUpdated() {
        ((UpdateBookListener) mBookListener).onBookUpdated();
    }

    // =================== Email Login Listener

    @Override
    public void onEmailUserLoginSuccess() {
        emailLoginListener.onEmailUserLoginSuccess();
    }

    @Override
    public void onEmailUserLoginFailure(String failureCode) {
        emailLoginListener.onEmailUserLoginFailure(failureCode);
    }

    // =================== Mobile Login Listener

    @Override
    public void onMobileAuthenticationSuccess() {
        mobileAuthListener.onMobileAuthenticationSuccess();
    }

    @Override
    public void onMobileAuthenticationFailure(String failureCode) {
        mobileAuthListener.onMobileAuthenticationFailure(failureCode);
    }

    @Override
    public void onOtpSent() {
        mobileAuthListener.onOtpSent();
    }

    @Override
    public void onOtpRetrievalFailed() {
        mobileAuthListener.onOtpRetrievalFailed();
    }

    // MARK: Async Tasks
    public class InsertBookTask extends AsyncTask<Book, Void, Void> {
        @Override
        protected Void doInBackground(Book... books) {
            // Insert this book in Room DB
            Book book = books[0];
            mLocalDB.getBookDao().insert(book);
            Log.i(TAG, "doInBackground: BOOK INSERTED: "+book.getName());

            // If its update, then delete all existing shared infos
            if (mBookListener instanceof UpdateBookListener)
            mLocalDB.getSharedInfoDao().deleteInfosForBook(book.getBookId());

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
            if (integer == 1 && mBookListener != null) {
//                mBookListener.onNewBookCreated();
            }
            else if (integer == -1 && mBookListener != null) {
                mBookListener.onBookNameInvalid(BOOK_DB_ERROR);
            }
        }
    }
}
