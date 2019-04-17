package singareddy.productionapps.capturethemoment.book;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import java.util.List;

import singareddy.productionapps.capturethemoment.DataRepository;
import singareddy.productionapps.capturethemoment.models.Book;

public class BooksViewModel extends AndroidViewModel implements BookDataListener{
    private static String TAG = "BooksViewModel";

    private DataRepository mDataRepo;
    private BookDataListener mBookDataListener;

    public BooksViewModel(Application application) {
        super(application);
        mDataRepo = DataRepository.getInstance(application);
    }

    public void getAllBooks () {
        mDataRepo.getAllBooks();
    }

    public void setmBookDataListener(BookDataListener mBookDataListener) {
        this.mBookDataListener = mBookDataListener;
        mDataRepo.setmBookDataListener(this);
    }

    @Override
    public void onBookLiveDataRecieved(LiveData<List<Book>> bookLiveData) {
        mBookDataListener.onBookLiveDataRecieved(bookLiveData);
    }
}
