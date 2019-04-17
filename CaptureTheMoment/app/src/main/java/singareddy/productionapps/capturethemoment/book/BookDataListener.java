package singareddy.productionapps.capturethemoment.book;

import android.arch.lifecycle.LiveData;

import java.util.List;

import singareddy.productionapps.capturethemoment.models.Book;

public interface BookDataListener {
    public void onBookLiveDataRecieved (LiveData<List<Book>> bookLiveData);
}
