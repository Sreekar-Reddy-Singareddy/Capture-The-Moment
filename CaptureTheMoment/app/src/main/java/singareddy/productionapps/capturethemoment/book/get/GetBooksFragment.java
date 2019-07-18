package singareddy.productionapps.capturethemoment.book.get;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import singareddy.productionapps.capturethemoment.DataSyncListener;
import singareddy.productionapps.capturethemoment.HomeActivity;
import singareddy.productionapps.capturethemoment.R;
import singareddy.productionapps.capturethemoment.models.Book;
import singareddy.productionapps.capturethemoment.user.auth.AuthModelFactory;
import singareddy.productionapps.capturethemoment.user.auth.AuthViewModel;
import singareddy.productionapps.capturethemoment.utils.AppUtilities;

public class GetBooksFragment extends Fragment implements GetBookListener, DataSyncListener {
    private static String TAG = "GetBooksFragment";

    private RecyclerView booksList;
    private RecyclerView.OnScrollListener bookScrollListener;
    private SwipeRefreshLayout refreshLayout;
    private GetBooksAdapter mAdapter;
    private GetBooksViewModel getBooksViewModel;
    private AuthViewModel authViewModel;
    private List<Book> allBooksData = new ArrayList<>();
    private View mFragmentView;
    private SharedPreferences sharedBookOwnersCache;
    private SharedPreferences.OnSharedPreferenceChangeListener sharedBookOwnersChangeListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentView = inflater.inflate(R.layout.fragment_all_books, container, false);
        initialiseViewModel();
        initialiseUI();
        return mFragmentView;
    }

    public void initialiseUI() {
        booksList = mFragmentView.findViewById(R.id.all_books_rv_books);
        bookScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.i(TAG, "onScrolled: 123: "+dy);
                HomeActivity parent = (HomeActivity) getActivity();
                if (dy > 0) {
                    parent.addBookFab.hide();
                }
                else {
                    parent.addBookFab.show();
                }
            }
        };
        refreshLayout = mFragmentView.findViewById(R.id.all_books_srl);
        refreshLayout.setOnRefreshListener(this::refreshBooks);
        mAdapter = new GetBooksAdapter(getContext(), allBooksData, getBooksViewModel);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        booksList.setAdapter(mAdapter);
        booksList.setLayoutManager(manager);
    }

    public void initialiseViewModel() {
        sharedBookOwnersCache = getContext().getSharedPreferences(AppUtilities.FileNames.SHARED_BOOK_OWNERS_CACHE, Context.MODE_PRIVATE);
        GetBooksModelFactory factory = GetBooksModelFactory.createFactory(this.getActivity());
        getBooksViewModel = ViewModelProviders.of(this, factory).get(GetBooksViewModel.class);
        getBooksViewModel.setmBookGetBookListenerListener(this);
        getBooksViewModel.getAllBooks().observe(this, new Observer<List<Book>>() {
            @Override
            public void onChanged(@Nullable List<Book> books) {
                mAdapter.setBookData(books);
                mAdapter.notifyDataSetChanged();
                Log.i(TAG, "onChanged: Book ID *****");
                for (Book book: books) {
                    book.setCards(getBooksViewModel.getCardsUnderTheBook(book.getBookId()));
                    Log.i(TAG, "onChanged: Cards: "+book.getCards());
                    getBooksViewModel.getCoverPhotoForTheBook(book.getBookId()).observe(GetBooksFragment.this,
                            new Observer<String>() {
                                @Override
                                public void onChanged(@Nullable String s) {
                                    Log.i(TAG, "onChanged: Path Acquired: "+s);
                                    mAdapter.notifyDataSetChanged();
                                }
                            });
                }
            }
        });
        AuthModelFactory authModelFactory = AuthModelFactory.createFactory(getActivity());
        authViewModel = ViewModelProviders.of(this, authModelFactory).get(AuthViewModel.class);
        sharedBookOwnersChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Log.i(TAG, "onSharedPreferenceChanged: 156723");
                Log.i(TAG, "onSharedPreferenceChanged: 156723 Book ID: "+key);
                mAdapter.notifyDataSetChanged();
            }
        };
    }

    private void refreshBooks() {
        Log.i(TAG, "refreshBooks: Refreshing books...");
        authViewModel.setDataSyncListener(this);
        authViewModel.eraseLocalData();
        authViewModel.setupInitialData();
    }

    @Override
    public void onResume() {
        super.onResume();
        booksList.addOnScrollListener(bookScrollListener);
        sharedBookOwnersCache.registerOnSharedPreferenceChangeListener(sharedBookOwnersChangeListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        booksList.removeOnScrollListener(bookScrollListener);
        sharedBookOwnersCache.unregisterOnSharedPreferenceChangeListener(sharedBookOwnersChangeListener);
    }

    @Override
    public void onBookDownloaded(Book downloadedBook) {
        allBooksData.add(downloadedBook);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBookRemoved(String removedBookId) {
//        allBooksData.removeIf((book) -> (book.getBookId().equals(removedBookId)));
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void shouldStopUILoader() {
        refreshLayout.setRefreshing(false);
    }
}
