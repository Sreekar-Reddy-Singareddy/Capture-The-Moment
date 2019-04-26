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
import android.widget.EditText;
import android.widget.Toast;

import singareddy.productionapps.capturethemoment.MainActivity;
import singareddy.productionapps.capturethemoment.R;
import singareddy.productionapps.capturethemoment.auth.AuthenticationListener;

public class MobileSignupFragment extends Fragment implements AuthenticationListener.Mobile, View.OnClickListener {
    private static String TAG = "MobileSignupFragment";

    View loginButton;
    EditText mobileOrOtp;

    AuthenticationViewModel authenticationViewModel;

    public MobileSignupFragment () {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: *");
        Log.i(TAG, "onCreateView: Activity: "+getActivity());
        View view = inflater.inflate(R.layout.activity_mobile_login, container, false);
        authenticationViewModel = ViewModelProviders.of(this).get(AuthenticationViewModel.class);
        authenticationViewModel.setMobileLoginListener(this);
        mobileOrOtp = view.findViewById(R.id.activity_mobile_login_mobile);
        loginButton = view.findViewById(R.id.email_signup_bt_continue);
        loginButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: *");
        super.onCreate(savedInstanceState);
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
    public void onMobileAuthenticationSuccess() {
        Log.i(TAG, "onMobileAuthenticationSuccess: *");
        Toast.makeText(getContext(), "Login Success", Toast.LENGTH_SHORT).show();
        Intent homeIntent = new Intent(getContext(), MainActivity.class);
        startActivity(homeIntent);
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

    private void resetAllViews () {
        mobileOrOtp.setText("");
    }
}
