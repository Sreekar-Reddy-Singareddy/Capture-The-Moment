package singareddy.productionapps.capturethemoment.book.add;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
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
import singareddy.productionapps.capturethemoment.models.SecondaryOwner;

import static singareddy.productionapps.capturethemoment.utils.AppUtilities.Book.*;
import static singareddy.productionapps.capturethemoment.utils.AppUtilities.ScreenTitles.*;

public class AddBookActivity extends AppCompatActivity implements AddBookListener {
    private static String TAG = "AddBookActivity";

    AddBookViewModel addBookViewModel;

    EditText bookName;
    Button createButton;
    ImageButton addNewSecOwner;
    RecyclerView secOwnersList;
    List<SecondaryOwner> activeSecOwners;
    SecOwnersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activeSecOwners = new ArrayList<>();
        activeSecOwners.add(new SecondaryOwner("ushasree@gmail.com", false));
        activeSecOwners.add(new SecondaryOwner("gopi@gmail.com", true));
        activeSecOwners.add(new SecondaryOwner("sreekesh@gmail.com", false));
        initialiseUI();
        initialiseViewModel();
    }

    private void initialiseUI() {
        setContentView(R.layout.activity_add_book);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(ADD_BOOK_SCREEN);
        bookName = findViewById(R.id.add_book_et_name);
        addNewSecOwner = findViewById(R.id.add_book_ib_add_sec_owner);
        secOwnersList = findViewById(R.id.add_book_rv_sec_owners);
        createButton = findViewById(R.id.add_book_bt_create);
        adapter = new SecOwnersAdapter(this, activeSecOwners);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        secOwnersList.setAdapter(adapter);
        secOwnersList.setLayoutManager(layoutManager);
    }

    private void initialiseViewModel() {
        AddBookModelFactory factory = AddBookModelFactory.createFactory(this);
        addBookViewModel = ViewModelProviders.of(this, factory).get(AddBookViewModel.class);
        addBookViewModel.setBookListener(this);
    }

    public void addNewSecOwner (View view) {
        if (view == addNewSecOwner) {
            SecondaryOwner secondaryOwnerObj = new SecondaryOwner();
            activeSecOwners.add(0, secondaryOwnerObj);
            adapter.notifyDataSetChanged();
        }
    }

    public void createBook (View view) {
        if (view == createButton) {
            createButton.setEnabled(false);
            addBookViewModel.createThisBook(bookName.getText().toString(), activeSecOwners);
        }
    }

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
        createButton.setEnabled(true);
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
        createButton.setEnabled(true);
    }

    @Override
    public void onNewBookCreated() {
        createButton.setEnabled(true);
        Toast.makeText(this, "Book created successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onSelfUsernameGiven(String selfUsername) {
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
                for (SecondaryOwner owner: activeSecOwners) {
                    if (!owner.equals(s)) tempActiveOwners.add(owner);
                }
                activeSecOwners.clear();
                activeSecOwners.addAll(tempActiveOwners);
                // Call viewmodel again
                addBookViewModel.createThisBook(bookName.getText().toString(), activeSecOwners);
                adapter.notifyDataSetChanged();
            }
        });
        dialog.show();
    }

    @Override
    public void onDuplicatesExist() {
        Toast.makeText(this, "Duplicate usernames detected", Toast.LENGTH_SHORT).show();
        adapter.notifyDataSetChanged();
        createButton.setEnabled(true);
    }
}
