package singareddy.productionapps.capturethemoment.card.add;

import android.Manifest;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import singareddy.productionapps.capturethemoment.BuildConfig;
import singareddy.productionapps.capturethemoment.utils.DepthPageTransformer;
import singareddy.productionapps.capturethemoment.R;
import singareddy.productionapps.capturethemoment.card.getcards.ImagePageAdapter;
import singareddy.productionapps.capturethemoment.card.getcards.IndicatorAdapter;
import singareddy.productionapps.capturethemoment.models.Card;

public class AddCardActivity extends AppCompatActivity implements AddCardListener{
    private static String TAG = "AddCardActivity";
    static final int CAMERA_PERMISSION_REQUEST = 1;
    static final int STORAGE_PERMISSION_REQUEST = 2;
    static final int CAMERA_INTENT_REQUEST = 3;
    static final int GALLERY_INTENT_REQUEST = 4;

    // Utitlity members
    private List<Uri> imageUris; // Input for new card
    private ImagePageAdapter adapter;
    private IndicatorAdapter indicatorAdapter;
    private int page = 1;
    private AddCardViewModel addCardViewModel;
    private String bookId;

    // UI members
    private ViewPager photoPager;
    private MultiAutoCompleteTextView description; // Input for new card
    private ImageView pickLocation;
    private TextView locationView; // Input for new card
    private FloatingActionButton addPhotoButton;
    private ConstraintLayout constraintLayout;
    private RecyclerView indicators;
    private ImageView cameraButton;
    private ImageView galleryButton;

    // Dummy members
    private String dummyDesc = "This is the card description. I am dummy... Wait for real one";
    private long dummyDate = new Date().getTime();
    private String dummyLocation = "Mysore, Karnataka";
    private List<String> dummyPeople = new ArrayList<>();
    private Uri capturedUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        if (getIntent() != null && getIntent().getExtras() != null) {
            bookId = getIntent().getExtras().getString("bookId");
        }

        setContentView(R.layout.activity_add_card);
        getSupportActionBar().setTitle("Add Memory");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int pagerWidth = getWindowManager().getDefaultDisplay().getWidth();
        constraintLayout = findViewById(R.id.add_card_cl_layout);
        imageUris = new ArrayList<>();
        photoPager = findViewById(R.id.add_card_pv_photos);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(pagerWidth,pagerWidth);
        params.startToStart = R.id.add_card_cl_layout;
        params.topToTop = R.id.add_card_cl_layout;
        params.endToEnd = R.id.add_card_cl_layout;
        photoPager.setLayoutParams(params);
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

    public void saveCard(MenuItem saveItem) {
        Log.i(TAG, "saveCard: Card will be saved under: "+bookId);
        Card newCard = new Card();
        newCard.setDescription(dummyDesc);
        newCard.setLocation(dummyLocation);
        newCard.setCreatedTime(dummyDate);
        newCard.setFriends(dummyPeople);
        newCard.setBookId(bookId);

        addCardViewModel.createNewCard(newCard, imageUris);
        saveItem.setEnabled(false);
    }

    private void pickLocation() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setPackage("com.google.android.apps.maps");
        startActivityForResult(intent, 456);
    }

    private void addPhotos() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialg_profile_pic, null, false);
        cameraButton = dialogView.findViewById(R.id.dialog_profile_iv_camera);
        galleryButton = dialogView.findViewById(R.id.dialog_profile_iv_gallery);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();
        dialog.show();
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: CAMERA");
                dialog.dismiss();
                photoFromCamera();
            }
        });
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: GALLERY");
                dialog.dismiss();
                photoFromGallery();
            }
        });
    }

    private void photoFromGallery() {
        if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION_REQUEST);
            return;
        }
        // All permissions granted
        Intent pickIntent = new Intent();
        pickIntent.setAction(Intent.ACTION_PICK);
        pickIntent.setType("image/*");
        pickIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(pickIntent, GALLERY_INTENT_REQUEST);
    }

    private void photoFromCamera() {
        if (this.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},CAMERA_PERMISSION_REQUEST);
            return;
        }
        // All permissions granted
        capturedUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID+".provider",new File("storage/emulated/0/newPic.jpg"));
        Log.i(TAG, "openCameraForPicture: URI: "+capturedUri);
        Intent cameraIntent = new Intent();
        cameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedUri);
        startActivityForResult(cameraIntent, CAMERA_INTENT_REQUEST);
    }

    private void cropImageAt(Uri capturedUri) {
        CropImage.activity(capturedUri)
                .setAspectRatio(1,1)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_INTENT_REQUEST && resultCode == RESULT_OK) {
            Log.i(TAG, "onActivityResult: IMAGE CAPTURED");
            // Crop the image here
            cropImageAt(capturedUri);
        }
        if (requestCode == GALLERY_INTENT_REQUEST && resultCode == RESULT_OK) {
            Log.i(TAG, "onActivityResult: DATA:" +data);
            Log.i(TAG, "onActivityResult: DATA URIs: "+data.getClipData().getItemAt(0).getUri());
            for (int i=0; i<data.getClipData().getItemCount(); i++) {
                Uri imageUri = data.getClipData().getItemAt(i).getUri();
                Log.i(TAG, "onActivityResult: UNCROPPED URI: "+imageUri);
                cropImageAt(imageUri);
            }
        }
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri croppedImageUri = result.getUri();
            Log.i(TAG, "onActivityResult: CROPPED URI: "+croppedImageUri);
            imageUris.add(croppedImageUri);
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
