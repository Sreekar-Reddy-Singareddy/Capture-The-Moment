package singareddy.productionapps.capturethemoment.user.auth;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import singareddy.productionapps.capturethemoment.R;
import singareddy.productionapps.capturethemoment.utils.AppUtilities;

public class SignupActivity extends AppCompatActivity implements TabLayout.BaseOnTabSelectedListener, View.OnClickListener {
    private static String TAG = "SignupActivity";

    TabLayout tabLayout;
    LinearLayout loginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialiseUI();
    }

    private void initialiseUI() {
        setTheme(AppUtilities.CURRENT_THEME);
        setContentView(R.layout.activity_signup);
        loginLink = findViewById(R.id.signup_ll_login);
        loginLink.setOnClickListener(this);
        tabLayout = findViewById(R.id.signup_tl_tabs);
        tabLayout.addOnTabSelectedListener(this);
        setMobileSignupView();
    }

    public void setMobileSignupView () {
        MobileSignup fragment = new MobileSignup();
        getSupportFragmentManager().beginTransaction().replace(R.id.signup_fl_container, fragment).commit();
    }

    public void setEmailSignupView () {
        EmailSignup fragment = new EmailSignup();
        getSupportFragmentManager().beginTransaction().replace(R.id.signup_fl_container, fragment).commit();
    }

    @Override
    public void onClick(View v) {
        if (v == loginLink) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (tab.getPosition() == 0) {
            setMobileSignupView();
        }
        else if (tab.getPosition() == 1) {
            setEmailSignupView();
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
