package singareddy.productionapps.capturethemoment.book.get;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import singareddy.productionapps.capturethemoment.DataRepository;
import singareddy.productionapps.capturethemoment.DataSyncListener;
import singareddy.productionapps.capturethemoment.models.Book;

public class GetBooksViewModel extends ViewModel implements GetBookListener, DataSyncListener {
    private static String TAG = "GetCardsViewModel";

    private GetBookListener mBookGetBookListenerListener;
    private DataRepository mDataRepo;
    private DataSyncListener dataSyncListener;

    public GetBooksViewModel(DataRepository repository) {
        mDataRepo = repository;
    }

    public LiveData<List<Book>> getAllBooks() {
        Log.i(TAG, "getAllBooks: *");
        return mDataRepo.getAllBooksOfThisUser();
    }

    public List<String> getCardsUnderTheBook(String bookId) {
        return mDataRepo.getCardsUnderTheBook(bookId);
    }

    public String getOneImagePathForCard(String cardId) {
        return mDataRepo.getOneImagePathForCard(cardId);
    }

    public LiveData<String> getCoverPhotoForTheBook(String bookId) {
        return mDataRepo.getCoverPhotoForTheBook(bookId);
    }

    public String getOwnerNameForBook(String bookId) {
        return mDataRepo.getOwnerNameForBook(bookId);
    }

    public void setupBooks() {
        mDataRepo.setupBooks();
    }

    // MARK: Setters and listener methods

    public void setmBookGetBookListenerListener(GetBookListener getBookListener) {
        this.mBookGetBookListenerListener = getBookListener;
    }

    public void setDataSyncListener(DataSyncListener dataSyncListener) {
        this.dataSyncListener = dataSyncListener;
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
    public void shouldStopUILoader() {

    }
}
