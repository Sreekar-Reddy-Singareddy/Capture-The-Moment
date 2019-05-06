package singareddy.productionapps.capturethemoment.card.add;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import singareddy.productionapps.capturethemoment.Utils.DepthPageTransformer;
import singareddy.productionapps.capturethemoment.R;
import singareddy.productionapps.capturethemoment.card.ImagePageAdapter;
import singareddy.productionapps.capturethemoment.card.IndicatorAdapter;
import singareddy.productionapps.capturethemoment.models.Card;

public class AddCardActivity extends AppCompatActivity implements AddCardListener{
    private static String TAG = "AddCardActivity";

    private ViewPager photoPager;
    private MultiAutoCompleteTextView description; // Input for new card
    private ImageView pickLocation;
    private TextView locationView; // Input for new card
    private FloatingActionButton addPhotoButton;
    private RecyclerView indicators;
    private List<Uri> imageUris; // Input for new card
    private ImagePageAdapter adapter;
    private IndicatorAdapter indicatorAdapter;
    private int page = 1;
    private AddCardViewModel addCardViewModel;
    private String bookId;

    // DUMMY DATA
    private String dummyDesc = "This is the card description. I am dummy... Wait for real one";
    private long dummyDate = new Date().getTime();
    private String dummyLocation = "Mysore, Karnataka";
    private List<String> dummyPeople = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);
        if (getIntent() != null && getIntent().getExtras() != null) {
            bookId = getIntent().getExtras().getString("bookId");
        }
        initialiseUI();
        initialiseDummyData();
        initialiseViewModel();
    }

    private void initialiseViewModel() {
        AddCardModelFactory factory = AddCardModelFactory.createFactory(this);
        addCardViewModel = ViewModelProviders.of(this, factory).get(AddCardViewModel.class);
        addCardViewModel.setAddCardListener(this);
    }

    private void initialiseDummyData() {
        dummyPeople.add("Vikas");
        dummyPeople.add("Subbu");
        dummyPeople.add("Bharath");
        dummyPeople.add("Divya");
    }

    private void initialiseUI() {
        getSupportActionBar().setTitle("Add Memory");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageUris = new ArrayList<>();
        photoPager = findViewById(R.id.add_card_pv_photos);
        photoPager.setPageTransformer(true, new DepthPageTransformer());
        photoPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                Log.i(TAG, "onPageSelected: PAGE: "+i);
                indicatorAdapter.setSelectedPage(i);
                indicatorAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        description = findViewById(R.id.add_card_et_description);
        pickLocation = findViewById(R.id.add_card_iv_location_icon);
        locationView = findViewById(R.id.add_card_tv_location);
        addPhotoButton = findViewById(R.id.add_card_fb_add_photo);
        indicators = findViewById(R.id.add_card_rv_indicator);
        pickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Logic to pick location
                pickLocation();
            }
        });
        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Logic to pick photos or take photos
                addPhotos();
            }
        });
        adapter = new ImagePageAdapter(this, getSupportFragmentManager(), imageUris);
        photoPager.setAdapter(adapter);

        indicatorAdapter = new IndicatorAdapter(this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        indicators.setAdapter(indicatorAdapter);
        indicators.setLayoutManager(manager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_card_context_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveCard(MenuItem saveItem) {
        Log.i(TAG, "saveCard: Card will be saved under: "+bookId);
        Card newCard = new Card();
        newCard.setDescription(dummyDesc);
        newCard.setLocation(dummyLocation);
        newCard.setCreatedTime(dummyDate);
        newCard.setFriends(dummyPeople);

        addCardViewModel.createNewCard(bookId, newCard, imageUris);
        saveItem.setEnabled(false);
    }

    private void pickLocation() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setPackage("com.google.android.apps.maps");
        startActivityForResult(intent, 456);
    }

    private void addPhotos() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, 123);
    }

    private void photoFromGallery() {

    }

    private void photoFromCamera() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK) {
            Log.i(TAG, "onActivityResult: DATA:" +data);
            Log.i(TAG, "onActivityResult: DATA URIs: "+data.getClipData().getItemAt(0).getUri());
            for (int i=0; i<data.getClipData().getItemCount(); i++) {
                Uri imageUri = data.getClipData().getItemAt(i).getUri();
                imageUris.add(imageUri);
            }
            adapter.setImageUris(imageUris);
            adapter.notifyDataSetChanged();
            indicatorAdapter.setPages(imageUris.size());
            indicatorAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCardCreated() {
        Toast.makeText(this, "Memory card created successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}
