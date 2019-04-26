package singareddy.productionapps.capturethemoment.auth;

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
import singareddy.productionapps.capturethemoment.user.AuthenticationViewModel;

public class MobileLogin extends AppCompatActivity implements View.OnClickListener, AuthListener.Mobile {
    private static String TAG = "MobileLogin";

    View loginButton;
    EditText mobileNumber, otpCode;
    AuthViewModel authViewModel;
    AuthenticationViewModel authenticationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initialiseViewModel();
        mobileNumber = findViewById(R.id.activity_mobile_login_mobile);
        otpCode = findViewById(R.id.activity_mobile_login_otp);
        otpCode.setVisibility(View.GONE);
        loginButton = findViewById(R.id.email_signup_bt_continue);
        loginButton.setOnClickListener(this);
    }

    private void initialiseViewModel() {
        AuthModelFactory factory = AuthModelFactory.createFactory(this);
        authViewModel = ViewModelProviders.of(this, factory).get(AuthViewModel.class);
        authViewModel.setMobileAuthListener(this);
    }

    @Override
    public void onClick(View v) {
        Log.i(TAG, "onClick: *");
        if (v == loginButton) {
            loginButton.setEnabled(false);
            authViewModel.authorizePhoneCredentials(
                    mobileNumber.getText().toString().trim(),
                    otpCode.getText().toString().trim());
        }
    }

    @Override
    public void onMobileAuthenticationSuccess() {
        Log.i(TAG, "onMobileAuthenticationSuccess: *");
        Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show();
        Intent homeIntent = new Intent(this, MainActivity.class);
        startActivity(homeIntent);
        finish();
    }

    @Override
    public void onMobileAuthenticationFailure(String failureCode) {
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
    public void onOtpSent() {
        // TODO: Need to implement timer here
    }

    @Override
    public void onOtpRetrievalFailed() {
        // Ask for manual OTP here
        otpCode.setVisibility(View.VISIBLE);
        loginButton.setEnabled(true);
    }

    private void resetAllViews () {
        mobileNumber.setText("");
    }
}