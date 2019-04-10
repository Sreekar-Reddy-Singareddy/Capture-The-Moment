package singareddy.productionapps.capturethemoment.user;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import singareddy.productionapps.capturethemoment.MainActivity;
import singareddy.productionapps.capturethemoment.R;

public class MobileLoginActivity extends AppCompatActivity implements View.OnClickListener, AuthenticationListener.Mobile {
    private static String TAG = "MobileLoginActivity";

    View loginButton;
    EditText mobileOrOtp;
    Long mobileNumber;

    AuthenticationViewModel authenticationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_login);
        authenticationViewModel = ViewModelProviders.of(this).get(AuthenticationViewModel.class);
        authenticationViewModel.setMobileLoginListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mobileOrOtp = findViewById(R.id.activity_mobile_login_mobile);
        loginButton = findViewById(R.id.email_signup_bt_continue);
        loginButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Log.i(TAG, "onClick: *");
        if (v == loginButton) {
            // TODO: Start the mobile login or signup here
            // TODO: If auto-OTP fails handle UI for manual-OTP
            loginButton.setEnabled(false);
            authenticationViewModel.authorizePhoneCredentials(mobileOrOtp.getText().toString());
            resetAllViews();
            mobileOrOtp.setHint("OTP");
        }
    }

    @Override
    public void onMobileAuthenticationSuccess(String mobile) {
        Log.i(TAG, "onMobileAuthenticationSuccess: *");
        Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show();
        Intent homeIntent = new Intent(this, MainActivity.class);
        startActivity(homeIntent);
        finish();
    }

    @Override
    public void onMobileAuthenticationFailure(String mobile, String failureCode) {
        Log.i(TAG, "onMobileAuthenticationFailure: *");
        loginButton.setEnabled(true);
        if (failureCode.equals("EMPTY_FIELDS")) {
            Toast.makeText(this, "Mobile number is invalid", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "Login Failure", Toast.LENGTH_SHORT).show();
        resetAllViews();
    }

    @Override
    public void onMobileFirstTimeLogin(String mobile) {
        Log.i(TAG, "onMobileFirstTimeLogin: *");
        loginButton.setEnabled(true);
        Toast.makeText(this, "First time user!", Toast.LENGTH_SHORT).show();
        resetAllViews();
        finish();
    }

    private void resetAllViews () {
        mobileOrOtp.setText("");
    }
}
