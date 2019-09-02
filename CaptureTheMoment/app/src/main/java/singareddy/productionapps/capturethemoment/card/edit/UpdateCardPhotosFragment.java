package singareddy.productionapps.capturethemoment.card.edit;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.List;

import singareddy.productionapps.capturethemoment.BuildConfig;
import singareddy.productionapps.capturethemoment.R;
import singareddy.productionapps.capturethemoment.card.get.ImagePageAdapter;
import singareddy.productionapps.capturethemoment.card.get.IndicatorAdapter;
import singareddy.productionapps.capturethemoment.utils.AppUtilities;

import static android.app.Activity.RESULT_OK;

public class UpdateCardPhotosFragment extends Fragment {
    static final String TAG = "UpdateCardPhotos";
    static final int CAMERA_PERMISSION_REQUEST = 1;
    static final int STORAGE_PERMISSION_REQUEST = 2;
    static final int CAMERA_INTENT_REQUEST = 3;
    static final int GALLERY_INTENT_REQUEST = 4;

    private View fragView;
    private ViewPager photosPagerView;
    private RecyclerView pagerIndicatorView;
    private ImagePageAdapter photosPagerAdapter;
    private IndicatorAdapter pagerIndicatorAdapter;
    private Button nextButton;
    private ImageView removePhotoButton;
    private FloatingActionButton addPhotoButton;
    private ImageView cameraButton;
    private ImageView galleryButton;
    private Uri capturedUri;
    private UpdateCardActivity parent;

    // Update related data structures
    private List<Uri> activePhotoUris;
    private List<Uri> removedPhotoUris;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        parent = (UpdateCardActivity) getActivity();
        activePhotoUris = parent.activePhotoUris;
        removedPhotoUris = parent.removedPhotoUris;

        fragView = inflater.inflate(R.layout.fragment_add_card_photos, container, false);

        initialiseUI();
        bindDataToUI();
        return fragView;
    }

    private void initialiseUI() {
        photosPagerView = fragView.findViewById(R.id.frag_add_card_photos_pv_photos);
        nextButton = fragView.findViewById(R.id.frag_add_card_photos_bt_next);
        removePhotoButton = fragView.findViewById(R.id.frag_add_card_photos_iv_close_image);
        pagerIndicatorView = fragView.findViewById(R.id.frag_add_card_photos_rv_indicator);
        addPhotoButton = fragView.findViewById(R.id.frag_add_card_photos_fb_add_photo);

        photosPagerView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                pagerIndicatorAdapter.setSelectedPage(i);
                pagerIndicatorAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        nextButton.setOnClickListener(this::showMoreDetailsOfCard);
        removePhotoButton.setOnClickListener(this::removeThisPhotoFromPager);
        addPhotoButton.setOnClickListener(this::addPhotos);

        toggleButtonsVisibility();
    }

    private void bindDataToUI() {
        photosPagerAdapter = new ImagePageAdapter(getContext(),getChildFragmentManager(), activePhotoUris);
        pagerIndicatorAdapter = new IndicatorAdapter(getContext());
        pagerIndicatorAdapter.setPages(activePhotoUris.size());
        pagerIndicatorAdapter.setSelectedPage(0);

        photosPagerView.setAdapter(photosPagerAdapter);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        ((LinearLayoutManager) manager).setOrientation(LinearLayoutManager.HORIZONTAL);
        pagerIndicatorView.setAdapter(pagerIndicatorAdapter);
        pagerIndicatorView.setLayoutManager(manager);
    }

    private void toggleButtonsVisibility() {
        if (activePhotoUris == null || activePhotoUris.size() == 0) {
            // Hide all the buttons
            nextButton.setVisibility(View.GONE);
            removePhotoButton.setVisibility(View.GONE);
        }
        else {
            // Show all the buttons
            nextButton.setVisibility(View.VISIBLE);
            removePhotoButton.setVisibility(View.VISIBLE);
        }
    }

    private void updateUIData() {
        toggleButtonsVisibility();
        pagerIndicatorAdapter.setPages(activePhotoUris.size());
        pagerIndicatorAdapter.notifyDataSetChanged();
        photosPagerAdapter.notifyDataSetChanged();
    }

    private void removeThisPhotoFromPager(View removePhotoButton) {
        // Remove the photo at this position
        int selectedPhoto = photosPagerView.getCurrentItem();
        Uri removedPhotoUri = activePhotoUris.remove(selectedPhoto);
        removedPhotoUris.add(removedPhotoUri);
        updateUIData();
    }

    private void showMoreDetailsOfCard (View nextButton) {
        parent.showMoreDetailsFragment();
    }

    private void addPhotos(View addPhotoButton) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialg_profile_pic, null, false);
        cameraButton = dialogView.findViewById(R.id.dialog_profile_iv_camera);
        galleryButton = dialogView.findViewById(R.id.dialog_profile_iv_gallery);
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();
        dialog.show();
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                photoFromCamera();
            }
        });
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                photoFromGallery();
            }
        });
    }

    private void photoFromGallery() {
        if (getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
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
        if (getContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},CAMERA_PERMISSION_REQUEST);
            return;
        }
        // All permissions granted
        capturedUri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID+".provider",new File("storage/emulated/0/newPic.jpg"));
        Intent cameraIntent = new Intent();
        cameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedUri);
        startActivityForResult(cameraIntent, CAMERA_INTENT_REQUEST);
    }

    private void cropImageAt(Uri capturedUri) {
        CropImage.activity(capturedUri)
                .setAspectRatio(AppUtilities.IMAGE_CROP_RATIO_X,AppUtilities.IMAGE_CROP_RATIO_Y)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(getContext(), this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_INTENT_REQUEST && resultCode == RESULT_OK) {
            // Crop the image here
            cropImageAt(capturedUri);
        }
        if (requestCode == GALLERY_INTENT_REQUEST && resultCode == RESULT_OK) {
            if (data.getClipData() == null) {
                // Only one image is selected
                cropImageAt(data.getData());
                return;
            }
            for (int i=0; i<data.getClipData().getItemCount(); i++) {
                Uri imageUri = data.getClipData().getItemAt(i).getUri();
                cropImageAt(imageUri);
            }
        }
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri croppedImageUri = result.getUri();
            activePhotoUris.add(croppedImageUri);
            updateUIData();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            photoFromGallery();
        }
        else if (requestCode == CAMERA_PERMISSION_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            photoFromCamera();
        }
    }
}
