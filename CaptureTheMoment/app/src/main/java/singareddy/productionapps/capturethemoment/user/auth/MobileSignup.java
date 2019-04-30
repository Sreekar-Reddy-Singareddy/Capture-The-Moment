package singareddy.productionapps.capturethemoment.user.auth;

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
import android.widget.EditText;
import android.widget.Toast;

import singareddy.productionapps.capturethemoment.HomeActivity;
import singareddy.productionapps.capturethemoment.R;

public class MobileSignup extends Fragment implements AuthListener.Mobile, View.OnClickListener {
    private static String TAG = "MobileSignup";

    View loginButton;
    EditText mobileNumber, otpCode;

    private AuthViewModel authViewModel;

    public MobileSignup() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: *");
        Log.i(TAG, "onCreateView: Activity: "+getActivity());
        View view = inflater.inflate(R.layout.activity_mobile_login, container, false);
        initialiseViewModel();
        mobileNumber = view.findViewById(R.id.activity_mobile_login_mobile);
        otpCode = view.findViewById(R.id.activity_mobile_login_otp);
        otpCode.setVisibility(View.GONE);
        loginButton = view.findViewById(R.id.email_signup_bt_continue);
        loginButton.setOnClickListener(this);
        return view;
    }

    private void initialiseViewModel() {
        AuthModelFactory factory = AuthModelFactory.createFactory(getActivity());
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
        Log.i(TAG, "onMobileAuthenticationFailure: *");
        loginButton.setEnabled(true);
        if (failureCode.equals("EMPTY_FIELDS")) {
            Toast.makeText(getContext(), "Mobile number is invalid", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(getContext(), "Login Failure", Toast.LENGTH_SHORT).show();
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
