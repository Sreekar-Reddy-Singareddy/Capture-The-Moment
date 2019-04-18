package singareddy.productionapps.capturethemoment.book;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import singareddy.productionapps.capturethemoment.R;

public class InsideBookActivity extends AppCompatActivity {
    private static String TAG = "InsideBookActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_inside_book);
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
        editBookIntent.putExtra("bookId", bookId);
        startActivity(editBookIntent);
    }
}
