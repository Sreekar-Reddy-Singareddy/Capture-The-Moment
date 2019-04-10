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

import singareddy.productionapps.capturethemoment.AppUtilities;
import singareddy.productionapps.capturethemoment.MainActivity;
import singareddy.productionapps.capturethemoment.R;
import singareddy.productionapps.capturethemoment.models.User;

public class EmailSignupFragment extends Fragment implements View.OnClickListener, AuthenticationListener.EmailSignup {
    private static String TAG = "EmailSignupFragment";

    EditText email, password, confirmPassword;
    View signupButton;
    AuthenticationViewModel authenticationViewModel;

    public EmailSignupFragment () {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.email_signup, container, false);
        authenticationViewModel = ViewModelProviders.of(this).get(AuthenticationViewModel.class);
        authenticationViewModel.setEmailSignupListener(this);
        email = view.findViewById(R.id.email_signup_et_email);
        password = view.findViewById(R.id.email_signup_et_password);
        confirmPassword = view.findViewById(R.id.email_signup_et_conf_password);
        signupButton = view.findViewById(R.id.email_signup_bt_continue);
        signupButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == signupButton) {
            signupButton.setEnabled(false);
            // TODO: Logic here to take and register the credentials
            String email = this.email.getText().toString().toLowerCase();
            String password = this.password.getText().toString();
            String confPassword = this.confirmPassword.getText().toString();
            User user = new User();
            user.setEmailId(email);
            authenticationViewModel.registerUserWithEmailCredentials(user, password, confPassword);
        }
    }

    private void resetAllViews() {
        email.setText("");
        password.setText("");
        confirmPassword.setText("");
    }

    @Override
    public void onEmailUserRegisterSuccess(String email) {
        Log.i(TAG, "onEmailUserRegisterSuccess: *");
        Toast.makeText(getContext(), "Registered "+ email, Toast.LENGTH_SHORT).show();
        Intent mainIntent = new Intent(getContext(), MainActivity.class);
        startActivity(mainIntent);
        resetAllViews();
        getActivity().finish();
    }

    @Override
    public void onEmailUserRegisterFailure(String email, String failureCode) {
        Log.i(TAG, "onEmailUserRegisterFailure: *");
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
