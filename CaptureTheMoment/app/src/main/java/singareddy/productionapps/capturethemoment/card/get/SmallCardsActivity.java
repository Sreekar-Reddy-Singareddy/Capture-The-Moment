package singareddy.productionapps.capturethemoment.card.get;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import singareddy.productionapps.capturethemoment.R;
import singareddy.productionapps.capturethemoment.book.delete.DeleteBookListener;
import singareddy.productionapps.capturethemoment.book.delete.DeleteBookModelFactory;
import singareddy.productionapps.capturethemoment.book.delete.DeleteBookViewModel;
import singareddy.productionapps.capturethemoment.book.edit.EditBookActivity;
import singareddy.productionapps.capturethemoment.models.Card;

public class SmallCardsActivity extends AppCompatActivity implements SmallCardClickListener, DeleteBookListener {
    private static String TAG = "SmallCardsActivity";

    // Utility members
    private String bookName;
    private String bookId;
    private GetCardsViewModel getCardsViewModel;
    private SmallCardsAdapter adapter;
    private ArrayList<CharSequence> allCardIds;
    private List<String> smallCardImagePaths;
    private List<Card> smallCards;
    private Boolean ownerCanEdit;
    private DeleteBookViewModel deleteBookViewModel;

    // UI members
    private RecyclerView cardsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialiseUI();
        initialiseViewModel();
    }

    private void initialiseViewModel() {
        long start = System.nanoTime();
        GetCardsModelFactory getCardsModelFactory = GetCardsModelFactory.createFactory(this);
        getCardsViewModel = ViewModelProviders.of(this, getCardsModelFactory).get(GetCardsViewModel.class);
        getCardsViewModel.getAllCardsFor(bookId).observe(this,
                new Observer<List<Card>>() {
                    @Override
                    public void onChanged(@Nullable List<Card> cards) {
                        if (cards == null) return;
                        Log.i(TAG, "onChanged: Cards fetched: "+cards.size());
                        smallCards = cards;
                        smallCardImagePaths = new ArrayList<>();
                        allCardIds = new ArrayList<>();
                        for (Card card: smallCards) {
                            allCardIds.add(card.getCardId());
                            String imagePath = getCardsViewModel.getOneImagePathForCard(card.getCardId());
                            smallCardImagePaths.add(imagePath);
                        }
                        adapter.setData(smallCardImagePaths);
                        adapter.notifyDataSetChanged();
                        long end = System.nanoTime();
                        Log.i(TAG, "onChanged: Small Cards Time: "+(end-start));
                    }
                });
        ownerCanEdit = getCardsViewModel.getCurrentUserEditAccessForThisBook(bookId);
        Log.i(TAG, "initialiseViewModel: MY BOOK? "+ownerCanEdit);
        adapter.setOwnerCanEdit(ownerCanEdit);

        DeleteBookModelFactory deleteBookModelFactory = DeleteBookModelFactory.createFactory(this);
        deleteBookViewModel = ViewModelProviders.of(this, deleteBookModelFactory).get(DeleteBookViewModel.class);
    }

    private void initialiseUI() {
        bookName = getIntent().getExtras().getString("bookName");
        bookId = getIntent().getExtras().getString("bookId");
        adapter = new SmallCardsAdapter(this, smallCardImagePaths, bookId, this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(bookName);
        setContentView(R.layout.activity_book_details);
        cardsList = findViewById(R.id.book_details_rv_cards);
        GridLayoutManager manager = new GridLayoutManager(this,3);
        cardsList.setAdapter(adapter);
        cardsList.setLayoutManager(manager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.book_context_menu, menu);
        // If this book is not an owned book, then user cannot edit it
        boolean isOwnBook = getIntent().getExtras() != null ?
                getIntent().getExtras().getBoolean("OwnBook") :
                false;
        if (!isOwnBook) {
            menu.removeItem(R.id.book_menu_item_update);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.book_menu_item_update) {
            navigateToEditBookActivity();
        }
        else if (item.getItemId() == R.id.book_menu_item_trash) {
            deleteBookViewModel.setDeleteBookListener(this);
            deleteBookViewModel.deleteBook(bookId);
        }
        else if (item.getItemId() == R.id.book_menu_item_info) {

        }
        else if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void navigateToEditBookActivity(){
        // Extract the selected bookId from the intent
        String bookId = getIntent().getExtras().getString("bookId");
        // Using this bookId, call update activity
        Intent editBookIntent = new Intent(this, EditBookActivity.class);
        editBookIntent.putExtra(EditBookActivity.BOOKID, bookId);
        startActivity(editBookIntent);
    }

    @Override
    public void onSmallCardClicked(Integer positionOfCardClicked) {
        Log.i(TAG, "onSmallCardClicked: Position: "+positionOfCardClicked);
        Card selectedCard = smallCards.get(positionOfCardClicked);
        Intent bigCardIntent = new Intent(this, BigCardActivity.class);
        bigCardIntent.putExtra(BigCardActivity.SELECTED_CARD_POSITION, positionOfCardClicked);
        bigCardIntent.putCharSequenceArrayListExtra(BigCardActivity.ALL_CARD_IDS, allCardIds);
        startActivity(bigCardIntent);
    }

    @Override
    public void onBookDeleted() {
        Toast.makeText(this, "Book Deleted!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
