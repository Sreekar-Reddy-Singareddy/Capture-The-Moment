package singareddy.productionapps.capturethemoment;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import singareddy.productionapps.capturethemoment.book.delete.DeleteBookListener;
import singareddy.productionapps.capturethemoment.book.delete.DeleteBookService;
import singareddy.productionapps.capturethemoment.card.delete.DeleteCardListener;
import singareddy.productionapps.capturethemoment.card.delete.DeleteCardService;
import singareddy.productionapps.capturethemoment.card.edit.UpdateCardListener;
import singareddy.productionapps.capturethemoment.card.edit.UpdateCardService;
import singareddy.productionapps.capturethemoment.card.get.SmallCardDownloadListener;
import singareddy.productionapps.capturethemoment.utils.AppUtilities;
import singareddy.productionapps.capturethemoment.card.add.AddCardListener;
import singareddy.productionapps.capturethemoment.card.add.AddCardService;
import singareddy.productionapps.capturethemoment.models.Card;
import singareddy.productionapps.capturethemoment.models.Friend;
import singareddy.productionapps.capturethemoment.models.ImagePath;
import singareddy.productionapps.capturethemoment.user.auth.AuthService;
import singareddy.productionapps.capturethemoment.book.BookListener;
import singareddy.productionapps.capturethemoment.book.add.AddBookListener;
import singareddy.productionapps.capturethemoment.book.edit.UpdateBookListener;
import singareddy.productionapps.capturethemoment.book.edit.UpdateBookService;
import singareddy.productionapps.capturethemoment.book.get.GetBookListener;
import singareddy.productionapps.capturethemoment.book.get.GetBooksService;
import singareddy.productionapps.capturethemoment.book.add.AddBookService;
import singareddy.productionapps.capturethemoment.models.Book;
import singareddy.productionapps.capturethemoment.models.SecondaryOwner;
import singareddy.productionapps.capturethemoment.models.ShareInfo;
import singareddy.productionapps.capturethemoment.models.User;
import singareddy.productionapps.capturethemoment.user.auth.AuthListener;
import singareddy.productionapps.capturethemoment.user.profile.ProfileListener;

import static singareddy.productionapps.capturethemoment.utils.AppUtilities.Book.*;
import static singareddy.productionapps.capturethemoment.utils.AppUtilities.FileNames.*;
import static singareddy.productionapps.capturethemoment.utils.AppUtilities.User.*;

/**
 * This class is the single reliable source of
 * entire data communication for the app.
 * Its job is only to deal with the data communication.
 * The logical processing of data is not done here.
 */
public class DataRepository implements AddBookListener, GetBookListener,
        UpdateBookListener, AuthListener.EmailLogin,
        AuthListener.Mobile, AuthListener.EmailSignup,
        DataSyncListener, ProfileListener,
        AddCardListener, UpdateCardListener, DeleteCardListener, DeleteBookListener,
        SmallCardDownloadListener {

    private static String TAG = "DataRepository";
    private static DataRepository DATA_REPOSITORY;
    private static File INTERNAL_STORAGE;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDB;
    private FirebaseUser mCurrentUser;

    private AddBookService mAddBookService;
    private GetBooksService mGetBooksService;
    private UpdateBookService mUpdateBookService;
    private AuthService mAuthService;
    private AddCardService mAddCardService;
    private UpdateCardService mUpdateCardService;
    private DeleteCardService deleteCardService;
    private DeleteBookService deleteBookService;

    private BookListener mBookListener;
    private GetBookListener mBookGetBookListenerListener;
    private AuthListener.EmailLogin emailLoginListener;
    private AuthListener.Mobile mobileAuthListener;
    private AuthListener.EmailSignup emailSignupListener;
    private ProfileListener profileListener;
    private AddCardListener addCardListener;
    private UpdateCardListener updateCardListener;
    private DeleteCardListener deleteCardListener;
    private DeleteBookListener deleteBookListener;
    private SmallCardDownloadListener smallCardDownloadListener;

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
            INTERNAL_STORAGE = context.getFilesDir();
        }
        CURRENT_USER = FirebaseAuth.getInstance().getCurrentUser();
        if (CURRENT_USER != null) {
            CURRENT_USER_ID = CURRENT_USER.getUid();
            LOGIN_PROVIDER = CURRENT_USER.getProviders().get(0);
            if (LOGIN_PROVIDER.equals(AppUtilities.Firebase.EMAIL_PROVIDER))
                CURRENT_USER_EMAIL = CURRENT_USER.getEmail();
            else CURRENT_USER_MOBILE = CURRENT_USER.getPhoneNumber().substring(3);
        }
        initialiseSettings(context);
        return DATA_REPOSITORY;
    }

    private static void initialiseSettings(Context context) {
        SharedPreferences settings = context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
        String currentTheme = settings.getString(
                context.getString(R.string.theme_change_key),
                context.getString(R.string.theme_change_value_orange));
        if (currentTheme.equals(context.getString(R.string.theme_change_value_orange))) {
            AppUtilities.CURRENT_THEME = R.style.ThemeOrange;
        }
        else if (currentTheme.equals(context.getString(R.string.theme_change_value_blue))) {
            AppUtilities.CURRENT_THEME = R.style.ThemeBlue;
        }
    }

    public static File getInternalStorageRef () {
        return INTERNAL_STORAGE;
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
        }
        mAddBookService.setBookListener(this);
        mAddBookService.createThisBook(bookName, secOwners);
    }

    /**
     * Get all the books of this owner as live data
     * and use it to update the UI.
     * @return
     */
    public LiveData<List<Book>> getAllBooksOfThisUser(){
        try {
            return mExecutor.submit(()-> mLocalDB.getBookDao().getAllBooks()).get();
        } catch (ExecutionException | InterruptedException e) {
            Log.i(TAG, "getAllBooksOfThisUser: Error: "+e.getLocalizedMessage());
            return new MutableLiveData<>();
        }
    }

    public LiveData<List<ShareInfo>> getSecondaryOwners(String bookId) {
        try{
            return mExecutor.submit(()->mLocalDB.getSharedInfoDao().getShareInfoForBookWithId(bookId)).get();
        }
        catch (Exception e) {
            Log.i(TAG, "getSecondaryOwners: "+e.getLocalizedMessage());
            return null;
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

    public void updateBook(String bookId, Boolean sameBookName, String newName, List<SecondaryOwner> activeOwners, List<SecondaryOwner> removedOwners) {
        if (mUpdateBookService == null) {
            mUpdateBookService = new UpdateBookService();
        }
        mUpdateBookService.setUpdateBookListener(this);
        mUpdateBookService.setDataSyncListener(this);
        mUpdateBookService.updateThisBook(bookId, sameBookName, newName, activeOwners, removedOwners);
    }

    public Integer getNumberOfSharedBooks() {
        try {
            Integer number = mExecutor.submit(()->mLocalDB.getBookDao().getNumberOfSharedBooks(CURRENT_USER_ID)).get();
            Log.i(TAG, "getNumberOfSharedBooks: "+number);
            return number;
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Integer getNumberOfOwnedBooks() {
        try {
            Integer number = mExecutor.submit(()->mLocalDB.getBookDao().getNumberOfOwnedBooks(CURRENT_USER_ID)).get();
            Log.i(TAG, "getNumberOfOwnedBooks: "+number);
            return number;
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void deleteBook(String bookId) {
        if (deleteBookService == null) deleteBookService = new DeleteBookService();
        List<String> secOwnerUids = getSecondaryOwnersOfThisBook(bookId);
        List<String> allCardIds = getAllCardIdsOfThisBook(bookId);
        deleteBookService.setDeleteBookListener(this);
        deleteBookService.deleteBookFromServer(bookId, secOwnerUids, allCardIds);

        // Another process to delete from local db
        deleteBookFromLocalDB(bookId);
    }

    private List<String> getAllCardIdsOfThisBook(String bookId) {
        try {
            return mExecutor.submit(() -> mLocalDB.getCardDao().getAllCardIdsUnderBook(bookId)).get();
        }
        catch (Exception e) {
            Log.i(TAG, "getSecondaryOwnersOfThisBook: "+e.getLocalizedMessage());
            return null;
        }
    }

    private List<String> getSecondaryOwnersOfThisBook(String bookId) {
        try {
            return mExecutor.submit(() -> mLocalDB.getSharedInfoDao().getSecOwnerUidsOf(bookId)).get();
        }
        catch (Exception e) {
            Log.i(TAG, "getSecondaryOwnersOfThisBook: "+e.getLocalizedMessage());
            return null;
        }
    }

    private void deleteBookFromLocalDB(String bookId) {
        try {
            mExecutor.submit(() -> {
               // Delete all images under each card under this book
               List<String> allCardIds = mLocalDB.getCardDao().getAllCardIdsUnderBook(bookId);
               for (String cardId : allCardIds) {
                   File cardFolder = new File(mContext.getFilesDir(), "/"+CURRENT_USER_ID+"/"+cardId);
                   Boolean deleted = cardFolder.delete();
                   Log.i(TAG, "Images of "+cardId+" deleted = "+deleted);
               }

               // Delete all cards under this book
                long deletedCards = mLocalDB.getCardDao().deleteAllCardsUnderBook(bookId);
                Log.i(TAG, "Cards Deleted = "+deletedCards);

                // Delete the book itself
                int booksDeleted = mLocalDB.getBookDao().deleteBook(bookId);
                Log.i(TAG, "Books Deleted = "+booksDeleted);

            });
        }
        catch (Exception e) {
            Log.i(TAG, "deleteBookFromLocalDB: Some Error Deleting Book: "+e.getLocalizedMessage());
        }
    }

    // =================================================================== Authentication Module
    public void registerEmailUser(String email, String password) {
        if (mAuthService == null) {
            mAuthService = new AuthService();
        }
        mAuthService.setEmailSignupListener(this);
        mAuthService.registerEmailUser(email, password);
    }

    public void loginUserWithEmail(String email, String password) {
        if (mAuthService == null) {
            mAuthService = new AuthService();
        }
        mAuthService.setEmailLoginListener(this);
        mAuthService.loginUserWithEmail(email, password);
    }

    public void authorizePhoneCredentials(String mobile, String otpCode) {
        if (mAuthService == null) {
            mAuthService = new AuthService();
            mAuthService.setMobileAuthListener(this);
        }
        mAuthService.authorizePhoneCredentials(mobile, otpCode);
    }

    public void sendPasswordResetEmail(String email) {
        if (mAuthService == null) mAuthService = new AuthService();
        mAuthService.setEmailLoginListener(this);
        mAuthService.sendPasswordResetEmail(email);
    }

    public void eraseLocalData() {
        eraseUserProfile();
        eraseBooks();
        eraseCards();
        eraseCardImages();
    }

    private void eraseCardImages() {
        Log.i(TAG, "eraseCardImages: UID: "+CURRENT_USER_ID);
        File file = mContext.getFilesDir();
//        try {
//            FileUtils.cleanDirectory(file);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void eraseCards() {
        try {
            mExecutor.submit(() -> {
                int a = mLocalDB.getCardDao().eraseAllCardData();
                int b = mLocalDB.getCardDao().eraseAllFriendData();
                int c = mLocalDB.getCardDao().eraseAllImagePathData();
                Log.i(TAG, "eraseCards: Cards Erased: "+a);
                Log.i(TAG, "eraseCards: Friends Erased: "+b);
                Log.i(TAG, "eraseCards: Paths Erased: "+c);
            });
        }
        catch (Exception e) {

        }
    }

    private void eraseBooks() {
        try {
            Object obj = mExecutor.submit(()->{
                int booksDeleted = mLocalDB.getBookDao().deleteAllData();
                int infosDeleted = mLocalDB.getSharedInfoDao().deleteAllData();
                Log.i(TAG, "eraseBooks: Books deleted: "+booksDeleted);
                Log.i(TAG, "eraseBooks: Infos deleted: "+infosDeleted);
            }).get();
            Log.i(TAG, "eraseBooks: Returned object: "+obj);

        }
        catch (InterruptedException | ExecutionException e) {
            Log.i(TAG, "eraseBooks: Error deleting data: "+e.getLocalizedMessage());
        }
    }

    private boolean eraseUserProfile() {
        SharedPreferences userProfileCache =
                mContext.getSharedPreferences(USER_PROFILE_CACHE, Context.MODE_PRIVATE);
        boolean committed = userProfileCache.edit().clear().commit();
        Log.i(TAG, "eraseUserProfile: User profile erased: "+committed);
        // Erase user's profile pic files
        File profilePic = new File(mContext.getFilesDir(), "profile_pic.jpg");
        if (profilePic.exists()) Log.i(TAG, "eraseUserProfile: Profile Pic Erased: "+profilePic.delete());
        return committed;
    }

    public void setupInitialData() {
        if (mAuthService == null) {
            mAuthService = new AuthService();
        }
        mAuthService.setDataSyncListener(this);
        mAuthService.setProfileListener(this);
        mAuthService.setupInitialData();
    }

    public void setupUserProfile() {
        if(mAuthService == null) {
            mAuthService = new AuthService();
        }
        mAuthService.setDataSyncListener(this);
        mAuthService.setProfileListener(this);
        mAuthService.setupUserProfile();
    }

    public SharedPreferences getUserProfileData() {
        SharedPreferences userProfileCache =
                mContext.getSharedPreferences(USER_PROFILE_CACHE,  Context.MODE_PRIVATE);
        return userProfileCache;
    }

    public void updateUserProfile(User userProfileToUpdate) {
        if (mAuthService == null) {
            mAuthService = new AuthService();
        }
        mAuthService.setProfileListener(this);
        mAuthService.setDataSyncListener(this);
        mAuthService.updateUserProfile(userProfileToUpdate);
    }

    public void saveProfilePic(Uri profilePicUri) {
        if (mAuthService == null) {
            mAuthService = new AuthService();
        }
        mAuthService.setProfileListener(this);
        mAuthService.saveProfilePic(profilePicUri);
    }

    public Uri whereToSaveProfilePic() {
        File profilePic = new File(mContext.getFilesDir(), "profile_pic.jpg");
        return Uri.fromFile(profilePic);
    }

    // =================================================================== Card Module
    public void createNewCard(Card newCard, List<Uri> imageUris) {
        if (mAddCardService == null) {
            mAddCardService = new AddCardService();
        }
        mAddCardService.setAddCardListener(this);
        mAddCardService.setDataSyncListener(this);
        mAddCardService.createNewCard(newCard, imageUris);
    }

    public LiveData<List<Card>> getAllCardsFor(String bookId) {
        try {
            Log.i(TAG, "getAllCardsFor: *");
            if (mAuthService == null) mAuthService = new AuthService();
            mAuthService.setSmallCardDownloadListener(this);
            return mExecutor.submit(() -> mLocalDB.getCardDao().getAllCardsUnderBook(bookId)).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getOneImagePathForCard(String cardId) {
        try {
            return mExecutor.submit(()->mLocalDB.getCardDao().getOneImagePathForCard(cardId)).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public LiveData<Card> getCardWithId(String cardId) {
        try {
            return mExecutor.submit(() -> mLocalDB.getCardDao().getCardWithId(cardId)).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getImagePathsForCardWithId(String cardId) {
        try {
            return mExecutor.submit(() -> mLocalDB.getCardDao().getImagePathsForCard(cardId)).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Boolean getCurrentUserEditAccessForThisBook(String bookId, String currentUserId) {
        try {
            String owner = mExecutor.submit(() -> mLocalDB.getBookDao().getOwnerOf(bookId)).get();
            if (owner.equals(currentUserId)) return true;
            mExecutor.submit(() -> mLocalDB.getSharedInfoDao().getShareInfoForBookWithId(bookId, currentUserId)).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void saveTheChangesOfCard(Card cardToEdit, List<Uri> activePhotoUris, List<String> removedPhotoPaths) {
        if (mUpdateCardService == null) mUpdateCardService = new UpdateCardService();
        mUpdateCardService.setDataSyncListener(this);
        mUpdateCardService.setUpdateCardListener(this);
        mUpdateCardService.saveTheChangesOfCard(cardToEdit, activePhotoUris, removedPhotoPaths);
    }

    public void deleteCardWithId(Card card) {
        if (deleteCardService == null) deleteCardService = new DeleteCardService();
        deleteCardService.setDeleteCardListener(this);
        deleteCardService.deleteCardWithId(card);
    }

    // =================== Setters

    public void setAddBookListener(AddBookListener mAddBookListener) {
        this.mBookListener = mAddBookListener;
    }

    public void setBookGetListener(GetBookListener getBookListener) {
        this.mBookGetBookListenerListener = getBookListener;
    }

    public void setUpdateBookListener(UpdateBookListener updateBookListener) {
        this.mBookListener = updateBookListener;
    }

    public void setEmailSignupListener(AuthListener.EmailSignup emailSignupListener) {
        this.emailSignupListener = emailSignupListener;
    }

    public void setEmailLoginListener(AuthListener.EmailLogin emailLoginListener) {
        this.emailLoginListener = emailLoginListener;
    }

    public void setMobileAuthListener(AuthListener.Mobile mobileAuthListener) {
        this.mobileAuthListener = mobileAuthListener;
    }

    public void setProfileListener(ProfileListener profileListener) {
        this.profileListener = profileListener;
    }

    public void setAddCardListener(AddCardListener addCardListener) {
        this.addCardListener = addCardListener;
    }

    public void setUpdateCardListener(UpdateCardListener updateCardListener) {
        this.updateCardListener = updateCardListener;
    }

    public void setDeleteCardListener(DeleteCardListener deleteCardListener) {
        this.deleteCardListener = deleteCardListener;
    }

    public void setDeleteBookListener(DeleteBookListener deleteBookListener) {
        this.deleteBookListener = deleteBookListener;
    }

    public void setSmallCardDownloadListener(SmallCardDownloadListener smallCardDownloadListener) {
        this.smallCardDownloadListener = smallCardDownloadListener;
    }

    // =================== Book Listeners
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

//    @Override

//    public void onBookDownloaded(Book downloadedBook) {

//        mBookGetBookListenerListener.onBookDownloaded(downloadedBook);

//    }

    @Override
    public void onBookRemoved(String removedBookId) {
        mBookGetBookListenerListener.onBookRemoved(removedBookId);
    }

    @Override
    public void hasToSaveUidInCache(Map<String, String> userMap) {
        Log.i(TAG, "hasToSaveUidInCache: *");
        // Save UIDs in shared preferences
        SharedPreferences preferences = mContext.getSharedPreferences(AppUtilities.FileNames.UIDS_CACHE, Context.MODE_PRIVATE);
        Log.i(TAG, "hasToSaveUidInCache: Preferences: "+preferences);
        SharedPreferences.Editor editor = preferences.edit();
        for (Map.Entry<String, String> entry: userMap.entrySet()) {
            Log.i(TAG, "hasToSaveUidInCache: **");
            editor.putString(entry.getKey(), entry.getValue());
            editor.commit();
        }
    }


    // =================== Add Book Listener
    @Override
    public void onNewBookCreated() {
        Log.i(TAG, "onNewBookCreated: *");
        ((AddBookListener) mBookListener).onNewBookCreated();
        // After new book is created there is no need for the service
    }


    // =================== Update Book Listener
    @Override
    public void onBookUpdated() {
        ((UpdateBookListener) mBookListener).onBookUpdated();
    }

    @Override
    public void hasToSaveBookInCache(Book book) {
        // This book has to be saved in Room DB
        new InsertBookTask().execute(book);
    }

    // =================== Email Signup Listener

    @Override
    public void onEmailUserRegisterSuccess(String email) {
        emailSignupListener.onEmailUserRegisterSuccess(email);
    }

    @Override
    public void onEmailUserRegisterFailure(String email, String failureCode) {
        emailSignupListener.onEmailUserRegisterFailure(email, failureCode);
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

    @Override
    public void onPasswordResetMailSent(String email) {
        emailLoginListener.onPasswordResetMailSent(email);
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

    // =================== Profile Listener

    @Override
    public void onProfileUpdated() {
        profileListener.onProfileUpdated();
    }

    @Override
    public void onProfilePicUpdated() {
        profileListener.onProfilePicUpdated();
    }

    // =================== Data Sync Listener

    @Override
    public void onUserProfileDownloaded(User currentUserProfile) {
        Log.i(TAG, "onUserProfileDownloaded: USER: "+currentUserProfile.getName());
        SharedPreferences userProfileCache = mContext.getSharedPreferences(USER_PROFILE_CACHE, Context.MODE_PRIVATE);
        Log.i(TAG, "onUserProfileDownloaded: USER PROFILE: "+userProfileCache);
        SharedPreferences.Editor editor = userProfileCache.edit();
        editor.putString("name", currentUserProfile.getName());
        editor.putString("about", currentUserProfile.getAbout());
        editor.putString("email",currentUserProfile.getEmailId());
        editor.putString("gender",currentUserProfile.getGender());
        editor.putString("location",currentUserProfile.getLocation());
        editor.putString("profilePic",currentUserProfile.getProfilePic());
        editor.putLong("mobile",currentUserProfile.getMobile());
        editor.putInt("age",currentUserProfile.getAge());
        boolean committed = editor.commit();
        Log.i(TAG, "onUserProfileDownloaded: COMMITTED: "+committed);
    }

    @Override
    public void onProfilePictureDownloaded() {
        Log.i(TAG, "onProfilePictureDownloaded: **");
        SharedPreferences userProfileCache = mContext.getSharedPreferences(USER_PROFILE_CACHE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userProfileCache.edit();
        editor.putBoolean("profilePicAvailable", true);
        editor.commit();
    }

    @Override
    public void onBookDownloadedFromFirebase(Book downloadedBook, Boolean sharedBookAccess) {
        Log.i(TAG, "onBookDownloadedFromFirebase: BOOK NAME: "+downloadedBook.getName());
        new InsertBookTask().execute(downloadedBook, sharedBookAccess);
    }

    @Override
    public void hasToRemoveSecOwnerFromRoomDB(String bookId, String uid) {
        try {
            mExecutor.submit(()->mLocalDB.getSharedInfoDao().deleteInfoFor(bookId, uid));
        }
        catch (Exception e) {
            Log.i(TAG, "hasToRemoveSecOwnerFromRoomDB: "+e.getLocalizedMessage());
        }
    }

    @Override
    public void hasToCleanUpUnwantedCardData(Card card, List<String> removedImagePaths) {
        // Delete the files from device
        for (String path: removedImagePaths) {
            File file = new File(mContext.getFilesDir(), "/"+path);
            Log.i(TAG, "hasToCleanUpUnwantedCardData: File Deleted: "+file.delete());
        }
    }

    @Override
    public void onCardDownloadedFromFirebase(Card card, List<Uri> imageUris) {
        // Save images in the internal storage
        if (imageUris != null) saveImagesInInternalStorage(card.getImagePaths(), imageUris);
        // Save card into Card DB
        saveCardInLocalDB(card);
        // Save people into Friend DB
        saveCardPeopleInLocalDB(card);
        // Save image paths into ImagePath DB
        saveImagePathsInLocalDB(card);
    }

    private void saveImagePathsInLocalDB(Card card) {
        try {
            int deletedPaths = mExecutor.submit(()->mLocalDB.getCardDao().deleteAllPathsOfCard(card.getCardId())).get();
            Log.i(TAG, "saveImagePathsInLocalDB: DELETED PATHS = "+deletedPaths);
            for (String path: card.getImagePaths()) {
                ImagePath imagePath = new ImagePath(card.getCardId(), path);
                Long code = mExecutor.submit(() -> mLocalDB.getCardDao().insertImagePath(imagePath)).get();
                Log.i(TAG, "saveImagePathsInLocalDB: IMAGE PATH INSERTED: "+code);
            }
        }catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void saveCardPeopleInLocalDB(Card card) {
        try {
            List<String> friends = card.getFriends();
            if (friends == null) return;
            long deletedFriends = mExecutor.submit(()->mLocalDB.getCardDao().deleteAllFriendsOfCard(card.getCardId())).get();
            Log.i(TAG, "saveCardPeopleInLocalDB: DELETED FRIEDS = "+deletedFriends);
            for (String friendName : card.getFriends()) {
                Friend friend = new Friend(card.getCardId(), friendName);
                Long code = mExecutor.submit(() -> mLocalDB.getCardDao().insertFriend(friend)).get();
                Log.i(TAG, "saveCardPeopleInLocalDB: FRIEND INSERTED: "+code);
            }
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void saveCardInLocalDB(Card card) {
        try {
            Long code = mExecutor.submit(()->mLocalDB.getCardDao().insertCard(card)).get();
            Log.i(TAG, "saveCardInLocalDB: CARD INSERTED: "+code);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void saveImagesInInternalStorage(List<String> imagePaths, List<Uri> imageUris) {
        Log.i(TAG, "saveImagesInInternalStorage: *");
        // Create a directory for this user if it does not exist already
        File userDir = new File(mContext.getFilesDir(), "/" + CURRENT_USER_ID);
        if (!userDir.exists()) userDir.mkdir();

        long start = System.nanoTime();
        for (int position=0; position<imagePaths.size(); position++) {
            String[] paths = imagePaths.get(position).split("/");
            Uri uri = imageUris.get(position);
            if (uri.getPath().contains(CURRENT_USER_ID)) continue;
            Log.i(TAG, "saveImagesInInternalStorage: This URI has to be stored in internal storage");
            try {
                File cardDir = new File(userDir,  "/" + paths[1]);
                if (!cardDir.exists()) cardDir.mkdirs();
                File imageFile = new File(cardDir, "/" + paths[2]);
                if (!imageFile.exists()) imageFile.createNewFile();
                FileOutputStream outputStream = new FileOutputStream(imageFile);
                byte [] imageData = IOUtils.toByteArray(mContext.getContentResolver().openInputStream(uri));
                outputStream.write(imageData);
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        long end = System.nanoTime();
        Log.i(TAG, "Time Taken: "+(end-start));

    }

    // =================== Add Card Listener

    @Override
    public void onCardCreated() {
        addCardListener.onCardCreated();
    }

    // =================== Update Card Listener

    @Override
    public void onCardUpdated() {
        updateCardListener.onCardUpdated();
    }

    // =================== Delete Card Listener

    @Override
    public void onCardDeleted(String cardId) {
        try{
            mExecutor.submit(()->{
                mLocalDB.getCardDao().deleteAllFriendsOfCard(cardId);
                mLocalDB.getCardDao().deleteAllPathsOfCard(cardId);
                mLocalDB.getCardDao().deleteCard(cardId);
            });
        }
        catch (Exception e) {
            Log.i(TAG, "onCardDeleted: Error: "+e.getLocalizedMessage());
        }
        deleteCardListener.onCardDeleted(cardId);
    }

    // =================== Delete Book Listener

    @Override
    public void onBookDeleted() {
        deleteBookListener.onBookDeleted();
    }

    // =================== Small Card Download Listener

    @Override
    public void onSmallCardDownloaded() {
        smallCardDownloadListener.onSmallCardDownloaded();
    }

    // MARK: Async Tasks
    public class InsertBookTask extends AsyncTask<Object, Void, Void> {
        @Override
        protected Void doInBackground(Object... objects) {
            // Insert this book in Room DB
            Book book = (Book) objects[0];
            mLocalDB.getBookDao().insert(book);
            Log.i(TAG, "doInBackground: BOOK INSERTED: "+book.getName());

            // Decide if its OWNED or SHARED
            if (book.getOwner().equals(CURRENT_USER_ID)) {
                Log.i(TAG, "doInBackground: OWNED BOOK");
                // OWNED
                for (Map.Entry<String, Boolean> entry : book.getSecOwners().entrySet()) {
                    ShareInfo info = new ShareInfo();
                    info.setBookId(book.getBookId());
                    info.setUid(entry.getKey());
                    info.setCanEdit(entry.getValue());
                    mLocalDB.getSharedInfoDao().insertShareInfo(info);
                }
            }
            else {
                Log.i(TAG, "doInBackground: SHARED BOOK");
                // SHARED
                Boolean sharedBookAccess = (Boolean) objects[1];
                ShareInfo info = new ShareInfo();
                info.setBookId(book.getBookId());
                info.setUid(CURRENT_USER_ID);
                info.setCanEdit(sharedBookAccess);
                mLocalDB.getSharedInfoDao().insertShareInfo(info);
            }
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
