package singareddy.productionapps.capturethemoment;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import singareddy.productionapps.capturethemoment.models.User;

/**
 * This class is the single reliable source of
 * entire data communication for the app.
 * Its job is only to deal with the data communication.
 * The logical processing of data is not done here.
 */
public class DataRepository {
    private static String TAG = "DataRepository";
    private static DataRepository DATA_REPOSITORY;

    // Firebase members
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    // Current user details are stored in this
    private User user;

    private DataRepository () {
        DATA_REPOSITORY = new DataRepository();
    }

    /**
     * Returns a singleton instance of this class
     * @return
     */
    public static DataRepository getInstance(){
        if (DATA_REPOSITORY == null) {
            Log.i(TAG, "getInstance: Creating Instance...");
            DATA_REPOSITORY = new DataRepository();
        }
        return DATA_REPOSITORY;
    }


}
