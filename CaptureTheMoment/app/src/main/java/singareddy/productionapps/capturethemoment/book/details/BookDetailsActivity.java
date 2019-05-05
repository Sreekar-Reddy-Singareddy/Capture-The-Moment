package singareddy.productionapps.capturethemoment.book.details;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import singareddy.productionapps.capturethemoment.R;

public class BookDetailsActivity extends AppCompatActivity {
    private static String TAG = "BookDetailsActivity";

    private String bookName;
    private RecyclerView cards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookName = getIntent().getExtras().getString("bookName");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(bookName);
        setContentView(R.layout.activity_book_details);
        cards = findViewById(R.id.book_details_rv_cards);
        SmallCardsAdapter adapter = new SmallCardsAdapter(this,null);
        GridLayoutManager manager = new GridLayoutManager(this,3);
        cards.setAdapter(adapter);
        cards.setLayoutManager(manager);
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
            navigateToEditActivity();
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

    public void navigateToEditActivity(){
        // Extract the selected bookId from the intent
        String bookId = getIntent().getExtras().getString("bookId");
        // Using this bookId, call update activity
        Intent editBookIntent = new Intent(this, EditBookActivity.class);
        editBookIntent.putExtra(EditBookActivity.BOOKID, bookId);
        startActivity(editBookIntent);
    }
}
