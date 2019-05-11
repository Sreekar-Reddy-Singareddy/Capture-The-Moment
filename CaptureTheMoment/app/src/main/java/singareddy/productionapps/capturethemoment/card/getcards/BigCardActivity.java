package singareddy.productionapps.capturethemoment.card.getcards;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import singareddy.productionapps.capturethemoment.R;
import singareddy.productionapps.capturethemoment.models.Card;

public class BigCardActivity extends AppCompatActivity implements BigCardClickListener {
    private static final String FRONT_FACE = "FRONT";
    private static final String BACK_FACE = "BACK";
    private static String TAG = "BigCardActivity";
    public static final String ALL_CARD_IDS = "allCardIds";
    public static final String SELECTED_CARD_POSITION = "selectedCardPosition";

    private Integer positionOfCardToBeDisplayed;
    private GetCardsViewModel getCardsViewModel;
    private ArrayList<CharSequence> allCardIds;
    protected Card cardToBeDisplayed;
    protected List<Uri> imageUris;
    private Fragment frontFragment, backFragment;
    private CardView uiCard;
    private String faceShown = BACK_FACE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialiseViewModel();
        initialiseUI();
    }

    private void initialiseViewModel() {
        GetCardsModelFactory factory = GetCardsModelFactory.createFactory(this);
        getCardsViewModel = ViewModelProviders.of(this, factory).get(GetCardsViewModel.class);
    }

    private void initialiseUI() {
        setContentView(R.layout.activity_big_card);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        int cardWidth = getWindowManager().getDefaultDisplay().getWidth();
        uiCard = findViewById(R.id.activity_big_card_cv_container);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(cardWidth-100, cardWidth-100); // TODO: Change the hard coded values here
        params.startToStart = R.id.activity_big_card_cl_layout;
        params.topToTop = R.id.activity_big_card_cl_layout;
        params.endToEnd = R.id.activity_big_card_cl_layout;
        params.bottomToBottom = R.id.activity_big_card_cl_layout;
        uiCard.setLayoutParams(params);
        uiCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: Card Clicked");
            }
        });
        allCardIds = getIntent().getExtras().getCharSequenceArrayList(ALL_CARD_IDS);
        positionOfCardToBeDisplayed = getIntent().getExtras().getInt(SELECTED_CARD_POSITION);
        getCardWithId(allCardIds.get(positionOfCardToBeDisplayed).toString());
    }

    private void getCardWithId (String cardId) {
        Log.i(TAG, "getCardWithId: Card ID to display: "+cardId);
        getCardsViewModel.getCardWithId(cardId).observe(this,
                new Observer<Card>() {
                    @Override
                    public void onChanged(@Nullable Card card) {
                        Log.i(TAG, "onChanged: CardID: "+card.getCardId());
                        cardToBeDisplayed = card;
                        getPathsForCard(card);
                        showFrontFrag();
                    }
                });
    }

    private void getPathsForCard(Card card) {
        card.setImagePaths(getCardsViewModel.getImagePathsForCardWithId(card.getCardId()));
        imageUris = getCardsViewModel.getUrisForPaths(this, card.getImagePaths());
        Log.i(TAG, "getPathsForCard: URIs for "+card.getCardId()+" are: "+imageUris.size());
    }

    public void previousCard (View v) {
        Log.i(TAG, "previousCard: *");
        if (positionOfCardToBeDisplayed > 0) {
            positionOfCardToBeDisplayed--;
            getCardWithId(allCardIds.get(positionOfCardToBeDisplayed).toString());
        }
    }

    public void nextCard (View v) {
        Log.i(TAG, "nextCard: *");
        if (positionOfCardToBeDisplayed < allCardIds.size()-1) {
            positionOfCardToBeDisplayed++;
            getCardWithId(allCardIds.get(positionOfCardToBeDisplayed).toString());
        }
    }

    private void showFrontFrag () {
        frontFragment = new BigCardFrontFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_big_card_cv_container, frontFragment).commit();
    }

    private void showBackFrag() {
        backFragment = new BigCardBackFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_big_card_cv_container, backFragment).commit();
    }

    @Override
    public void bigCardClicked() {
        Log.i(TAG, "bigCardClicked: *");
        switch (faceShown) {
            case FRONT_FACE:
                showFrontFrag();
                faceShown = BACK_FACE;
                break;
            case BACK_FACE:
                showBackFrag();
                faceShown = FRONT_FACE;
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}