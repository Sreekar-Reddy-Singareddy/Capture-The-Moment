package singareddy.productionapps.capturethemoment.user.profile;

import android.Manifest;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import singareddy.productionapps.capturethemoment.BuildConfig;
import singareddy.productionapps.capturethemoment.HomeActivity;
import singareddy.productionapps.capturethemoment.R;
import singareddy.productionapps.capturethemoment.models.User;
import singareddy.productionapps.capturethemoment.user.auth.AuthModelFactory;
import singareddy.productionapps.capturethemoment.user.auth.AuthViewModel;
import singareddy.productionapps.capturethemoment.utils.AppUtilities;

import static android.app.Activity.RESULT_OK;
import static singareddy.productionapps.capturethemoment.utils.AppUtilities.Defaults.DEFAULT_LONG;
import static singareddy.productionapps.capturethemoment.utils.AppUtilities.Defaults.DEFAULT_STRING;
import static singareddy.productionapps.capturethemoment.utils.AppUtilities.FBUser.ABOUT;
import static singareddy.productionapps.capturethemoment.utils.AppUtilities.FBUser.EMAIL;
import static singareddy.productionapps.capturethemoment.utils.AppUtilities.FBUser.LOCATION;
import static singareddy.productionapps.capturethemoment.utils.AppUtilities.FBUser.MOBILE;
import static singareddy.productionapps.capturethemoment.utils.AppUtilities.FBUser.NAME;
import static singareddy.productionapps.capturethemoment.utils.AppUtilities.FileNames.USER_PROFILE_PICTURE;
import static singareddy.productionapps.capturethemoment.utils.AppUtilities.SharedPrefKeys.PROFILE_PIC_AVAILABLE;

public class ProfileFragmentNew extends Fragment implements View.OnClickListener, ProfileListener {
    private static String TAG = "ProfileFragmentNew";

    static final int CAMERA_PERMISSION_REQUEST = 1;
    static final int STORAGE_PERMISSION_REQUEST = 2;
    static final int CAMERA_INTENT_REQUEST = 3;
    static final int GALLERY_INTENT_REQUEST = 4;
    private static final String POS_BUTTON = "Save";
    private static final String NEG_BUTTON = "Cancel";
    private static final int NAME_INPUT = 1;
    private static final int ABOUT_INPUT = 2;
    private static final int MOBILE_INPUT = 3;
    private static final int EMAIL_INPUT = 4;
    private static final int LOCATION_INPUT = 5;

    TextView name, about, mobile, email, location;
    ImageView profilePicture;
    FloatingActionButton changeProfilePicture;
    ImageView editName, editAbout, editMobile, editEmail, editLocation, cameraButton, galleryButton;
    View fragView;
    HomeActivity parent;
    View dialogView;
    EditText textInput;
    AlertDialog textInputDialog;

    // Viewmodel layer members
    private User currentUser;
    private int fieldBeingEdited;
    private Uri capturedUri;
    AuthViewModel authViewModel;
    SharedPreferences userProfileCache;
    SharedPreferences.OnSharedPreferenceChangeListener userProfileCacheListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        parent = (HomeActivity) getActivity(); 
        fragView = inflater.inflate(R.layout.profile_fragment_new, container, false);
        initialiseUI();
        initialiseViewModel();
        return fragView;
    }

    private void initialiseUI() {
        dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);
        textInput = dialogView.findViewById(R.id.dialog_profile_et_input);
        textInputDialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setPositiveButton(POS_BUTTON, this::onSaveClick)
                .setNegativeButton(NEG_BUTTON, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        textInputDialog.getWindow().setBackgroundDrawableResource(R.color.colorOrangeLight);

        name = fragView.findViewById(R.id.profile_tv_name);
        about = fragView.findViewById(R.id.profile_tv_about);
        mobile = fragView.findViewById(R.id.profile_tv_mobile);
        email = fragView.findViewById(R.id.profile_tv_email);
        location = fragView.findViewById(R.id.profile_tv_location);

        editName = fragView.findViewById(R.id.profile_iv_edit_name);
        editAbout = fragView.findViewById(R.id.profile_iv_edit_about);
        editMobile = fragView.findViewById(R.id.profile_iv_edit_mobile);
        editEmail = fragView.findViewById(R.id.profile_iv_edit_email);
        editLocation = fragView.findViewById(R.id.profile_iv_edit_location);
        editName.setOnClickListener(this);
        editAbout.setOnClickListener(this);
        editMobile.setOnClickListener(this);
        editEmail.setOnClickListener(this);
        editLocation.setOnClickListener(this);

        profilePicture = fragView.findViewById(R.id.profile_iv_image);
        changeProfilePicture = fragView.findViewById(R.id.profile_fab_edit_picture);
        changeProfilePicture.setOnClickListener(this);

        if (AppUtilities.User.LOGIN_PROVIDER.equals(AppUtilities.Firebase.EMAIL_PROVIDER))
            editEmail.setVisibility(View.INVISIBLE);
        else if (AppUtilities.User.LOGIN_PROVIDER.equals(AppUtilities.Firebase.PHONE_PROVIDER))
            editMobile.setVisibility(View.INVISIBLE);
    }

    private void initialiseViewModel() {
        AuthModelFactory factory = AuthModelFactory.createFactory(getActivity());
        authViewModel = ViewModelProviders.of(this, factory).get(AuthViewModel.class);
        authViewModel.setProfileListener(this);
        userProfileCacheListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals(PROFILE_PIC_AVAILABLE)) {
                    setProfilePic();
                }
                ProfileFragmentNew.this.intialiseProfile();
            }
        };
        userProfileCache = authViewModel.getUserProfileData();
    }

    private void intialiseProfile() {
        currentUser = new User();
        setProfilePic();
        String name = userProfileCache.getString(NAME, DEFAULT_STRING);
        String about = userProfileCache.getString(ABOUT, DEFAULT_STRING);
        Long mobile = userProfileCache.getLong(MOBILE, DEFAULT_LONG);
        String email = userProfileCache.getString(EMAIL, DEFAULT_STRING);
        String location = userProfileCache.getString(LOCATION, DEFAULT_STRING);
        currentUser.setName(name);
        currentUser.setAbout(about);
        currentUser.setMobile(mobile);
        currentUser.setEmailId(email);
        currentUser.setLocation(location);
        this.name.setText(name);
        this.about.setText(about);
        this.mobile.setText(mobile != DEFAULT_LONG ? String.valueOf(mobile) : "");
        this.email.setText(email);
        this.location.setText(location);
    }

    private void askForTextInput(String currentData) {
        textInput.setText(currentData);
        textInputDialog.show();
    }

    private void setProfilePic() {
        File profilePic = new File(getContext().getFilesDir(), USER_PROFILE_PICTURE);
        if (profilePic.exists()) {
            try {
                byte[] imageData = IOUtils.toByteArray(new FileInputStream(profilePic));
                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inSampleSize = 2;
                Bitmap profImage = BitmapFactory.decodeByteArray(imageData, 0, imageData.length, opt);
                System.out.println("Image Size: "+profImage.getByteCount()/1E6+" MB");
                profilePicture.setImageBitmap(profImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onSaveClick(DialogInterface dialog, int which) {
        switch (fieldBeingEdited) {
            case NAME_INPUT:
                String name = textInput.getText().toString().trim();
                if (name.equals(currentUser.getName())) return;
                this.name.setText(name);
                currentUser.setName(name);
                authViewModel.updateUserProfile(currentUser);
                break;
            case ABOUT_INPUT:
                String about = textInput.getText().toString().trim();
                if (about.equals(currentUser.getAbout())) return;
                this.about.setText(about);
                currentUser.setAbout(about);
                authViewModel.updateUserProfile(currentUser);
                break;
            case MOBILE_INPUT:
                String mobile = textInput.getText().toString().trim();
                Long convertedMobile = authViewModel.validateMobile(mobile);
                if (convertedMobile == null || convertedMobile.equals(currentUser.getMobile())) return;
                this.mobile.setText(convertedMobile.toString());
                currentUser.setMobile(convertedMobile);
                authViewModel.updateUserProfile(currentUser);
                break;
            case EMAIL_INPUT:
                String email = textInput.getText().toString().trim();
                if (email.equals(currentUser.getEmailId())) return;
                this.email.setText(email);
                currentUser.setEmailId(email);
                authViewModel.updateUserProfile(currentUser);
                break;
            case LOCATION_INPUT:
                String location = textInput.getText().toString().trim();
                if (location.equals(currentUser.getLocation())) return;
                this.location.setText(location);
                currentUser.setLocation(location);
                authViewModel.updateUserProfile(currentUser);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (v == changeProfilePicture) {
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
        else if (v == editName) {
            fieldBeingEdited = NAME_INPUT;
            askForTextInput(name.getText().toString());
        }
        else if (v == editAbout) {
            fieldBeingEdited = ABOUT_INPUT;
            askForTextInput(about.getText().toString());
        }
        else if (v == editMobile) {
            fieldBeingEdited = MOBILE_INPUT;
            askForTextInput(mobile.getText().toString());
        }
        else if (v == editEmail) {
            fieldBeingEdited = EMAIL_INPUT;
            askForTextInput(email.getText().toString());
        }
        else if (v == editLocation) {
            fieldBeingEdited = LOCATION_INPUT;
            askForTextInput(location.getText().toString());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        userProfileCache.registerOnSharedPreferenceChangeListener(userProfileCacheListener);
        intialiseProfile();
    }

    @Override
    public void onPause() {
        super.onPause();
        userProfileCache.unregisterOnSharedPreferenceChangeListener(userProfileCacheListener);
    }

    private void openCameraForPicture() {
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

    private void openGalleryForPicture() {
        if (getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
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
            profilePicture.setImageURI(croppedImageUri);
            // Once image is set, save it in Firebase
            byte[] imageData = convertUriToBytes(croppedImageUri);
            saveProfilePic(imageData);
        }
    }

    /**
     * Called both when image is captured from camera
     * or picked from gallery. getContext() is called before
     * the image is set to the image view.
     * @param capturedUri
     */
    private void cropImageAt(Uri capturedUri) {
        CropImage.activity(capturedUri)
                .setAspectRatio(AppUtilities.IMAGE_CROP_RATIO_X,AppUtilities.IMAGE_CROP_RATIO_Y)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(getContext(), this);
    }

    /**
     * Called to convert image at getContext() Uri to byte data.
     * getContext() is called after the image is cropped (usually).
     * @param imageUri
     * @return
     */
    private byte[] convertUriToBytes (Uri imageUri) {
        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(imageUri);
            byte[] imageData = IOUtils.toByteArray(inputStream);
            return imageData;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveProfilePic (byte[] imageData) {
        try {
            FileOutputStream outputStream = getContext().openFileOutput(USER_PROFILE_PICTURE, Context.MODE_PRIVATE);
            outputStream.write(imageData);
            outputStream.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            File profilePicFile = new File(getContext().getFilesDir(), USER_PROFILE_PICTURE);
            if (profilePicFile.exists()) {
                Uri profilePicUri = Uri.fromFile(profilePicFile);
                authViewModel.saveProfilePic(profilePicUri);
            }
        }
    }

    @Override
    public void onProfileUpdated() {
        Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProfilePicUpdated() {
        Toast.makeText(getContext(), "Picture changed!", Toast.LENGTH_SHORT).show();
    }

}
