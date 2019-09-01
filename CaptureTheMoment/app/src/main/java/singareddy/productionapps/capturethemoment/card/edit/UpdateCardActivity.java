package singareddy.productionapps.capturethemoment.card.edit;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import singareddy.productionapps.capturethemoment.R;
import singareddy.productionapps.capturethemoment.card.get.GetCardsModelFactory;
import singareddy.productionapps.capturethemoment.card.get.GetCardsViewModel;
import singareddy.productionapps.capturethemoment.models.Card;
import singareddy.productionapps.capturethemoment.utils.AppUtilities;

public class UpdateCardActivity extends AppCompatActivity implements UpdateCardListener {
    private static final String TAG = "UpdateCardActivity";
    public static final String CARD_ID_TO_EDIT = "cardId";
    public static final String PHOTOS_FRAG_STATE = "Photos";
    public static final String DETAILS_FRAG_STATE = "MoreDetails";

    private GetCardsViewModel getCardsViewModel;
    private UpdateCardViewModel updateCardViewModel;
    private String cardIdToEdit;
    private ConstraintLayout fragmentContainer;
    private UpdateCardPhotosFragment photosFragment;
    private UpdateCardMoreDetailsFragment moreDetailsFragment;
    protected MutableLiveData<Boolean> cardUpdateSuccessFlag;

    // Card data
    protected Card cardToEdit;
    protected List<Uri> activePhotoUris;
    protected List<Uri> removedPhotoUris;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        removedPhotoUris = new ArrayList<>();
        cardUpdateSuccessFlag = new MutableLiveData<>(); cardUpdateSuccessFlag.setValue(false);

        initialiseUI();
        initialiseViewModel();
    }

    private void initialiseUI() {
        setTheme(AppUtilities.CURRENT_THEME);
        setContentView(R.layout.activity_add_card);
        Drawable icon = getDrawable(R.drawable.back);
        getSupportActionBar().setHomeAsUpIndicator(icon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(AppUtilities.ScreenTitles.SCREEN_TITLE_EDIT_CARD);
        cardIdToEdit = getIntent().getExtras().getString(CARD_ID_TO_EDIT);
    }

    private void initialiseViewModel() {
        ViewModelProvider.Factory factory = GetCardsModelFactory.createFactory(this);
        getCardsViewModel = ViewModelProviders.of(this, factory).get(GetCardsViewModel.class);
        getCardsViewModel.getCardWithId(cardIdToEdit).observe(this,
                new Observer<Card>() {
                    @Override
                    public void onChanged(@Nullable Card card) {
                        cardToEdit = card;
                        card.setImagePaths(getCardsViewModel.getImagePathsForCardWithId(cardIdToEdit));
                        activePhotoUris = getCardsViewModel.getUrisForPaths(UpdateCardActivity.this, card.getImagePaths());
                        showPhotosFragment();
                    }
                });

        ViewModelProvider.Factory updateFactory = UpdateCardModelFactory.createFactory(this);
        updateCardViewModel = ViewModelProviders.of(this, updateFactory).get(UpdateCardViewModel.class);
    }

    private void showPhotosFragment() {
        if (photosFragment == null) photosFragment = new UpdateCardPhotosFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.add_card_cl_layout, photosFragment)
                .addToBackStack(PHOTOS_FRAG_STATE)
                .commit();
    }

    protected void showMoreDetailsFragment() {
        if (moreDetailsFragment == null) moreDetailsFragment = new UpdateCardMoreDetailsFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.add_card_cl_layout, moreDetailsFragment)
                .addToBackStack(DETAILS_FRAG_STATE)
                .commit();
    }

    public void saveUpdatedCard() {
        updateCardViewModel.setUpdateCardListener(this);
        updateCardViewModel.saveTheChangesOfCard (cardToEdit, activePhotoUris, removedPhotoUris);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onCardUpdated(String bookId) {
        Toast.makeText(this, "Card Updated!", Toast.LENGTH_SHORT).show();
        cardUpdateSuccessFlag.postValue(true);
        finish();
    }
}
