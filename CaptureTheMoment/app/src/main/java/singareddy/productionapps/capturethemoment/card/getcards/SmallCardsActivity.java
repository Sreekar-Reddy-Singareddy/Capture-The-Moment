package singareddy.productionapps.capturethemoment.card.getcards;

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

import java.util.ArrayList;
import java.util.List;

import singareddy.productionapps.capturethemoment.R;
import singareddy.productionapps.capturethemoment.book.details.EditBookActivity;
import singareddy.productionapps.capturethemoment.models.Card;

public class SmallCardsActivity extends AppCompatActivity implements SmallCardClickListener{
    private static String TAG = "SmallCardsActivity";

    private String bookName;
    private String bookId;
    private RecyclerView cardsList;
    private GetCardsViewModel getCardsViewModel;
    private SmallCardsAdapter adapter;
    private List<String> smallCardImagePaths;
    private List<Card> smallCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialiseUI();
        initialiseViewModel();
    }

    private void initialiseViewModel() {
        GetCardsModelFactory factory = GetCardsModelFactory.createFactory(this);
        getCardsViewModel = ViewModelProviders.of(this, factory).get(GetCardsViewModel.class);
        getCardsViewModel.getAllCardsFor(bookId).observe(this,
                new Observer<List<Card>>() {
                    @Override
                    public void onChanged(@Nullable List<Card> cards) {
                        Log.i(TAG, "onChanged: Cards fetched: "+cards.size());
                        smallCards = cards;
                        smallCardImagePaths = new ArrayList<>();
                        for (Card card: smallCards) {
                            String imagePath = getCardsViewModel.getOneImagePathForCard(card.getCardId());
                            smallCardImagePaths.add(imagePath);
                        }
                        adapter.setData(smallCardImagePaths);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void initialiseUI() {
        bookName = getIntent().getExtras().getString("bookName");
        bookId = getIntent().getExtras().getString("bookId");
        Log.i(TAG, "onCreate: BookID: "+bookId);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(bookName);
        setContentView(R.layout.activity_book_details);
        cardsList = findViewById(R.id.book_details_rv_cards);
        adapter = new SmallCardsAdapter(this, smallCardImagePaths, bookId, this);
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
        bigCardIntent.putExtra(BigCardActivity.CARD_ID, selectedCard.getCardId());
        startActivity(bigCardIntent);
    }
}
