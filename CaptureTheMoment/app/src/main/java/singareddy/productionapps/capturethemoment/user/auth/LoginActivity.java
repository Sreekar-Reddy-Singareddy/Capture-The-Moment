package singareddy.productionapps.capturethemoment.user.auth;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import singareddy.productionapps.capturethemoment.HomeActivity;
import singareddy.productionapps.capturethemoment.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, AuthListener.EmailLogin {
    private static String TAG = "LoginActivity";

    EditText email, password;
    View signup, loginUsingMobile, login, passwordHelp;

    AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Log.i(TAG, "onCreate: CURRENT USER");
            Intent homeIntent = new Intent(this, HomeActivity.class);
            startActivity(homeIntent);
            finish();
        }
        setContentView(R.layout.activity_login);
        initialiseViewModel();
        signup = findViewById(R.id.signup_ll_login);
        loginUsingMobile = findViewById(R.id.login_ll_mobile);
        login = findViewById(R.id.email_signup_bt_continue);
        passwordHelp = findViewById(R.id.login_ll_password_help);
        email = findViewById(R.id.login_et_email); email.setText("sreekesh@gmail.com");
        password = findViewById(R.id.login_et_password); password.setText("Sree@123");
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
            login.setEnabled(false);
            String email = this.email.getText().toString().toLowerCase();
            String password = this.password.getText().toString();
            authViewModel.loginUserWithEmail(email, password);
//            authenticationViewModel.loginUserWithEmailCredentials(email, password);
        }
        else if (v == passwordHelp) {
            // TODO: UI to send reset password link to email
        }
    }

    @Override
    public void onEmailUserLoginSuccess() {
        Log.i(TAG, "onEmailUserLoginSuccess: *");
        Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show();
        // On successful login, erase all the data.
        authViewModel.eraseLocalData();
        Log.i(TAG, "onEmailUserLoginSuccess: ALL DATA ERASED");
        Intent homeIntent = new Intent(this, HomeActivity.class);
        startActivity(homeIntent);
        // Once data is erased, download this user's data
        authViewModel.setupInitialData();
        finish();
    }

    @Override
    public void onEmailUserLoginFailure(String failureCode) {
        Log.i(TAG, "onEmailUserLoginFailure: *");
        login.setEnabled(true);
        if (failureCode.equals("EMPTY_FIELDS")) {
            Toast.makeText(this, "Email and Password must be entered", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "Login Failure", Toast.LENGTH_SHORT).show();
        resetAllViews();
    }

    public void resetAllViews () {
        email.setText("");
        password.setText("");
    }
}
