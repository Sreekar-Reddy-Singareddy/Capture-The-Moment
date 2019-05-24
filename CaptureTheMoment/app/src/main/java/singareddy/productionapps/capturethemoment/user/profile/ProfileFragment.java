package singareddy.productionapps.capturethemoment.user.profile;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import singareddy.productionapps.capturethemoment.R;
import singareddy.productionapps.capturethemoment.user.auth.AuthModelFactory;
import singareddy.productionapps.capturethemoment.user.auth.AuthViewModel;

import static singareddy.productionapps.capturethemoment.utils.AppUtilities.FileNames.USER_PROFILE_PICTURE;
import static singareddy.productionapps.capturethemoment.utils.AppUtilities.SharedPrefKeys.PROFILE_PIC_AVAILABLE;
import static singareddy.productionapps.capturethemoment.utils.AppUtilities.FBUser.*;
import static singareddy.productionapps.capturethemoment.utils.AppUtilities.Defaults.*;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    static String TAG = "ProfileFragment";

    TextView name, ownedBooks, sharedBooks;
    ImageView profilePic;
    Button editProfile;
    View fragView;

    // Viewmodel layer members
    AuthViewModel authViewModel;
    SharedPreferences userProfileCache;
    SharedPreferences.OnSharedPreferenceChangeListener userProfileCacheListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragView = inflater.inflate(R.layout.profile_fragment, container, false);
        initialiseUI();
        initialiseViewModel();
        return fragView;
    }

    private void initialiseUI() {
        name = fragView.findViewById(R.id.profile_fragment_name_data);
        sharedBooks = fragView.findViewById(R.id.profile_tv_shared_books);
        ownedBooks = fragView.findViewById(R.id.profile_tv_owned_books);
        profilePic = fragView.findViewById(R.id.profile_fragment_profile_pic);
        editProfile = fragView.findViewById(R.id.profile_bt_edit);
        editProfile.setOnClickListener(this);
    }

    private void initialiseViewModel() {
        AuthModelFactory factory = AuthModelFactory.createFactory(getActivity());
        authViewModel = ViewModelProviders.of(this, factory).get(AuthViewModel.class);
        userProfileCacheListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals(PROFILE_PIC_AVAILABLE)) {
                    setProfilePic();
                }
                ProfileFragment.this.intialiseProfile();
            }
        };
        userProfileCache = authViewModel.getUserProfileData();
    }

    private void intialiseProfile() {
        setProfilePic();
        name.setText(userProfileCache.getString(NAME, DEFAULT_STRING));
        sharedBooks.setText(authViewModel.getNumberOfSharedBooks().toString());
        ownedBooks.setText(authViewModel.getNumberOfOwnedBooks().toString());
    }

    private void setProfilePic() {
        File profilePic = new File(getContext().getFilesDir(), USER_PROFILE_PICTURE);
        if (profilePic.exists()) {
            try {
                byte[] imageData = IOUtils.toByteArray(new FileInputStream(profilePic));
                this.profilePic.setImageBitmap(BitmapFactory.decodeByteArray(imageData, 0, imageData.length));
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    @Override
    public void onClick(View v) {
        if (v == editProfile) {
            Intent editProfileIntent = new Intent(getContext(), ProfileUpdateActivity.class);
            startActivity(editProfileIntent);
        }
    }
}
