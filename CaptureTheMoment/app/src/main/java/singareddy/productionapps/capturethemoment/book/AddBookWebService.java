package singareddy.productionapps.capturethemoment.book;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import singareddy.productionapps.capturethemoment.AppUtilities;
import singareddy.productionapps.capturethemoment.models.SecondaryOwner;

public class AddBookWebService {
    private static String TAG = "AddBookWebService";

    private FirebaseDatabase mfirebaseDB;
    private List<SecondaryOwner> validSecOwnersArray = new ArrayList<>();
    private Integer mOwnersValidated;
    private AddBookListener mAddBookListener;

    public AddBookWebService() {
        mfirebaseDB = FirebaseDatabase.getInstance();
    }

    /** STATUS - NOT WORKING
     * This method interacts with the firebase to validate
     * the secondary owners.
     * @param secOwners - List of secondary owners and their edit access
     */
    public void createThisBook (final List<SecondaryOwner> secOwners) {
        // Get reference of the users node
        DatabaseReference targetDataNode = mfirebaseDB.getReference().child(AppUtilities.Firebase.ALL_REGISTERED_USERS_NODE);
        mOwnersValidated = 0;

        // Run a loop
        for (final SecondaryOwner secOwner : secOwners) {
            // Check if this owner has registered in the app or not
            Log.i(TAG, "createThisBook: Username: "+secOwner.getUsername());

            // If the owner is already validated, then skip that owner and continue
            if (secOwner.getValidated() == 1) {
                mOwnersValidated++;
                continue;
            }

            // If not validated, only then the control reaches here
            Query query = targetDataNode.orderByValue().equalTo(secOwner.getUsername().toLowerCase().trim());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.i(TAG, "onDataChange: Value: "+dataSnapshot.getValue());
                    mOwnersValidated++;

                    // This snapshot MUST be non null.
                    if (dataSnapshot.getValue() != null) {
                        // User is valid
                        secOwner.setValidated(1);
                        validSecOwnersArray.add(secOwner);
                    }
                    else {
                        // User is not valid
                        secOwner.setValidated(-1);
                    }
                    mAddBookListener.onThisSecOwnerValidated();

                    // UI must be told once all items have been checked.
                    // This is done ONLY once for every click on the UI button.
                    // That button is enabled again ONLY after this notification is sent to UI.
                    if (mOwnersValidated == secOwners.size()) {
                        mAddBookListener.onAllSecOwnersValidated();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.i(TAG, "onCancelled: Error: "+databaseError.getMessage());
                    // Even though it is an error, the user has been validated once
                    // So, update the count
                    mOwnersValidated++;
                    secOwner.setValidated(-1);
                    mAddBookListener.onThisSecOwnerValidated();
                }
            });
        }
    }

    public void setAddBookListener(AddBookListener mAddBookListener) {
        this.mAddBookListener = mAddBookListener;
    }
}
