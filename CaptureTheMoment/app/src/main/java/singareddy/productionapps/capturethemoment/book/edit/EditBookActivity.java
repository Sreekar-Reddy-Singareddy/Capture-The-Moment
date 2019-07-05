package singareddy.productionapps.capturethemoment.book.edit;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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
import singareddy.productionapps.capturethemoment.book.add.AddBookListener;
import singareddy.productionapps.capturethemoment.book.add.SecOwnersAdapter;
import singareddy.productionapps.capturethemoment.models.Book;
import singareddy.productionapps.capturethemoment.models.SecondaryOwner;
import singareddy.productionapps.capturethemoment.models.ShareInfo;
import singareddy.productionapps.capturethemoment.utils.AppUtilities;

import static singareddy.productionapps.capturethemoment.utils.AppUtilities.Book.BOOK_DB_ERROR;
import static singareddy.productionapps.capturethemoment.utils.AppUtilities.Book.BOOK_EXISTS;
import static singareddy.productionapps.capturethemoment.utils.AppUtilities.Book.BOOK_NAME_EMPTY;
import static singareddy.productionapps.capturethemoment.utils.AppUtilities.Book.BOOK_NAME_INVALID;
import static singareddy.productionapps.capturethemoment.utils.AppUtilities.Book.*;

public class EditBookActivity extends AppCompatActivity
        implements AddBookListener, UpdateBookListener, OwnerRemoveClickListener {
    private static String TAG = "EditBookActivity";

    public static String BOOKID = "BOOKID";
    Integer mNewBookCreationFlag = 0;
    UpdateBookViewModel updateBookViewModel;
    String bookId;
    EditText bookName;
    RecyclerView secOwnersList;
    ImageButton addSecOwnerButton;
    Button createBookButton;
    List<SecondaryOwner> activeOwners;
    List<SecondaryOwner> removedOwners;
    SecOwnersAdapter adapter;
    Book mBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        removedOwners = new ArrayList<>();

        initialiseUI();
        initViewModel();
    }

    private void initialiseUI() {
        setTheme(AppUtilities.CURRENT_THEME);
        setContentView(R.layout.activity_add_book);
        Drawable icon = getDrawable(R.drawable.back);
        getSupportActionBar().setHomeAsUpIndicator(icon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bookId = getIntent().getStringExtra(BOOKID);
        bookName = findViewById(R.id.add_book_et_name);
        secOwnersList = findViewById(R.id.add_book_rv_sec_owners);
        addSecOwnerButton = findViewById(R.id.add_book_ib_add_sec_owner);
        createBookButton = findViewById(R.id.add_book_bt_create);
        activeOwners = new ArrayList<>();
        adapter = new SecOwnersAdapter(this, activeOwners, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        secOwnersList.setAdapter(adapter);
        secOwnersList.setLayoutManager(layoutManager);
    }

    public void initViewModel () {
        UpdateBookModelFactory factory = UpdateBookModelFactory.createFactory(this);
        updateBookViewModel = ViewModelProviders.of(this, factory).get(UpdateBookViewModel.class);
        updateBookViewModel.setUpdateBookListener(this);
        mBook = updateBookViewModel.getBookDetailsFor(bookId);
        updateBookViewModel.getSecondaryOwners(bookId).observe(this, new Observer<List<ShareInfo>>() {
            @Override
            public void onChanged(@Nullable List<ShareInfo> shareInfos) {
                Log.i(TAG, "onChanged: shared infos...");
                List<SecondaryOwner> currentSecOwners = updateBookViewModel.getUsernamesForUids(EditBookActivity.this, shareInfos);
                activeOwners.clear();
                activeOwners.addAll(currentSecOwners);
                adapter.notifyDataSetChanged();
                Log.i(TAG, "onChanged: OWNER: "+activeOwners.get(0).getUsername());
            }
        });
        getSupportActionBar().setTitle("Edit "+mBook.getName());
        bookName.setText(mBook.getName());
    }

    public void addNewSecOwner(View view) {
        if (view == addSecOwnerButton) {
            SecondaryOwner secondaryOwnerObj = new SecondaryOwner();
            activeOwners.add(0, secondaryOwnerObj);
            adapter.notifyDataSetChanged();
        }
    }

    public void createBook (View view) {
        createBookButton.setEnabled(false);
        updateBookViewModel.updateBook(bookId ,bookName.getText().toString(), mBook.getName(), activeOwners, removedOwners);
    }

    @Override
    public void onOwnerRemoved(SecondaryOwner removedOwner) {
        if (removedOwner.getValidated() != SEC_OWNER_VALID) return;
        removedOwners.add(removedOwner);
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
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAllSecOwnersValidated() {
        adapter.notifyDataSetChanged();
        createBookButton.setEnabled(true);
    }

    @Override
    public void onDuplicatesExist() {
        createBookButton.setEnabled(true);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSelfUsernameGiven(String selfUsername) {
        createBookButton.setEnabled(true);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.book_self_username_dialog_title))
                .setMessage(getResources().getString(R.string.book_self_username_dialog_message))
                .setNeutralButton(getResources().getString(R.string.book_self_username_dialog_neutral_button), null)
                .create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                // Remove this secondary owner
                SecondaryOwner s = new SecondaryOwner(selfUsername);
                List<SecondaryOwner> tempActiveOwners = new ArrayList<>();
                for (SecondaryOwner owner: activeOwners) {
                    if (!owner.equals(s)) tempActiveOwners.add(owner);
                }
                activeOwners.clear();
                activeOwners.addAll(tempActiveOwners);
                tempActiveOwners = null;
                // Call viewmodel again
                updateBookViewModel.updateBook(bookId, bookName.getText().toString(), mBook.getName(), activeOwners, removedOwners);
                adapter.notifyDataSetChanged();
            }
        });
        dialog.show();
    }

    @Override
    public void onBookUpdated() {
        Toast.makeText(this, "Book Updated!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
