package singareddy.productionapps.capturethemoment.user;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
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
import singareddy.productionapps.capturethemoment.models.User;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    static String TAG = "ProfileFragment";

    TextView name, mobile, age, email, gender;
    ImageView profilePic;
    Button editProfile;

    // Viewmodel layer members
    AuthenticationViewModel authenticationViewModel;

    public ProfileFragment () {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: *");
        View view = inflater.inflate(R.layout.profile_fragment, container, false);
        name = view.findViewById(R.id.profile_fragment_name_data);
        mobile = view.findViewById(R.id.profile_fragment_mobile_data);
        age = view.findViewById(R.id.profile_fragment_age_data);
        email = view.findViewById(R.id.profile_fragment_email_data);
        gender = view.findViewById(R.id.profile_fragment_gender_data);
        editProfile = view.findViewById(R.id.profile_bt_edit);
        editProfile.setOnClickListener(this);
        authenticationViewModel = ViewModelProviders.of(this).get(AuthenticationViewModel.class);
        loadProfileData();
        return view;
    }

    /**
     * This method asks the Viewmodel to display the profile details of the user
     */
    private void loadProfileData() {
        User user = authenticationViewModel.loadUserProfile();
        name.setText(user.getName());
        mobile.setText(user.getMobile().toString());
        age.setText(user.getAge().toString());
        gender.setText(user.getGender());
        email.setText(user.getEmailId());
    }

    @Override
    public void onClick(View v) {
        if (v == editProfile) {
            Intent editProfileIntent = new Intent(getContext(), ProfileUpdateActivity.class);
            startActivity(editProfileIntent);
        }
    }
}
