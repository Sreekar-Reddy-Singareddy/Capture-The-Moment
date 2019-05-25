package singareddy.productionapps.capturethemoment.book.get;

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

import java.util.ArrayList;
import java.util.List;

import singareddy.productionapps.capturethemoment.R;
import singareddy.productionapps.capturethemoment.models.Book;

public class GetBooksFragment extends Fragment implements GetBookListener {
    private static String TAG = "GetBooksFragment";

    private RecyclerView booksList;
    private GetBooksAdapter mAdapter;
    private GetBooksViewModel getBooksViewModel;
    private List<Book> allBooksData = new ArrayList<>();
    private View mFragmentView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentView = inflater.inflate(R.layout.fragment_all_books, container, false);
        initialiseUI();
        initialiseViewModel();
        return mFragmentView;
    }

    public void initialiseViewModel() {
        GetBooksModelFactory factory = GetBooksModelFactory.createFactory(this.getActivity());
        getBooksViewModel = ViewModelProviders.of(this, factory).get(GetBooksViewModel.class);
        getBooksViewModel.setmBookGetBookListenerListener(this);
        getBooksViewModel.getAllBooks().observe(this, new Observer<List<Book>>() {
            @Override
            public void onChanged(@Nullable List<Book> books) {
                mAdapter.setBookData(books);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    public void initialiseUI() {
        booksList = mFragmentView.findViewById(R.id.all_books_rv_books);
        mAdapter = new GetBooksAdapter(getContext(), allBooksData);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        booksList.setAdapter(mAdapter);
        booksList.setLayoutManager(manager);
    }

    @Override
    public void onBookDownloaded(Book downloadedBook) {
        allBooksData.add(downloadedBook);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBookRemoved(String removedBookId) {
        allBooksData.removeIf((book) -> (book.getBookId().equals(removedBookId)));
        mAdapter.notifyDataSetChanged();

    }
}
