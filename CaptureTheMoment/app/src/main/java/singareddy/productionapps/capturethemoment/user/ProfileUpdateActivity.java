package singareddy.productionapps.capturethemoment.user;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import singareddy.productionapps.capturethemoment.R;
import singareddy.productionapps.capturethemoment.models.User;

public class ProfileUpdateActivity extends AppCompatActivity {
    private static String TAG = "ProfileUpdateActivity";

    EditText mobile, email;

    // Viewmodel members
    AuthenticationViewModel authenticationViewModel;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);
        authenticationViewModel = ViewModelProviders.of(this).get(AuthenticationViewModel.class);
        currentUser = authenticationViewModel.loadUserProfile();
        mobile = findViewById(R.id.profile_update_et_mobile);
        email = findViewById(R.id.profile_update_et_email);
        initialiseViews();
    }

    private void initialiseViews() {
        if (!currentUser.getEmailId().equals("NA")) {
            email.setText(currentUser.getEmailId().toLowerCase());
            Log.i(TAG, "initialiseViews: Email: "+currentUser.getEmailId());
        }
        if (currentUser.getMobile() != 0l) {
            mobile.setText(currentUser.getMobile().toString());
        }
    }
}
