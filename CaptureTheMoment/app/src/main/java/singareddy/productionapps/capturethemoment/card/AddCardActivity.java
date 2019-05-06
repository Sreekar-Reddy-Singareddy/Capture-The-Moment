package singareddy.productionapps.capturethemoment.card;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.IntegerRes;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import singareddy.productionapps.capturethemoment.DepthPageTransformer;
import singareddy.productionapps.capturethemoment.R;

public class AddCardActivity extends AppCompatActivity {
    private static String TAG = "AddCardActivity";

    private ViewPager photoPager;
    private MultiAutoCompleteTextView description;
    private ImageView pickLocation;
    private TextView locationView;
    private FloatingActionButton addPhotoButton;
    private RecyclerView indicators;
    private List<Uri> imageUris;
    private ImagePageAdapter adapter;
    private IndicatorAdapter indicatorAdapter;
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);
        initialiseUI();
    }

    private void initialiseUI() {
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
}
