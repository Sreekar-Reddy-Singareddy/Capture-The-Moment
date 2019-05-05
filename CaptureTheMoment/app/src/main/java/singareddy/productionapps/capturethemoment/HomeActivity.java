package singareddy.productionapps.capturethemoment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import singareddy.productionapps.capturethemoment.user.auth.AuthModelFactory;
import singareddy.productionapps.capturethemoment.user.auth.AuthViewModel;
import singareddy.productionapps.capturethemoment.book.addbook.AddBookActivity;
import singareddy.productionapps.capturethemoment.book.getbooks.GetBooksFragment;
import singareddy.productionapps.capturethemoment.user.auth.LoginActivity;
import singareddy.productionapps.capturethemoment.user.profile.ProfileFragment;
import singareddy.productionapps.capturethemoment.user.profile.ProfileUpdateActivity;

import static singareddy.productionapps.capturethemoment.AppUtilities.User.*;
import static singareddy.productionapps.capturethemoment.AppUtilities.Firebase.*;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = "HomeActivity";

    NavigationView navigationView;
    TextView userName;
    ImageView profilePic;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    FloatingActionButton addBookFab;

    AuthViewModel authViewModel;
    SharedPreferences userProfileCache;
    SharedPreferences.OnSharedPreferenceChangeListener userProfileCacheListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialiseViewModel();
        initialiseUI();
        initialiseUserProfile();
        initialiseStaticConstants();
    }

    private void initialiseViewModel() {
        AuthModelFactory factory = AuthModelFactory.createFactory(this);
        authViewModel = ViewModelProviders.of(this, factory).get(AuthViewModel.class);
        userProfileCache = authViewModel.getUserProfileData();
        userProfileCacheListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals("name")) {
                    String username = sharedPreferences.getString(key, "Welcome!");
                    userName.setText(username);
                    if (username.equals("Welcome!") || username.equals("")) {
                        AlertDialog dialog = new AlertDialog.Builder(HomeActivity.this)
                                .setIcon(R.drawable.ic_launcher_foreground)
                                .setTitle("Update Profile")
                                .setMessage("Hi there! Your profile has not yet been updated. Update it for better experience. Do you want to do it now?")
                                .setPositiveButton("Let's do it", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent profileUpdateIntent = new Intent(HomeActivity.this, ProfileUpdateActivity.class);
                                        startActivity(profileUpdateIntent);
                                    }
                                })
                                .setNegativeButton("Not now", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Simply close the dialog
                                    }
                                }).create();
                        dialog.show();
                    }
                }
                else if (key.equals("profilePicAvailable")) {
                    setProfilePic();
                }
            }
        };
    }

    private void initialiseUI() {
        // Initially, show all the books
        GetBooksFragment getBooksFragment = new GetBooksFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_container, getBooksFragment).commit();
        getSupportActionBar().setTitle("Books");

        drawerLayout = findViewById(R.id.activity_main_drawer_layout);
        navigationView = findViewById(R.id.acctivity_main_navigation_view);
        View headerView = navigationView.getHeaderView(0);
        userName = headerView.findViewById(R.id.nav_header_tv_name);
        profilePic = headerView.findViewById(R.id.nav_header_iv_pic);
        addBookFab = findViewById(R.id.activity_main_add_book_button);
        addBookFab.setOnClickListener(this);

        Log.i(TAG, "onCreate: Nav: "+navigationView);
        userName.setText("Welcome!");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home_black_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initialiseUserProfile() {
        String username = userProfileCache.getString("name", "Welcome!");
        userName.setText(username);
    }

    private void initialiseStaticConstants() {
        Log.i(TAG, "initialiseStaticConstants: ***");
        CURRENT_USER = FirebaseAuth.getInstance().getCurrentUser();
        CURRENT_USER_ID = CURRENT_USER.getUid();
        LOGIN_PROVIDER = CURRENT_USER.getProviders().get(0);
        CURRENT_USER_EMAIL = LOGIN_PROVIDER.equals(EMAIL_PROVIDER) ?
                CURRENT_USER.getEmail() : null;
        CURRENT_USER_MOBILE = LOGIN_PROVIDER.equals(PHONE_PROVIDER) ?
                CURRENT_USER.getPhoneNumber().substring(3) : null;
    }

    private void setProfilePic() {
        Log.i(TAG, "setProfilePic: PROFILE PIC!!");
        File profilePic = new File(this.getFilesDir(), "profile_pic.jpg");
        if (profilePic.exists()) {
            try {
                byte[] imageData = IOUtils.toByteArray(new FileInputStream(profilePic));
                this.profilePic.setImageBitmap(BitmapFactory.decodeByteArray(imageData, 0, imageData.length));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == addBookFab) {
            // Navigate to the next activity - AddBookActivity
            Intent addBookIntent = new Intent(this, AddBookActivity.class);
            startActivity(addBookIntent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            // Open the navigation drawer
            drawerLayout.openDrawer(Gravity.LEFT);
            return true;
        }
        if (menuItem.getItemId() == R.id.main_nav_menu_home_item) {
            getSupportActionBar().setTitle("Books");
            addBookFab.show();
            GetBooksFragment getBooksFragment = new GetBooksFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_container, getBooksFragment).commit();
        }
        else if (menuItem.getItemId() == R.id.main_nav_menu_profile_item) {
            getSupportActionBar().setTitle("Profile");
            addBookFab.hide();
            ProfileFragment profileFragment = new ProfileFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_container, profileFragment).commit();
        }
        else if (menuItem.getItemId() == R.id.main_nav_menu_signout_item) {
            authViewModel.logout();
            Intent loginActivity = new Intent(this, LoginActivity.class);
            startActivity(loginActivity);
            finish();
        }
        drawerLayout.closeDrawer(Gravity.LEFT);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        userProfileCache.registerOnSharedPreferenceChangeListener(userProfileCacheListener);
        setProfilePic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        userProfileCache.unregisterOnSharedPreferenceChangeListener(userProfileCacheListener);
    }
}
