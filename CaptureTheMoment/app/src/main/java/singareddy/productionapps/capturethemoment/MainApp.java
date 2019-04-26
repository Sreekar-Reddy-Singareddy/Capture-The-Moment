package singareddy.productionapps.capturethemoment;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

public class MainApp extends Application {
    private static String TAG = "MainApp";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: *");
//        Intent intent = new Intent(this, HomeActivity.class);
//        startActivity(intent);
    }
}
