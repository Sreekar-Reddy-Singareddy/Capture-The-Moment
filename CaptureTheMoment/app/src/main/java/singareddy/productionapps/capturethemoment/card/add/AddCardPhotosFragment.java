package singareddy.productionapps.capturethemoment.card.add;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
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

import singareddy.productionapps.capturethemoment.BuildConfig;
import singareddy.productionapps.capturethemoment.R;
import singareddy.productionapps.capturethemoment.card.get.ImagePageAdapter;
import singareddy.productionapps.capturethemoment.card.get.IndicatorAdapter;
import singareddy.productionapps.capturethemoment.utils.AppUtilities;

import static android.app.Activity.RESULT_OK;

public class AddCardPhotosFragment extends Fragment {
    private static String TAG = "AddCardPhotosFragment";
    static final int CAMERA_PERMISSION_REQUEST = 1;
    static final int STORAGE_PERMISSION_REQUEST = 2;
    static final int CAMERA_INTENT_REQUEST = 3;
    static final int GALLERY_INTENT_REQUEST = 4;

    private View fragView;
    private ViewPager photoPager;
    private FloatingActionButton addPhotoButton;
    private RecyclerView indicators;
    private ImageView cameraButton;
    private ImageView galleryButton;
    private Button next;
    private ImageView removeImage;

    private ImagePageAdapter adapter;
    private IndicatorAdapter indicatorAdapter;
    private AddCardActivity parent;
    private Uri capturedUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragView = inflater.inflate(R.layout.fragment_add_card_photos, container, false);
        initialiseUI();
        return fragView;
    }

    private void initialiseUI() {
        parent = (AddCardActivity) getActivity();
        int pagerWidth = parent.getWindowManager().getDefaultDisplay().getWidth();
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(pagerWidth,pagerWidth);
        params.startToStart = R.id.frag_add_card_photos_cl_layout;
        params.topToTop = R.id.frag_add_card_photos_cl_layout;
        params.endToEnd = R.id.frag_add_card_photos_cl_layout;
        params.bottomToBottom = R.id.frag_add_card_photos_cl_layout;
        photoPager = fragView.findViewById(R.id.frag_add_card_photos_pv_photos);
        photoPager.setLayoutParams(params);
        photoPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                indicatorAdapter.setSelectedPage(i);
                indicatorAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        addPhotoButton = fragView.findViewById(R.id.frag_add_card_photos_fb_add_photo);
        indicators = fragView.findViewById(R.id.frag_add_card_photos_rv_indicator);
        next = fragView.findViewById(R.id.frag_add_card_photos_bt_next);
        removeImage = fragView.findViewById(R.id.frag_add_card_photos_iv_close_image);
        removeImage.setVisibility(View.GONE);
        removeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.getImageUris().remove(photoPager.getCurrentItem());
                updateImagePager();
            }
        });
        next.setVisibility(View.GONE);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.detailsFrag();
            }
        });
        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Logic to pick photos or take photos
                addPhotos();
            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        indicators.setLayoutManager(manager);
        adapter = new ImagePageAdapter(getContext(), getChildFragmentManager(), parent.getImageUris());
        photoPager.setAdapter(adapter);
        indicatorAdapter = new IndicatorAdapter(getContext());
        indicators.setAdapter(indicatorAdapter);
        updateImagePager();
    }

    private void addPhotos() {
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
            for (int i=0; i<data.getClipData().getItemCount(); i++) {
                Uri imageUri = data.getClipData().getItemAt(i).getUri();
                cropImageAt(imageUri);
            }
        }
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri croppedImageUri = result.getUri();
            parent.getImageUris().add(croppedImageUri);
            updateImagePager();
        }
    }

    private void updateImagePager() {
        adapter.setImageUris(parent.getImageUris());
        adapter.notifyDataSetChanged();
        indicatorAdapter.setPages(parent.getImageUris().size());
        indicatorAdapter.notifyDataSetChanged();
        setNextNavigation();
        photoPager.invalidate();
    }

    private void setNextNavigation() {
        if (parent.getImageUris() == null || parent.getImageUris().size() == 0) {
            next.setVisibility(View.GONE);
            removeImage.setVisibility(View.GONE);
        }
        else {
            next.setVisibility(View.VISIBLE);
            removeImage.setVisibility(View.VISIBLE);
        }
    }
}
