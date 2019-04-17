package singareddy.productionapps.capturethemoment.book;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import singareddy.productionapps.capturethemoment.AppUtilities;
import singareddy.productionapps.capturethemoment.R;
import singareddy.productionapps.capturethemoment.models.Book;

public class AllBooksFragment extends Fragment implements BookDataListener{
    private static String TAG = "AllBooksFragment";

    private RecyclerView booksList;
    private AllBooksAdapter mAdapter;
    private BooksViewModel booksViewModel;

    public AllBooksFragment () {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        booksViewModel = ViewModelProviders.of(this).get(BooksViewModel.class);
        booksViewModel.setmBookDataListener(this);

        View view = inflater.inflate(R.layout.fragment_all_books, container, false);
        booksList = view.findViewById(R.id.all_books_rv_books);
        mAdapter = new AllBooksAdapter(getContext());
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        booksList.setAdapter(mAdapter);
        booksList.setLayoutManager(manager);

        booksViewModel.getAllBooks();
        return view;
    }

    @Override
    public void onBookLiveDataRecieved(LiveData<List<Book>> bookLiveData) {
        bookLiveData.observe(this, new Observer<List<Book>>() {
            @Override
            public void onChanged(@Nullable List<Book> books) {
                Log.i(TAG, "onChanged: Books: "+books);
                mAdapter.setBookData(books);
                mAdapter.notifyDataSetChanged();
            }
        });
    }
}
