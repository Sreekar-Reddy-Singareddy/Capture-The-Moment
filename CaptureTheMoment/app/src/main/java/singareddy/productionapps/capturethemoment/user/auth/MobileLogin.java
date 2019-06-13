package singareddy.productionapps.capturethemoment.user.auth;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import singareddy.productionapps.capturethemoment.R;
import singareddy.productionapps.capturethemoment.utils.AppUtilities;

public class MobileLogin extends AppCompatActivity implements AuthListener.Mobile {
    private static String TAG = "MobileLogin";

    AuthViewModel authViewModel;
    ConstraintLayout parentLayout;
    private MobileAuthLogic mobileAuthLogic;

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
        parentLayout = findViewById(R.id.activity_mobile_login_parent_layout);
        mobileAuthLogic = new MobileAuthLogic(this, parentLayout, authViewModel);
    }

    private void initialiseViewModel() {
        AuthModelFactory factory = AuthModelFactory.createFactory(this);
        authViewModel = ViewModelProviders.of(this, factory).get(AuthViewModel.class);
        authViewModel.setMobileAuthListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMobileAuthenticationSuccess() {
        mobileAuthLogic.loginSuccess();
        finish();
    }

    @Override
    public void onMobileAuthenticationFailure(String failureCode) {
        mobileAuthLogic.loginFailed(failureCode);
    }

    @Override
    public void onOtpSent() {
        Log.i(TAG, "onOtpSent: **");
        mobileAuthLogic.autoRetrieveOtp();   
    }

    @Override
    public void onOtpRetrievalFailed() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        authViewModel.reset();
    }
}
