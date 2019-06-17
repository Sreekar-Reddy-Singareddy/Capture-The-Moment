package singareddy.productionapps.capturethemoment.card.add;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import singareddy.productionapps.capturethemoment.R;
import singareddy.productionapps.capturethemoment.models.Card;
import singareddy.productionapps.capturethemoment.utils.AppUtilities;

public class AddCardActivity extends AppCompatActivity implements AddCardListener{
    private static String TAG = "AddCardActivity";
    public static final String BOOK_ID = "bookId";
    protected static final String PHOTOS_FRAG_STATE = "Photos";
    protected static final String DETAILS_FRAG_STATE = "Details";

    // Utitlity members
    private List<Uri> imageUris;
    private AddCardViewModel addCardViewModel;
    private String bookId;
    protected String cardDescription = "This is the card description. I am dummy... Wait for real one";
    protected long cardDate = new Date().getTime();
    protected String cardLocation = "Mysore, Karnataka";
    private List<String> cardFriends = new ArrayList<>();
    private AddCardPhotosFragment photosFragment;
    private AddCardMoreDetailsFragment detailsFragment;
    private Boolean detailsFragShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialiseUI();
        initialiseDummyData();
        initialiseViewModel();
    }

    private void initialiseUI() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            bookId = getIntent().getExtras().getString(BOOK_ID);
        }
        setTheme(AppUtilities.CURRENT_THEME);
        setContentView(R.layout.activity_add_card);
        getSupportActionBar().setTitle(AppUtilities.ScreenTitles.SCREEN_TITLE_ADD_CARD);
        Drawable icon = getDrawable(R.drawable.back);
        getSupportActionBar().setHomeAsUpIndicator(icon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imageUris = new ArrayList<>();
        photosFrag();
    }

    private void initialiseDummyData() {
        cardFriends.add("Vikas");
        cardFriends.add("Subbu");
        cardFriends.add("Bharath");
        cardFriends.add("Divya");
    }

    private void initialiseViewModel() {
        AddCardModelFactory factory = AddCardModelFactory.createFactory(this);
        addCardViewModel = ViewModelProviders.of(this, factory).get(AddCardViewModel.class);
        addCardViewModel.setAddCardListener(this);
    }

    void photosFrag () {
        detailsFragShown = false;
        if (photosFragment == null) photosFragment = new AddCardPhotosFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.add_card_cl_layout, photosFragment)
                .addToBackStack(PHOTOS_FRAG_STATE)
                .commit();
    }

    void detailsFrag () {
        detailsFragShown = true;
        if (detailsFragment == null) detailsFragment = new AddCardMoreDetailsFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.add_card_cl_layout, detailsFragment)
                .addToBackStack(DETAILS_FRAG_STATE)
                .commit();
    }

    protected void saveCard() {
        Card newCard = new Card();
        newCard.setDescription(cardDescription);
        newCard.setLocation(cardLocation);
        newCard.setCreatedTime(cardDate);
        newCard.setFriends(cardFriends);
        newCard.setBookId(bookId);

        addCardViewModel.createNewCard(newCard, imageUris);
    }

    public List<Uri> getImageUris() {
        return imageUris;
    }

    public void setImageUris(List<Uri> imageUris) {
        this.imageUris = imageUris;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (detailsFragShown){
                getSupportFragmentManager().popBackStackImmediate();
                detailsFragShown = false;
                return false;
            }
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (detailsFragShown) {
            getSupportFragmentManager().popBackStackImmediate();
            detailsFragShown = false;
            return;
        }
        finish();
    }

    @Override
    public void onCardCreated() {
        Toast.makeText(this, "Memory card created successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}
