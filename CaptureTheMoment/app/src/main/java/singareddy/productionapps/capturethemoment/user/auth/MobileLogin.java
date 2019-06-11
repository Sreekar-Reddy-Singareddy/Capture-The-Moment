package singareddy.productionapps.capturethemoment.user.auth;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import singareddy.productionapps.capturethemoment.HomeActivity;
import singareddy.productionapps.capturethemoment.R;
import singareddy.productionapps.capturethemoment.utils.AppUtilities;

public class MobileLogin extends AppCompatActivity implements View.OnClickListener, AuthListener.Mobile {
    private static String TAG = "MobileLogin";

    Button loginButton;
    EditText mobileNumber, otpInput;
    AuthViewModel authViewModel;
    ProgressBar progressLoader;
    TextView otpRetrievalLabel, otpTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialiseViewModel();
        initialiseUI();
    }

    private void initialiseUI() {
        setTheme(AppUtilities.CURRENT_THEME);
        setContentView(R.layout.activity_mobile_login);
        Drawable icon = getDrawable(R.drawable.back);
        getSupportActionBar().setHomeAsUpIndicator(icon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressLoader = findViewById(R.id.activity_mobile_login_pb_loading);
        otpRetrievalLabel = findViewById(R.id.activity_mobile_login_tv_otp_label);
        otpTimer = findViewById(R.id.activity_mobile_login_tv_time);
        mobileNumber = findViewById(R.id.activity_mobile_login_mobile);
        otpInput = findViewById(R.id.activity_mobile_login_otp);
        otpInput.setVisibility(View.GONE);
        loginButton = findViewById(R.id.login_bt_continue);
        loginButton.setOnClickListener(this);
    }

    private void initialiseViewModel() {
        AuthModelFactory factory = AuthModelFactory.createFactory(this);
        authViewModel = ViewModelProviders.of(this, factory).get(AuthViewModel.class);
        authViewModel.setMobileAuthListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == loginButton) {
            loginButton.setEnabled(false);
            authViewModel.authorizePhoneCredentials(
                    mobileNumber.getText().toString().trim(),
                    otpInput.getText().toString().trim());
        }
    }

    private void toggleLoaderViews() {
        if (otpInput.getVisibility() == View.VISIBLE) {
            if (loginButton.getVisibility() == View.VISIBLE) {
                loginButton.setVisibility(View.INVISIBLE);
                progressLoader.setVisibility(View.VISIBLE);
            }
            else {
                loginButton.setVisibility(View.VISIBLE);
                progressLoader.setVisibility(View.INVISIBLE);
            }
        }
        else {
            if (loginButton.getVisibility() == View.VISIBLE) {
                loginButton.setVisibility(View.INVISIBLE);
                progressLoader.setVisibility(View.VISIBLE);
                otpRetrievalLabel.setVisibility(View.VISIBLE);
                otpTimer.setVisibility(View.VISIBLE);
            }
            else {
                loginButton.setVisibility(View.VISIBLE);
                progressLoader.setVisibility(View.INVISIBLE);
                otpRetrievalLabel.setVisibility(View.INVISIBLE);
                otpTimer.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onMobileAuthenticationSuccess() {
        Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show();
        // On successful login, erase all the data.
        authViewModel.eraseLocalData();
        Intent homeIntent = new Intent(this, HomeActivity.class);
        startActivity(homeIntent);
        // Once data is erased, download this user's data
        authViewModel.setupInitialData();
        finish();
    }

    @Override
    public void onMobileAuthenticationFailure(String failureCode) {
        toggleLoaderViews();
        if (failureCode.equals("EMPTY_FIELDS")) {
            Toast.makeText(this, "Mobile number is invalid", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "Login Failure", Toast.LENGTH_SHORT).show();
        resetAllViews();
    }

    @Override
    public void onOtpSent() {
        OtpRetriveTask otpRetriveTask = new OtpRetriveTask(otpTimer, otpRetrievalLabel, loginButton, progressLoader, otpInput);
        otpRetriveTask.execute();
    }

    @Override
    public void onOtpRetrievalFailed() {
    }

    private void resetAllViews () {
        mobileNumber.setText("");
        otpInput.setVisibility(View.GONE);
    }
}
