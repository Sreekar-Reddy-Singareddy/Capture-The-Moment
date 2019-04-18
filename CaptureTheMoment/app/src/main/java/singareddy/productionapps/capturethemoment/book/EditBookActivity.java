package singareddy.productionapps.capturethemoment.book;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import singareddy.productionapps.capturethemoment.R;
import singareddy.productionapps.capturethemoment.models.SecondaryOwner;

import static singareddy.productionapps.capturethemoment.AppUtilities.Book.BOOK_DB_ERROR;
import static singareddy.productionapps.capturethemoment.AppUtilities.Book.BOOK_EXISTS;
import static singareddy.productionapps.capturethemoment.AppUtilities.Book.BOOK_NAME_EMPTY;
import static singareddy.productionapps.capturethemoment.AppUtilities.Book.BOOK_NAME_INVALID;

public class EditBookActivity extends AppCompatActivity implements BookListener{
    private static String TAG = "EditBookActivity";

    Integer mNewBookCreationFlag = 0;
    BookCRUDViewModel mBookCRUDViewModel;

    EditText bookName;
    RecyclerView secOwnersList;
    ImageButton addSecOwnerButton;
    Button createBookButton;
    List<SecondaryOwner> secOwnersData;
    SecOwnersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialiseUI();
    }

    private void initialiseUI() {
        setContentView(R.layout.activity_add_book);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bookName = findViewById(R.id.add_book_et_name);
        secOwnersList = findViewById(R.id.add_book_rv_sec_owners);
        addSecOwnerButton = findViewById(R.id.add_book_ib_add_sec_owner);
        createBookButton = findViewById(R.id.add_book_bt_create);
        secOwnersData = new ArrayList<>();
        adapter = new SecOwnersAdapter(this, secOwnersData);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        secOwnersList.setAdapter(adapter);
        secOwnersList.setLayoutManager(layoutManager);
    }

    public void addNewSecOwner(View view) {
        if (view == addSecOwnerButton) {
            SecondaryOwner secondaryOwnerObj = new SecondaryOwner();
            secOwnersData.add(0, secondaryOwnerObj);
            adapter.notifyDataSetChanged();
        }
    }

    public void createBook (View view) {
        createBookButton.setEnabled(false);
    }

    public void initViewModel () {
        mBookCRUDViewModel = ViewModelProviders.of(this).get(BookCRUDViewModel.class);
    }

    // *************** Overriddent methods ***************
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    // *************** Interface methods ***************
    @Override
    public void onBookNameInvalid(String code) {
        Log.i(TAG, "onBookNameInvalid: Code: "+code);
        createBookButton.setEnabled(true);
        String toastMessage = "";
        switch (code) {
            case BOOK_NAME_EMPTY:
                toastMessage = "Book name cannot be empty";
                break;
            case BOOK_NAME_INVALID:
                toastMessage = "Book name contains invalid characters";
                break;
            case BOOK_EXISTS:
                toastMessage = "Book already exists with this name!";
                break;
            case BOOK_DB_ERROR:
                toastMessage = "Some database has occured. Try again.";
                break;
        }
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onThisSecOwnerValidated() {
        Log.i(TAG, "onThisSecOwnerValidated: Some Owner is validated");
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAllSecOwnersValidated() {
        Log.i(TAG, "onAllSecOwnersValidated: All owners are validated. Some might be invalid as well.");
        adapter.notifyDataSetChanged();
        createBookButton.setEnabled(true);
    }

}
