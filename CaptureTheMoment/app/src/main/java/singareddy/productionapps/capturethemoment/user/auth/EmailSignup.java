package singareddy.productionapps.capturethemoment.user.auth;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import singareddy.productionapps.capturethemoment.utils.AppUtilities;
import singareddy.productionapps.capturethemoment.HomeActivity;
import singareddy.productionapps.capturethemoment.R;

public class EmailSignup extends Fragment implements View.OnClickListener, AuthListener.EmailSignup {
    private static String TAG = "EmailSignup";

    EditText email, password, confirmPassword;
    View fragView;
    View signupButton;
    AuthViewModel authViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragView = inflater.inflate(R.layout.email_signup, container, false);
        initialiseUI();
        initialiseViewModel();
        return fragView;
    }

    private void initialiseUI() {
        email = fragView.findViewById(R.id.email_signup_et_email);
        password = fragView.findViewById(R.id.email_signup_et_password);
        confirmPassword = fragView.findViewById(R.id.email_signup_et_conf_password);
        signupButton = fragView.findViewById(R.id.login_bt_continue);
        signupButton.setOnClickListener(this);
    }

    private void initialiseViewModel() {
        AuthModelFactory factory = AuthModelFactory.createFactory(getActivity());
        authViewModel = ViewModelProviders.of(this, factory).get(AuthViewModel.class);
        authViewModel.setEmailSignupListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == signupButton) {
            signupButton.setEnabled(false);
            String email = this.email.getText().toString().toLowerCase();
            String password = this.password.getText().toString();
            String confPassword = this.confirmPassword.getText().toString();
            authViewModel.registerEmailUser (email, password, confPassword);
        }
    }

    private void resetAllViews() {
        email.setText("");
        password.setText("");
        confirmPassword.setText("");
    }

    @Override
    public void onEmailUserRegisterSuccess(String email) {
        Toast.makeText(getContext(), "Registered "+ email, Toast.LENGTH_SHORT).show();
        // On successful login, erase all the data.
        authViewModel.eraseLocalData();
        Intent mainIntent = new Intent(getContext(), HomeActivity.class);
        startActivity(mainIntent);
        // Once data is erased, download this user's data
        authViewModel.setupInitialData();
        resetAllViews();
        getActivity().finish();
    }

    @Override
    public void onEmailUserRegisterFailure(String email, String failureCode) {
        signupButton.setEnabled(true);
        switch (failureCode) {
            case AppUtilities.FailureCodes.EMPTY_EMAIL:
                Toast.makeText(getContext(), "Email cannot be empty", Toast.LENGTH_SHORT).show();
                break;
            case AppUtilities.FailureCodes.EMPTY_PASSWORD:
                Toast.makeText(getContext(), "Passwords cannot be empty", Toast.LENGTH_SHORT).show();
                break;
            case AppUtilities.FailureCodes.PASSWORD_MISMATCH:
                Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                password.setText(""); confirmPassword.setText("");
                break;
            case AppUtilities.FailureCodes.EMAIL_EXISTS:
                Toast.makeText(getContext(), "Email is already registered", Toast.LENGTH_SHORT).show();
                resetAllViews();
                break;
        }
    }
}
