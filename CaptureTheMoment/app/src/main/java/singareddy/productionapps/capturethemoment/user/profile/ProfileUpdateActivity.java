package singareddy.productionapps.capturethemoment.user.profile;

import android.Manifest;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import singareddy.productionapps.capturethemoment.R;
import singareddy.productionapps.capturethemoment.user.auth.AuthModelFactory;
import singareddy.productionapps.capturethemoment.user.auth.AuthViewModel;
import singareddy.productionapps.capturethemoment.models.User;

public class ProfileUpdateActivity extends AppCompatActivity implements View.OnClickListener, ProfileListener {
    private static String TAG = "ProfileUpdateActivity";
    static final int CAMERA_PERMISSION_REQUEST = 1;
    static final int STORAGE_PERMISSION_REQUEST = 2;
    static final int CAMERA_INTENT_REQUEST = 3;
    static final int GALLERY_INTENT_REQUEST = 4;

    ImageView profilePic, cameraButton, galleryButton;
    EditText mobile, email, age, location, name;
    Spinner gender;
    Button save;

    // Viewmodel members
    AuthViewModel authViewModel;

    User currentUser;
    SharedPreferences userProfileCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialiseViewModel();
        initialiseUI();
        initialiseUserProfile();
    }

    private void initialiseViewModel() {
        AuthModelFactory factory = AuthModelFactory.createFactory(this);
        authViewModel = ViewModelProviders.of(this, factory).get(AuthViewModel.class);
        authViewModel.setProfileListener(this);
        userProfileCache = authViewModel.getUserProfileData();
    }

    private void initialiseUI() {
        setContentView(R.layout.activity_profile_update);
        name = findViewById(R.id.profile_update_et_name);
        age = findViewById(R.id.profile_update_et_age);
        gender = findViewById(R.id.profile_update_sn_gender);
        location = findViewById(R.id.profile_update_et_location);
        mobile = findViewById(R.id.profile_update_et_mobile);
        email = findViewById(R.id.profile_update_et_email);
        save = findViewById(R.id.profile_update_bt_save);
        profilePic = findViewById(R.id.profile_update_iv_profilepic);
        save.setOnClickListener(this);
        profilePic.setOnClickListener(this);
    }

    private void initialiseUserProfile() {
        String uname = userProfileCache.getString("name", "");
        Integer uage = userProfileCache.getInt("age", 0);
        String uemail = userProfileCache.getString("email", "");
        Long umobile = userProfileCache.getLong("mobile", 0l);
        String ulocation = userProfileCache.getString("location", "");

        name.setText(uname);
        if (uage != null || uage != 0) age.setText(uage.toString());
        email.setText(uemail);
        if (umobile != null || umobile != 0) mobile.setText(umobile.toString());
        location.setText(ulocation);
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
        else if (v == profilePic) {
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
                    openCameraForPicture();
                }
            });
            galleryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "onClick: GALLERY");
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
        Intent cameraIntent = new Intent();
        cameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
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
            Log.i(TAG, "onActivityResult: IMAGE CAPTURED");
            Log.i(TAG, "onActivityResult: DATA URI: "+data.getExtras().get("data"));
            profilePic.setImageBitmap(((Bitmap) data.getExtras().get("data")));
        }
        else if (requestCode == GALLERY_INTENT_REQUEST && resultCode == RESULT_OK) {
            Log.i(TAG, "onActivityResult: PICKED");
            Log.i(TAG, "onActivityResult: DATA URI: "+data.getData().getPath());
            profilePic.setImageURI(data.getData());
        }
    }

    private void uploadProfilePic() {
        
    }

    @Override
    public void onProfileUpdated() {
        Log.i(TAG, "onProfileUpdated: *");
        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        // Once the profile is updated, finish this activity
        finish();
    }
}
