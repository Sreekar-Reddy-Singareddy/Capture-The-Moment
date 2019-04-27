package singareddy.productionapps.capturethemoment.user;

import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import static singareddy.productionapps.capturethemoment.AppUtilities.FailureCodes.*;

import singareddy.productionapps.capturethemoment.R;
import singareddy.productionapps.capturethemoment.auth.AuthModelFactory;
import singareddy.productionapps.capturethemoment.auth.AuthViewModel;
import singareddy.productionapps.capturethemoment.models.User;

public class ProfileUpdateActivity extends AppCompatActivity implements View.OnClickListener, ProfileListener {
    private static String TAG = "ProfileUpdateActivity";

    EditText mobile, email, age, location, name;
    Spinner gender;
    Button save;

    // Viewmodel members
    AuthViewModel authViewModel;
    AuthenticationViewModel authenticationViewModel;
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
        save.setOnClickListener(this);
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
    }

    @Override
    public void onProfileUpdated() {
        Log.i(TAG, "onProfileUpdated: *");
        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        // Once the profile is updated, finish this activity
        finish();
    }
}
