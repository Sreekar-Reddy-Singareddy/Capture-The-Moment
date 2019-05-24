package singareddy.productionapps.capturethemoment.user.profile;

import android.Manifest;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import singareddy.productionapps.capturethemoment.BuildConfig;
import singareddy.productionapps.capturethemoment.R;
import singareddy.productionapps.capturethemoment.user.auth.AuthModelFactory;
import singareddy.productionapps.capturethemoment.user.auth.AuthViewModel;
import singareddy.productionapps.capturethemoment.models.User;

import static singareddy.productionapps.capturethemoment.utils.AppUtilities.ScreenTitles.EDIT_PROFILE_SCREEN;
import static singareddy.productionapps.capturethemoment.utils.AppUtilities.FBUser.*;
import static singareddy.productionapps.capturethemoment.utils.AppUtilities.Defaults.*;
import static singareddy.productionapps.capturethemoment.utils.AppUtilities.FileNames.*;

public class ProfileUpdateActivity extends AppCompatActivity implements View.OnClickListener, ProfileListener {
    private static String TAG = "ProfileUpdateActivity";
    static final int CAMERA_PERMISSION_REQUEST = 1;
    static final int STORAGE_PERMISSION_REQUEST = 2;
    static final int CAMERA_INTENT_REQUEST = 3;
    static final int GALLERY_INTENT_REQUEST = 4;

    ImageView profilePic, cameraButton, galleryButton, editProfilePic;
    EditText mobile, email, age, location, name;
    Spinner gender;
    Button save;

    // Viewmodel members
    AuthViewModel authViewModel;
    SharedPreferences userProfileCache;

    // Photo URIs
    private Uri capturedUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialiseUI();
        initialiseViewModel();
        initialiseUserProfile();
    }

    private void initialiseViewModel() {
        AuthModelFactory factory = AuthModelFactory.createFactory(this);
        authViewModel = ViewModelProviders.of(this, factory).get(AuthViewModel.class);
        authViewModel.setProfileListener(this);
        userProfileCache = authViewModel.getUserProfileData();
    }

    private void initialiseUI() {
        getSupportActionBar().setTitle(EDIT_PROFILE_SCREEN);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_profile_update);
        name = findViewById(R.id.profile_update_et_name);
        age = findViewById(R.id.profile_update_et_age);
        gender = findViewById(R.id.profile_update_sn_gender);
        location = findViewById(R.id.profile_update_et_location);
        mobile = findViewById(R.id.profile_update_et_mobile);
        email = findViewById(R.id.profile_update_et_email);
        save = findViewById(R.id.profile_update_bt_save);
        profilePic = findViewById(R.id.profile_update_iv_profilepic);
        editProfilePic = findViewById(R.id.profile_update_iv_edit_dp);
        save.setOnClickListener(this);
        editProfilePic.setOnClickListener(this);
    }

    private void initialiseUserProfile() {
        String uname = userProfileCache.getString(NAME, DEFAULT_STRING);
        Integer uage = userProfileCache.getInt(AGE, DEFAULT_INT);
        String uemail = userProfileCache.getString(EMAIL, DEFAULT_STRING);
        Long umobile = userProfileCache.getLong(MOBILE, DEFAULT_LONG);
        String ulocation = userProfileCache.getString(LOCATION, DEFAULT_STRING);
        Bitmap profilePic = authViewModel.setProfilePic(this);
        if (profilePic != null) {
            this.profilePic.setImageBitmap(profilePic);
        }

        name.setText(uname);
        if (uage != null && uage != DEFAULT_INT) age.setText(uage.toString());
        email.setText(uemail);
        if (umobile != null && umobile != DEFAULT_LONG) mobile.setText(umobile.toString());
        location.setText(ulocation);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == save) {
            save.setEnabled(false);
            String name = this.name.getText().toString();
            Integer age = Integer.parseInt(this.age.getText().toString());
            String location = this.location.getText().toString();
            Long mobile = Long.parseLong(this.mobile.getText().toString());
            String email = this.email.getText().toString();
            User userProfileToUpdate = new User(name,mobile,age,(String) this.gender.getSelectedItem(),email);
            userProfileToUpdate.setLocation(location);
            authViewModel.updateUserProfile(userProfileToUpdate);
        }
        else if (v == editProfilePic) {
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
                    dialog.dismiss();
                    openCameraForPicture();
                }
            });
            galleryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    openGalleryForPicture();
                }
            });
        }
    }

    private void openCameraForPicture() {
        if (this.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},CAMERA_PERMISSION_REQUEST);
            return;
        }
        // All permissions granted
        capturedUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID+".provider",new File("storage/emulated/0/newPic.jpg"));
        Intent cameraIntent = new Intent();
        cameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedUri);
        startActivityForResult(cameraIntent, CAMERA_INTENT_REQUEST);
    }

    private void openGalleryForPicture() {
        if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION_REQUEST);
            return;
        }
        // All permissions granted
        Intent pickIntent = new Intent();
        pickIntent.setAction(Intent.ACTION_PICK);
        pickIntent.setType("image/*");
        startActivityForResult(pickIntent, GALLERY_INTENT_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED  && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            openCameraForPicture();
        }
        else if (requestCode == STORAGE_PERMISSION_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGalleryForPicture();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_INTENT_REQUEST && resultCode == RESULT_OK) {
            // Crop the image here
            cropImageAt(capturedUri);
        }
        else if (requestCode == GALLERY_INTENT_REQUEST && resultCode == RESULT_OK) {
            // Crop the image here
            cropImageAt(data.getData());
        }
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            // Either image is captured or picked,
            // once it is cropped the resulting URI comes here.
            // The URI is saved in cache.
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri croppedImageUri = result.getUri();
            // Use the result to set the image
            profilePic.setImageURI(croppedImageUri);
            // Once image is set, save it in Firebase
            byte[] imageData = convertUriToBytes(croppedImageUri);
            saveProfilePic(imageData);
        }
    }

    /**
     * Called both when image is captured from camera
     * or picked from gallery. This is called before
     * the image is set to the image view.
     * @param capturedUri
     */
    private void cropImageAt(Uri capturedUri) {
        CropImage.activity(capturedUri)
                .setAspectRatio(1,1)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    /**
     * Called to convert image at this Uri to byte data.
     * This is called after the image is cropped (usually).
     * @param imageUri
     * @return
     */
    private byte[] convertUriToBytes (Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            byte[] imageData = IOUtils.toByteArray(inputStream);
            return imageData;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveProfilePic (byte[] imageData) {
        try {
            FileOutputStream outputStream = openFileOutput(USER_PROFILE_PICTURE, Context.MODE_PRIVATE);
            outputStream.write(imageData);
            outputStream.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            File profilePicFile = new File(getFilesDir(), USER_PROFILE_PICTURE);
            if (profilePicFile.exists()) {
                Uri profilePicUri = Uri.fromFile(profilePicFile);
                authViewModel.saveProfilePic(profilePicUri);
            }
        }
    }

    @Override
    public void onProfileUpdated() {
        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        // Once the profile is updated, finish this activity
        finish();
    }

    @Override
    public void onProfilePicUpdated() {
        Toast.makeText(this, "Picture changed!", Toast.LENGTH_SHORT).show();
    }
}
