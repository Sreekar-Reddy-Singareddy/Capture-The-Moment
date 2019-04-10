package singareddy.productionapps.capturethemoment.user;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
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
import singareddy.productionapps.capturethemoment.models.User;

public class ProfileUpdateActivity extends AppCompatActivity implements View.OnClickListener, ProfileListener {
    private static String TAG = "ProfileUpdateActivity";

    EditText mobile, email, age, location, name;
    Spinner gender;
    Button save;

    // Viewmodel members
    AuthenticationViewModel authenticationViewModel;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);
        authenticationViewModel = ViewModelProviders.of(this).get(AuthenticationViewModel.class);
        authenticationViewModel.setProfileListener(this);

        name = findViewById(R.id.profile_update_et_name);
        age = findViewById(R.id.profile_update_et_age);
        gender = findViewById(R.id.profile_update_sn_gender);
        location = findViewById(R.id.profile_update_et_location);
        mobile = findViewById(R.id.profile_update_et_mobile);
        email = findViewById(R.id.profile_update_et_email);
        save = findViewById(R.id.profile_update_bt_save);
        save.setOnClickListener(this);
        initialiseViews();
    }

    private void initialiseViews() {
        currentUser = authenticationViewModel.loadUserProfile();
        name.setText(!currentUser.getName().equals("NA") ? currentUser.getName() : "");
        age.setText(currentUser.getAge() != 0 ? currentUser.getAge().toString() : "");
        email.setText(!currentUser.getEmailId().equals("NA") ? currentUser.getEmailId().toLowerCase() : "");
        mobile.setText(currentUser.getMobile() != 0l ? currentUser.getMobile().toString() : "");
        location.setText(!currentUser.getLocation().equals("NA") ? currentUser.getLocation() : "");
    }

    @Override
    public void onClick(View v) {
        if (v == save) {
            // TODO: Here we will take the entered details and save them
            save.setEnabled(false);
            String name = this.name.getText().toString();
            Integer age = Integer.parseInt(this.age.getText().toString());
            String location = this.location.getText().toString();
            Long mobile = Long.parseLong(this.mobile.getText().toString());
            String email = this.email.getText().toString();
            User updatedUser = new User(name,mobile,age,"NA",email);
            updatedUser.setLocation(location);
            authenticationViewModel.updateUserProfile(updatedUser);
        }
    }

    @Override
    public void onProfileUpdated() {
        Log.i(TAG, "onProfileUpdated: *");
        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        // Once the profile is updated, finish this activity
        finish();
    }

    @Override
    public void onProfileUpdateFailed(String failureCause) {
        Log.i(TAG, "onProfileUpdateFailed: *");
        switch (failureCause) {
            case PROFILE_NAME_EMPTY:
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
        }

    }
}
