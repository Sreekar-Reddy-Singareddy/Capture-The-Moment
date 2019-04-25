package singareddy.productionapps.capturethemoment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import singareddy.productionapps.capturethemoment.book.addbook.AddBookActivity;
import singareddy.productionapps.capturethemoment.book.getbooks.GetBooksFragment;
import singareddy.productionapps.capturethemoment.models.User;
import singareddy.productionapps.capturethemoment.user.AuthenticationListener;
import singareddy.productionapps.capturethemoment.user.AuthenticationViewModel;
import singareddy.productionapps.capturethemoment.user.LoginActivity;
import singareddy.productionapps.capturethemoment.user.ProfileFragment;
import singareddy.productionapps.capturethemoment.user.ProfileListener;
import singareddy.productionapps.capturethemoment.user.ProfileUpdateActivity;

public class MainActivity extends AppCompatActivity implements AuthenticationListener.Logout, ProfileListener.InitialProfile, View.OnClickListener {
    private static String TAG = "MainActivity";

    NavigationView navigationView;
    TextView userName;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    FloatingActionButton addBookFab;

    AuthenticationViewModel authenticationViewModel;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initially, show all the books
        GetBooksFragment getBooksFragment = new GetBooksFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_container, getBooksFragment).commit();

        authenticationViewModel = ViewModelProviders.of(this).get(AuthenticationViewModel.class);
        authenticationViewModel.setLogoutListener(this);
        authenticationViewModel.setInitialProfileListener(this);
        if (!AppUtilities.UPDATE_PROFILE_DIALOG_SHOWN) {
            authenticationViewModel.checkIfUserLoggedInFirstTime();
        }

        drawerLayout = findViewById(R.id.activity_main_drawer_layout);
        navigationView = findViewById(R.id.acctivity_main_navigation_view);
        View headerView = navigationView.getHeaderView(0);
        userName = headerView.findViewById(R.id.nav_header_tv_name);
        addBookFab = findViewById(R.id.activity_main_add_book_button);
        addBookFab.setOnClickListener(this);

        Log.i(TAG, "onCreate: Nav: "+navigationView);
        userName.setText("Welcome!");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home_black_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadViews();
    }

    /**
     * Loads initial views with data
     */
    private void loadViews() {
        currentUser = authenticationViewModel.loadUserProfile();
        String name = currentUser.getName();
        if (name != null && !name.equals("NA")) {
            userName.setText(name);
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
            GetBooksFragment getBooksFragment = new GetBooksFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_container, getBooksFragment).commit();
        }
        else if (menuItem.getItemId() == R.id.main_nav_menu_profile_item) {
            ProfileFragment profileFragment = new ProfileFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_container, profileFragment).commit();
        }
        else if (menuItem.getItemId() == R.id.main_nav_menu_signout_item) {
            authenticationViewModel.logout();
        }
        drawerLayout.closeDrawer(Gravity.LEFT);
        return true;
    }

    @Override
    public void onUserLoggedOut() {
        Log.i(TAG, "onUserLoggedOut: *");
        Intent loginActivity = new Intent(this, LoginActivity.class);
        startActivity(loginActivity);
        finish();
    }

    @Override
    public void onUserProfilePending() {
        Log.i(TAG, "onUserProfilePending: *");
        // TODO: Prompt the user to update the profileUser
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_launcher_foreground)
                .setTitle("Update Profile")
                .setMessage("Hi there! Your profile has not yet been updated. Update it for better experience. Do you want to do it now?")
                .setPositiveButton("Let's do it", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: Take user to update profile screen
                        Intent profileUpdateIntent = new Intent(MainActivity.this, ProfileUpdateActivity.class);
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
        // Once the dialog is displayed, the user should not be interrupted again
        // with it. Hence as long as the app runs, this is remembered.
        AppUtilities.UPDATE_PROFILE_DIALOG_SHOWN = true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "onSaveInstanceState: ***");
        super.onSaveInstanceState(outState);
        outState.putBoolean("PROFILE_DIALOG", true);
    }

    @Override
    public void onClick(View v) {
        if (v == addBookFab) {
            // Navigate to the next activity - AddBookActivity
            Intent addBookIntent = new Intent(this, AddBookActivity.class);
            startActivity(addBookIntent);
        }
    }
}
