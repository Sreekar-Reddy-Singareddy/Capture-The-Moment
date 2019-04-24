package singareddy.productionapps.capturethemoment.book.addbook;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import singareddy.productionapps.capturethemoment.R;
import singareddy.productionapps.capturethemoment.models.SecondaryOwner;

import static singareddy.productionapps.capturethemoment.AppUtilities.Book.*;

public class AddBookActivity extends AppCompatActivity implements AddBookListener {
    private static String TAG = "AddBookActivity";

    AddBookViewModel addBookViewModel;
    Integer mNewBookCreationFlag = 0;

    EditText bookName;
    Button createButton;
    ImageButton addNewSecOwner;
    RecyclerView secOwnersList;
    List<SecondaryOwner> secOwnersData;
    SecOwnersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        initialiseViewModel();
        secOwnersData = new ArrayList<>();
        secOwnersData.add(new SecondaryOwner("ushasree@gmail.com", false));
        secOwnersData.add(new SecondaryOwner("gopi@gmail.com", true));
        secOwnersData.add(new SecondaryOwner("sreekesh@gmail.com", false));
        initialiseUI();
    }

    private void initialiseViewModel() {
        AddBookModelFactory factory = AddBookModelFactory.createFactory(this);
        addBookViewModel = ViewModelProviders.of(this, factory).get(AddBookViewModel.class);
        addBookViewModel.setBookListener(this);
        Log.i(TAG, "initialiseViewModel: MODEL: "+addBookViewModel);
    }

    private void initialiseUI() {
        bookName = findViewById(R.id.add_book_et_name); bookName.setText("Vellore");
        addNewSecOwner = findViewById(R.id.add_book_ib_add_sec_owner);
        secOwnersList = findViewById(R.id.add_book_rv_sec_owners);
        createButton = findViewById(R.id.add_book_bt_create);
        adapter = new SecOwnersAdapter(this, secOwnersData);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        secOwnersList.setAdapter(adapter);
        secOwnersList.setLayoutManager(layoutManager);
    }

    public void addNewSecOwner (View view) {
        if (view == addNewSecOwner) {
            SecondaryOwner secondaryOwnerObj = new SecondaryOwner();
            secOwnersData.add(0, secondaryOwnerObj);
            adapter.notifyDataSetChanged();
        }
    }

    public void createBook (View view) {
        if (view == createButton) {
            Log.i(TAG, "createBook: Book name :"+bookName.getText());
            Log.i(TAG, "createBook: Sec owners: "+secOwnersData.size());
            createButton.setEnabled(false);
            addBookViewModel.createThisBook(bookName.getText().toString(), secOwnersData);
            Log.i(TAG, "createBook: Enabled: "+createButton.isEnabled());
        }
    }

    // *************** Interface methods ***************
    @Override
    public void onBookNameInvalid(String code) {
        Log.i(TAG, "onBookNameInvalid: Code: "+code);
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
        Log.i(TAG, "onThisSecOwnerValidated: Some Owner is validated");
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAllSecOwnersValidated() {
        Log.i(TAG, "onAllSecOwnersValidated: All owners are validated. Some might be invalid as well.");
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
                .setTitle("Own username")
                .setMessage("You cannot give your own username as secondary owner since you are the primary owner. This will be removed.")
                .setNeutralButton("Remove", null)
                .create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Log.i(TAG, "onDismiss: SELF_OWNER: "+selfUsername);
                // Remove this secondary owner
                SecondaryOwner s = new SecondaryOwner(selfUsername);
                secOwnersData.removeIf((owner) -> (owner.equals(s)));
                // Call viewmodel again
                addBookViewModel.createThisBook(bookName.getText().toString(), secOwnersData);
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

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: Activity stopping...");
        // When the activity is stopping, clean up all the variables everywhere
//        addBookViewModel.cleanUpVariables();
    }
}
