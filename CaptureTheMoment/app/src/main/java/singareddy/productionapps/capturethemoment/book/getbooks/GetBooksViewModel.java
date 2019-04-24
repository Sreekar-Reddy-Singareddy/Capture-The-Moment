package singareddy.productionapps.capturethemoment.book.getbooks;

import android.arch.lifecycle.ViewModel;
import android.util.Log;

import java.util.List;

import singareddy.productionapps.capturethemoment.DataRepository;
import singareddy.productionapps.capturethemoment.models.Book;

public class GetBooksViewModel extends ViewModel implements GetBookListener {
    private static String TAG = "GetBooksViewModel";

    private GetBookListener mBookGetBookListenerListener;
    private DataRepository mDataRepo;

    public GetBooksViewModel(DataRepository repository) {
        mDataRepo = repository;
    }

    public void getAllBooks() {
        Log.i(TAG, "getAllBooks: *");
        mDataRepo.setBookGetListener(this);
        mDataRepo.getAllBooks();
    }

    // MARK: Setters and listener methods
    public void setmBookGetBookListenerListener(GetBookListener getBookListener) {
        this.mBookGetBookListenerListener = getBookListener;
    }

    @Override
    public void onBookDownloaded(Book downloadedBook) {
        mBookGetBookListenerListener.onBookDownloaded(downloadedBook);
    }

    @Override
    public void onBookRemoved(String removedBookId) {
        mBookGetBookListenerListener.onBookRemoved(removedBookId);
    }
}