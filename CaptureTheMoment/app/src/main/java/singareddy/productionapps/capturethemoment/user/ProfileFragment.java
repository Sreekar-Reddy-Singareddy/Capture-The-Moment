package singareddy.productionapps.capturethemoment.user;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
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

import singareddy.productionapps.capturethemoment.R;
import singareddy.productionapps.capturethemoment.auth.AuthModelFactory;
import singareddy.productionapps.capturethemoment.auth.AuthViewModel;
import singareddy.productionapps.capturethemoment.models.User;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    static String TAG = "ProfileFragment";

    TextView name;
    ImageView profilePic;
    Button editProfile;

    // Viewmodel layer members
    AuthenticationViewModel authenticationViewModel;
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
        editProfile = view.findViewById(R.id.profile_bt_edit);
        editProfile.setOnClickListener(this);
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
