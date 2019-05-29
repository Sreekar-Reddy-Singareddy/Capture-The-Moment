package singareddy.productionapps.capturethemoment.card.get;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import singareddy.productionapps.capturethemoment.R;
import singareddy.productionapps.capturethemoment.card.delete.DeleteCardListener;
import singareddy.productionapps.capturethemoment.card.delete.DeleteCardsModelFactory;
import singareddy.productionapps.capturethemoment.card.delete.DeleteCardsViewModel;
import singareddy.productionapps.capturethemoment.card.edit.UpdateCardActivity;
import singareddy.productionapps.capturethemoment.models.Card;

public class BigCardActivity extends AppCompatActivity implements BigCardClickListener, DeleteCardListener {
    private static String TAG = "BigCardActivity";

    private static final String FRONT_FACE = "FRONT";
    private static final String BACK_FACE = "BACK";
    public static final String ALL_CARD_IDS = "allCardIds";
    public static final String SELECTED_CARD_POSITION = "selectedCardPosition";

    private Integer positionOfCardToBeDisplayed;
    private GetCardsViewModel getCardsViewModel;
    private DeleteCardsViewModel deleteCardsViewModel;
    private ArrayList<CharSequence> allCardIds;
    private Fragment frontFragment, backFragment;
    private CardView uiCard;
    private String faceShown = BACK_FACE;

    /**
     * The components of Card
     */
    protected Card cardToBeDisplayed;
    protected List<Uri> imageUris;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialiseViewModel();
        initialiseUI();
    }

    private void initialiseViewModel() {
        GetCardsModelFactory factory = GetCardsModelFactory.createFactory(this);
        getCardsViewModel = ViewModelProviders.of(this, factory).get(GetCardsViewModel.class);

        DeleteCardsModelFactory deleteFactory = DeleteCardsModelFactory.createFactory(BigCardActivity.this);
        deleteCardsViewModel = ViewModelProviders.of(BigCardActivity.this, deleteFactory).get(DeleteCardsViewModel.class);
    }

    private void initialiseUI() {
        setContentView(R.layout.activity_big_card);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        int cardWidth = getWindowManager().getDefaultDisplay().getWidth();
        uiCard = findViewById(R.id.activity_big_card_cv_container);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(cardWidth-100, cardWidth-100); // TODO: Change the hard coded values here
        params.startToStart = R.id.activity_big_card_cl_layout;
        params.topToTop = R.id.activity_big_card_cl_layout;
        params.endToEnd = R.id.activity_big_card_cl_layout;
        params.bottomToBottom = R.id.activity_big_card_cl_layout;
        uiCard.setLayoutParams(params);
        allCardIds = getIntent().getExtras().getCharSequenceArrayList(ALL_CARD_IDS);
        positionOfCardToBeDisplayed = getIntent().getExtras().getInt(SELECTED_CARD_POSITION);
        getCardWithId(allCardIds.get(positionOfCardToBeDisplayed).toString());
    }

    private void getCardWithId (String cardId) {
        getCardsViewModel.getCardWithId(cardId).observe(this,
                new Observer<Card>() {
                    @Override
                    public void onChanged(@Nullable Card card) {
                        if (card == null) return;
                        cardToBeDisplayed = card;
                        getPathsForCard(card);
                        showFrontFrag(true);
                    }
                });
    }

    private void getPathsForCard(Card card) {
        card.setImagePaths(getCardsViewModel.getImagePathsForCardWithId(card.getCardId()));
        imageUris = getCardsViewModel.getUrisForPaths(this, card.getImagePaths());
    }

    public void previousCard (View v) {
        if (positionOfCardToBeDisplayed > 0) {
            positionOfCardToBeDisplayed--;
            getCardWithId(allCardIds.get(positionOfCardToBeDisplayed).toString());
        }
    }

    public void nextCard (View v) {
        if (positionOfCardToBeDisplayed < allCardIds.size()-1) {
            positionOfCardToBeDisplayed++;
            getCardWithId(allCardIds.get(positionOfCardToBeDisplayed).toString());
        }
    }

    private void showFrontFrag(boolean firstTime) {
        getSupportFragmentManager().popBackStackImmediate();
        frontFragment = new BigCardFrontFragment();
        getSupportFragmentManager().beginTransaction()
            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
            .replace(R.id.activity_big_card_cv_container, frontFragment)
            .addToBackStack("Front")
            .commit();
    }

    private void showBackFrag() {
        getSupportFragmentManager().popBackStackImmediate();
        backFragment = new BigCardBackFragment();
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .replace(R.id.activity_big_card_cv_container, backFragment)
                .addToBackStack("Back")
                .commit();
    }

    private void deleteCard() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.card_delete_dialog_title))
                .setMessage(getResources().getString(R.string.card_delete_dialog_message))
                .setPositiveButton(getResources().getString(R.string.card_delete_dialog_pos_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteCardsViewModel.setDeleteCardListener(BigCardActivity.this);
                        deleteCardsViewModel.deleteCardWithId(cardToBeDisplayed);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.card_delete_dialog_neg_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialogBuilder.create().show();
    }

    private void editCard() {
        // In this method, we call an activity that lets the user
        // edit the card data and save it. The UI will be same as Add Card UI
        Intent updateIntent = new Intent(this, UpdateCardActivity.class);
        updateIntent.putExtra(UpdateCardActivity.CARD_ID_TO_EDIT, cardToBeDisplayed.getCardId());
        startActivity(updateIntent);
    }

    @Override
    public void bigCardClicked() {
        switch (faceShown) {
            case FRONT_FACE:
                showFrontFrag(false);
                faceShown = BACK_FACE;
                break;
            case BACK_FACE:
                showBackFrag();
                faceShown = FRONT_FACE;
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_card_context_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        else if (item.getItemId() == R.id.add_card_menu_item_edit) editCard();
        else if (item.getItemId() == R.id.add_card_menu_item_delete) deleteCard();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCardDeleted(String cardId) {
        Toast.makeText(this, "Card Deleted!", Toast.LENGTH_SHORT).show();
        allCardIds.remove(cardId);
        finish();
    }
}
