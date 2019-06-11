package singareddy.productionapps.capturethemoment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import singareddy.productionapps.capturethemoment.user.auth.AuthModelFactory;
import singareddy.productionapps.capturethemoment.user.auth.AuthViewModel;
import singareddy.productionapps.capturethemoment.utils.AppUtilities;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";
    private static final int ORANGE_THEME_CODE = 0;
    private static final int BLUE_THEME_CODE = 1;

    private Spinner refreshSpinner;
    private TextView signout;
    private TextView themeSummary;
    private ImageView themeDisplayImageView;
    private View themeSelectionDialogView;
    private CardView orangeTheme;
    private CardView blueTheme;
    private SharedPreferences settingsPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener settingsListener;
    private AlertDialog themeChangeDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingsPreferences = getSharedPreferences(AppUtilities.FileNames.APP_SETTINGS, MODE_PRIVATE);
        settingsListener = this::settingsChanged;
        initialiseUI();
    }

    private void initialiseUI() {
        setTheme(AppUtilities.CURRENT_THEME);
        setContentView(R.layout.activity_settings);
        Drawable icon = getDrawable(R.drawable.back);
        getSupportActionBar().setHomeAsUpIndicator(icon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Initialise from shared preferences
        String selectedTheme = settingsPreferences.getString(getString(R.string.theme_change_key),getString(R.string.theme_change_value_orange));

        signout = findViewById(R.id.settings_tv_signout);
        signout.setOnClickListener(this::signout);
        themeSummary = findViewById(R.id.settings_tv_theme_summary);
        themeSummary.setText(selectedTheme);
        themeDisplayImageView = findViewById(R.id.settings_iv_theme);
        themeDisplayImageView.setImageDrawable(getDrawable(R.drawable.theme_display_icon));
        themeDisplayImageView.setOnClickListener(this::changeTheme);
    }

    private void changeTheme(View view) {
        themeSelectionDialogView = getLayoutInflater().inflate(R.layout.list_item_theme, null);
        orangeTheme = themeSelectionDialogView.findViewById(R.id.list_item_theme_orange);
        orangeTheme.setOnClickListener(SettingsActivity.this::themeChangeButtonClicked);
        blueTheme = themeSelectionDialogView.findViewById(R.id.list_item_theme_blue);
        blueTheme.setOnClickListener(SettingsActivity.this::themeChangeButtonClicked);
        themeChangeDialog = new AlertDialog.Builder(this)
                .setView(themeSelectionDialogView)
                .create();
        themeChangeDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        themeChangeDialog.show();
    }

    private void themeChangeButtonClicked(View view) {
        switch (view.getId()) {
            case R.id.list_item_theme_orange:
                settingsPreferences.edit()
                        .putString(
                                getString(R.string.theme_change_key),
                                getString(R.string.theme_change_value_orange))
                        .apply();
                break;
            case R.id.list_item_theme_blue:
                settingsPreferences.edit()
                        .putString(
                                getString(R.string.theme_change_key),
                                getString(R.string.theme_change_value_blue))
                        .apply();
                break;
        }
        // Dismiss the dialog
        themeChangeDialog.dismiss();
        // Set the result code for the sake of calling activity
        setResult(HomeActivity.THEME_CHANGE_RESULT_CODE);
        // Recreate the activity
        recreate();
        finish();
    }

    private void settingsChanged(SharedPreferences sharedPreferences, String key) {
        Log.i(TAG, "settingsChanged: *");
        if (key.equals(getString(R.string.theme_change_key))) {
            // Since the shared preference has changed, the constant should also be changed
            String theme = sharedPreferences.getString(key, getString(R.string.theme_change_value_orange));
            if (theme.equals(getString(R.string.theme_change_value_orange))){
                AppUtilities.CURRENT_THEME = R.style.ThemeOrange;
            }
            else if (theme.equals(getString(R.string.theme_change_value_blue))) {
                AppUtilities.CURRENT_THEME = R.style.ThemeBlue;
            }
        }
    }

    private void signout(View view) {
        Intent signoutIntent = new Intent();
        signoutIntent.putExtra(HomeActivity.LOGOUT_INTENT_KEY, true);
        setResult(RESULT_OK, signoutIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        settingsPreferences.registerOnSharedPreferenceChangeListener(settingsListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        settingsPreferences.unregisterOnSharedPreferenceChangeListener(settingsListener);
    }
}
