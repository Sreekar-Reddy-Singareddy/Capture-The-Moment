package singareddy.productionapps.capturethemoment.user;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import singareddy.productionapps.capturethemoment.R;

public class SignupActivity extends AppCompatActivity implements TabLayout.BaseOnTabSelectedListener, View.OnClickListener {
    private static String TAG = "SignupActivity";

    TabLayout tabLayout;
    LinearLayout loginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        loginLink = findViewById(R.id.signup_ll_login);
        loginLink.setOnClickListener(this);
        tabLayout = findViewById(R.id.signup_tl_tabs);
        tabLayout.addOnTabSelectedListener(this);
        setMobileSignupView();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (tab.getPosition() == 0) {
            // TODO: Logic for mobile signup
            setMobileSignupView();
        }
        else if (tab.getPosition() == 1) {
            // TODO: Logic for email signup
            setEmailSignupView();
        }
    }

    public void setMobileSignupView () {
        Log.i(TAG, "setMobileSignupView: Activity: "+this);
        MobileSignupFragment fragment = new MobileSignupFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.signup_fl_container, fragment).commit();
    }

    public void setEmailSignupView () {
        EmailSignupFragment fragment = new EmailSignupFragment();
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
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
