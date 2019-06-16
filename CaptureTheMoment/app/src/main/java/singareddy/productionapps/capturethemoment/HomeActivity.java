package singareddy.productionapps.capturethemoment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import singareddy.productionapps.capturethemoment.user.auth.AuthModelFactory;
import singareddy.productionapps.capturethemoment.user.auth.AuthViewModel;
import singareddy.productionapps.capturethemoment.book.add.AddBookActivity;
import singareddy.productionapps.capturethemoment.book.get.GetBooksFragment;
import singareddy.productionapps.capturethemoment.user.profile.ProfileFragmentNew;
import singareddy.productionapps.capturethemoment.utils.AppUtilities;
import singareddy.productionapps.capturethemoment.utils.SwipeComputer;

import static singareddy.productionapps.capturethemoment.utils.AppUtilities.User.*;
import static singareddy.productionapps.capturethemoment.utils.AppUtilities.Firebase.*;

public class HomeActivity extends AppCompatActivity {
    private static String TAG = "HomeActivity";

    private final static int HOME_TAB = 0;
    private final static int PROFILE_TAB = 1;
    private final static int SETTINGS_TAB = 2;
    public final static String LOGOUT_INTENT_KEY = "LogoutKey";
    private static final int SETTINGS_SCREEN_REQUEST_CODE = 123;
    public static final int THEME_CHANGE_RESULT_CODE = 345;

    View container;
    Toolbar toolbar;
    FloatingActionButton addBookFab;
    TabLayout tabLayout;
    int selectedTab;
    AuthViewModel authViewModel;
    SharedPreferences userProfileCache;
    SharedPreferences.OnSharedPreferenceChangeListener userProfileCacheListener;
    private TabLayout.OnTabSelectedListener tabListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialiseUI();
        initialiseViewModel();
        initialiseStaticConstants();
    }

    private void initialiseUI() {
        setTheme(AppUtilities.CURRENT_THEME);
        setContentView(R.layout.activity_main);
        // Initially, show all the books
        GetBooksFragment getBooksFragment = new GetBooksFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_container, getBooksFragment).commit();
        getSupportActionBar().setTitle("Books");

        container = findViewById(R.id.activity_main_container);
        addBookFab = findViewById(R.id.activity_main_add_book_button);
        tabLayout = findViewById(R.id.activity_main_tab_layout);
        tabListener = new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectedTab = tab.getPosition();
                switch (selectedTab) {
                    case HOME_TAB:
                        // Home item selected
                        showHomeScreen();
                        break;
                    case PROFILE_TAB:
                        // Profile item selected
                        showProfileScreen();
                        break;
                    case SETTINGS_TAB:
                        // TODO: Settings screen
                        showSettingsScreen();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        };
        addBookFab.setOnClickListener(this::addBook);
    }

    private void initialiseViewModel() {
        AuthModelFactory factory = AuthModelFactory.createFactory(this);
        authViewModel = ViewModelProviders.of(this, factory).get(AuthViewModel.class);
        userProfileCache = authViewModel.getUserProfileData();

        userProfileCacheListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (!key.equals(AppUtilities.FBUser.NAME)) return;
                String username = userProfileCache.getString(AppUtilities.FBUser.NAME, AppUtilities.Defaults.DEFAULT_STRING);
                if (username.equals(AppUtilities.Defaults.DEFAULT_STRING)) {
                    AlertDialog dialog = new AlertDialog.Builder(HomeActivity.this)
                            .setIcon(R.drawable.ic_launcher_foreground)
                            .setTitle("Update Profile")
                            .setMessage("Hi there! Your profile has not yet been updated. Update it for better experience. Do you want to do it now?")
                            .setPositiveButton("Let's do it", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    showProfileScreen();
                                    tabLayout.getTabAt(PROFILE_TAB).select();
                                }
                            })
                            .setNegativeButton("Not now", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Simply circular_close the dialog
                                }
                            }).create();
                    dialog.show();
                }
            }
        };
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

    private void showHomeScreen() {
        Log.i(TAG, "showHomeScreen: HOME SCREEN");
        getSupportActionBar().setTitle("Books");
        addBookFab.show();
        GetBooksFragment getBooksFragment = new GetBooksFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_main_container, getBooksFragment)
                .commit();
    }

    private void showProfileScreen() {
        getSupportActionBar().setTitle("Profile");
        addBookFab.hide();
        ProfileFragmentNew profileFragment = new ProfileFragmentNew();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_main_container, profileFragment)
                .addToBackStack("Profile")
                .commit();
    }

    private void showSettingsScreen() {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivityForResult(settingsIntent, SETTINGS_SCREEN_REQUEST_CODE);
    }

    public void addBook(View addBookFab) {
        if (addBookFab == addBookFab) {
            // Navigate to the next activity - AddBookActivity
            Intent addBookIntent = new Intent(this, AddBookActivity.class);
            startActivity(addBookIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        tabLayout.addOnTabSelectedListener(tabListener);
        userProfileCache.registerOnSharedPreferenceChangeListener(userProfileCacheListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        tabLayout.removeOnTabSelectedListener(tabListener);
        userProfileCache.unregisterOnSharedPreferenceChangeListener(userProfileCacheListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult: Request Code: "+requestCode+" Data: "+data);
        if (requestCode == SETTINGS_SCREEN_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            boolean logoutFlag = data.getExtras().getBoolean(LOGOUT_INTENT_KEY);
            if (logoutFlag) authViewModel.logout();
            finish();
        }
        else if (requestCode == SETTINGS_SCREEN_REQUEST_CODE) {
            if (resultCode == THEME_CHANGE_RESULT_CODE) recreate();
            tabLayout.getTabAt(HOME_TAB).select();
            showHomeScreen();
        }
    }
}
