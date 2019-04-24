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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, AuthenticationListener.EmailLogin{
    private static String TAG = "LoginActivity";

    EditText email, password;
    View signup, loginUsingMobile, login, passwordHelp;
    AuthenticationViewModel authenticationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        authenticationViewModel = ViewModelProviders.of(this).get(AuthenticationViewModel.class);
        authenticationViewModel.setEmailLoginListener(this);
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

    @Override
    public void onClick(View v) {
        if (v == signup) {
            // TODO: Present UI to choose between phone and email signup
            Intent signupIntent = new Intent(this, SignupActivity.class);
            startActivity(signupIntent);
            finish();
        }
        else if (v == loginUsingMobile) {
            // TODO: UI to login using mobile number
            Intent mobileLoginIntent = new Intent(this, MobileLoginActivity.class);
            startActivity(mobileLoginIntent);
        }
        else if (v == login) {
            // TODO: Logic to login using email
            login.setEnabled(false);
            String email = this.email.getText().toString().toLowerCase();
            String password = this.password.getText().toString();
            authenticationViewModel.loginUserWithEmailCredentials(email, password);
        }
        else if (v == passwordHelp) {
            // TODO: UI to send reset password link to email
        }
    }

    @Override
    public void onEmailUserLoginSuccess() {
        Log.i(TAG, "onEmailUserLoginSuccess: *");
        Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show();
        Intent homeIntent = new Intent(this, MainActivity.class);
        startActivity(homeIntent);
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
