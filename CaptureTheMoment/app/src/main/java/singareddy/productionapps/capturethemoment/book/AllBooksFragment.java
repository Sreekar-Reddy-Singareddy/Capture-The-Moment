package singareddy.productionapps.capturethemoment.book;

import android.arch.lifecycle.LiveData;
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

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import singareddy.productionapps.capturethemoment.AppUtilities;
import singareddy.productionapps.capturethemoment.R;
import singareddy.productionapps.capturethemoment.models.Book;
import singareddy.productionapps.capturethemoment.models.User;

public class AllBooksFragment extends Fragment implements BookListener.Retrieve {
    private static String TAG = "AllBooksFragment";

    private RecyclerView booksList;
    private AllBooksAdapter mAdapter;
    private BookCRUDViewModel bookCRUDViewModel;
    private List<Book> allBooksData = new ArrayList<>();

    public AllBooksFragment () {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bookCRUDViewModel = ViewModelProviders.of(this).get(BookCRUDViewModel.class);
        bookCRUDViewModel.setmBookRetrieveListener(this);

        View view = inflater.inflate(R.layout.fragment_all_books, container, false);
        booksList = view.findViewById(R.id.all_books_rv_books);
        mAdapter = new AllBooksAdapter(getContext(), allBooksData);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        booksList.setAdapter(mAdapter);
        booksList.setLayoutManager(manager);

        bookCRUDViewModel.retrieveAllBooks();
        return view;
    }


    @Override
    public void onBookDownloaded(Book downloadedBook) {
        allBooksData.add(downloadedBook);
        mAdapter.notifyDataSetChanged();
    }
}
