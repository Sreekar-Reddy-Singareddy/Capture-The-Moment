package singareddy.productionapps.capturethemoment.user.auth;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import singareddy.productionapps.capturethemoment.HomeActivity;
import singareddy.productionapps.capturethemoment.R;
import singareddy.productionapps.capturethemoment.utils.AppUtilities;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, AuthListener.EmailLogin {
    private static String TAG = "LoginActivity";

    EditText email, password;
    View signup, loginUsingMobile, passwordHelp;
    Button login;
    ProgressBar loadingProgressBar;
    AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent homeIntent = new Intent(this, HomeActivity.class);
            startActivity(homeIntent);
            finish();
        }
        initialiseViewModel();
        initialiseUI();
    }

    private void initialiseUI() {
        setTheme(AppUtilities.CURRENT_THEME);
        setContentView(R.layout.activity_login);
        loadingProgressBar = findViewById(R.id.login_pb_loading);
        signup = findViewById(R.id.signup_ll_login);
        loginUsingMobile = findViewById(R.id.login_ll_mobile);
        login = findViewById(R.id.login_bt_continue);
        passwordHelp = findViewById(R.id.login_ll_password_help);
        email = findViewById(R.id.login_et_email);
        password = findViewById(R.id.login_et_password);
        signup.setOnClickListener(this);
        loginUsingMobile.setOnClickListener(this);
        login.setOnClickListener(this);
        passwordHelp.setOnClickListener(this);
    }

    private void initialiseViewModel() {
        AuthModelFactory factory = AuthModelFactory.createFactory(this);
        authViewModel = ViewModelProviders.of(this, factory).get(AuthViewModel.class);
        authViewModel.setEmailLoginListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == signup) {
            Intent signupIntent = new Intent(this, SignupActivity.class);
            startActivity(signupIntent);
            finish();
        }
        else if (v == loginUsingMobile) {
            Intent mobileLoginIntent = new Intent(this, MobileLogin.class);
            startActivity(mobileLoginIntent);
        }
        else if (v == login) {
            if (!AppUtilities.isInternetAvailable(this)) {
                Snackbar.make(findViewById(R.id.login_et_email), "Check your internet connection.", Snackbar.LENGTH_LONG).show();
            }
            toggleLoginLoader();
            String email = this.email.getText().toString().toLowerCase();
            String password = this.password.getText().toString();
            authViewModel.loginUserWithEmail(email, password);
        }
        else if (v == passwordHelp) {
            authViewModel.sendPasswordResetEmail(email.getText().toString());
        }
    }

    private void toggleLoginLoader() {
        if (login.getVisibility() == View.VISIBLE) {
            login.setVisibility(View.INVISIBLE);
            loadingProgressBar.setVisibility(View.VISIBLE);
        }
        else {
            login.setVisibility(View.VISIBLE);
            loadingProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onEmailUserLoginSuccess() {
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
    public void onEmailUserLoginFailure(String failureCode) {
        toggleLoginLoader();
        if (failureCode.equals("EMPTY_FIELDS")) {
            Toast.makeText(this, "Email and Password must be entered", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @Override
    public void onPasswordResetMailSent(String email) {
        Toast.makeText(this, "Password reset mail sent to "+email, Toast.LENGTH_LONG).show();
    }

    public void resetAllViews () {
        email.setText("");
        password.setText("");
    }
}
