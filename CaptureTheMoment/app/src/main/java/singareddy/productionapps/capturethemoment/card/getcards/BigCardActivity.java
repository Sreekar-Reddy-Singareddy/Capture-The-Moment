package singareddy.productionapps.capturethemoment.card.getcards;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import java.util.List;

import singareddy.productionapps.capturethemoment.R;
import singareddy.productionapps.capturethemoment.models.Card;

public class BigCardActivity extends AppCompatActivity {
    private static String TAG = "BigCardActivity";
    public static final String CARD_ID = "cardId";

    private String cardId;
    private GetCardsViewModel getCardsViewModel;
    private List<String> bigCardImagePaths;
    private List<Uri> bigCardImageUris;
    private ImagePageAdapter imageAdapter;
    private ViewPager imagePager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialiseUI();
        initialiseViewModel();
    }

    private void initialiseViewModel() {
        GetCardsModelFactory factory = GetCardsModelFactory.createFactory(this);
        getCardsViewModel = ViewModelProviders.of(this, factory).get(GetCardsViewModel.class);
        getCardsViewModel.getCardWithId(cardId).observe(this,
                new Observer<Card>() {
                    @Override
                    public void onChanged(@Nullable Card card) {
                        Log.i(TAG, "onChanged: Card ID Same: "+card.getCardId().equals(cardId));
                        bigCardImagePaths = getCardsViewModel.getImagePathsForCardWithId(cardId);
                        Log.i(TAG, "onChanged: No Of Images in this card: "+bigCardImagePaths.size());
                        bigCardImageUris = getCardsViewModel.getUrisForPaths(BigCardActivity.this, bigCardImagePaths);
                    }
                });
    }

    private void initialiseUI() {
        setContentView(R.layout.list_item_card);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cardId = getIntent().getExtras().getString(CARD_ID);
        imageAdapter = new ImagePageAdapter(this, getSupportFragmentManager(), bigCardImageUris);
        imagePager = findViewById(R.id.big_card_pv_image_pager);
        Log.i(TAG, "initialiseUI: "+imagePager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
