package singareddy.productionapps.capturethemoment.user.profile;

import android.Manifest;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
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
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import singareddy.productionapps.capturethemoment.AppUtilities;
import singareddy.productionapps.capturethemoment.BuildConfig;
import singareddy.productionapps.capturethemoment.R;
import singareddy.productionapps.capturethemoment.user.auth.AuthModelFactory;
import singareddy.productionapps.capturethemoment.user.auth.AuthViewModel;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    static String TAG = "ProfileFragment";

    TextView name;
    ImageView profilePic;
    ImageView editProfilePic;
    Button editProfile;

    // Viewmodel layer members
    AuthViewModel authViewModel;
    SharedPreferences userProfileCache;
    SharedPreferences.OnSharedPreferenceChangeListener userProfileCacheListener;

    public ProfileFragment () {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: *");
        initialiseViewModel();
        View view = inflater.inflate(R.layout.profile_fragment, container, false);
        name = view.findViewById(R.id.profile_fragment_name_data);
        profilePic = view.findViewById(R.id.profile_fragment_profile_pic);
        editProfile = view.findViewById(R.id.profile_bt_edit);
        editProfilePic = view.findViewById(R.id.profile_fragment_image_edit);
        editProfile.setOnClickListener(this);
        editProfilePic.setOnClickListener(this);
        return view;
    }

    private void initialiseViewModel() {
        AuthModelFactory factory = AuthModelFactory.createFactory(getActivity());
        authViewModel = ViewModelProviders.of(this, factory).get(AuthViewModel.class);
        userProfileCacheListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                ProfileFragment.this.intialiseProfile();
            }
        };
        userProfileCache = authViewModel.getUserProfileData();
    }

    private void intialiseProfile() {
        name.setText(userProfileCache.getString("name", ""));
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

    @Override
    public void onClick(View v) {
        if (v == editProfile) {
            Intent editProfileIntent = new Intent(getContext(), ProfileUpdateActivity.class);
            startActivity(editProfileIntent);
        }
    }
}
