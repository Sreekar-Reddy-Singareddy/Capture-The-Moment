package singareddy.productionapps.capturethemoment.user.auth;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import singareddy.productionapps.capturethemoment.HomeActivity;
import singareddy.productionapps.capturethemoment.R;

public class MobileSignup extends Fragment implements AuthListener.Mobile, View.OnClickListener {
    private static String TAG = "MobileSignup";

    View fragmentView;
    Button loginButton;
    EditText mobileNumber, otpInput;
    private AuthViewModel authViewModel;
    ProgressBar progressLoader;
    TextView otpRetrievalLabel, otpTimer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.activity_mobile_login, container, false);
        initialiseUI();
        initialiseViewModel();
        return fragmentView;
    }

    private void initialiseUI() {
        progressLoader = fragmentView.findViewById(R.id.activity_mobile_login_pb_loading);
        otpRetrievalLabel = fragmentView.findViewById(R.id.activity_mobile_login_tv_otp_label);
        otpTimer = fragmentView.findViewById(R.id.activity_mobile_login_tv_time);
        mobileNumber = fragmentView.findViewById(R.id.activity_mobile_login_mobile);
        otpInput = fragmentView.findViewById(R.id.activity_mobile_login_otp);
        otpInput.setVisibility(View.GONE);
        loginButton = fragmentView.findViewById(R.id.login_bt_continue);
        loginButton.setOnClickListener(this);
    }

    private void initialiseViewModel() {
        AuthModelFactory factory = AuthModelFactory.createFactory(getActivity());
        authViewModel = ViewModelProviders.of(this, factory).get(AuthViewModel.class);
        authViewModel.setMobileAuthListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == loginButton) {
            toggleLoaderViews();
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
        Toast.makeText(getContext(), "Login Success", Toast.LENGTH_SHORT).show();
        // On successful login, erase all the data.
        authViewModel.eraseLocalData();
        Intent homeIntent = new Intent(getContext(), HomeActivity.class);
        startActivity(homeIntent);
        // Once data is erased, download this user's data
        authViewModel.setupInitialData();
        getActivity().finish();
    }

    @Override
    public void onMobileAuthenticationFailure(String failureCode) {
        toggleLoaderViews();
        if (failureCode.equals("EMPTY_FIELDS")) {
            Toast.makeText(getContext(), "Mobile number is invalid", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(getContext(), "Login Failure", Toast.LENGTH_SHORT).show();
        resetAllViews();
    }

    @Override
    public void onOtpSent() {
//        Log.i(TAG, "onOtpSent: Timer label: "+otpTimer);
        OtpRetriveTask otpRetriveTask = new OtpRetriveTask(otpTimer, otpRetrievalLabel, loginButton, progressLoader, otpInput);
        otpRetriveTask.execute();
    }

    @Override
    public void onOtpRetrievalFailed() {
        // Ask for manual OTP here
//        toggleLoaderViews();
//        otpInput.setVisibility(View.VISIBLE);
    }

    private void resetAllViews () {
        mobileNumber.setText("");
        otpInput.setVisibility(View.GONE);
    }
}
