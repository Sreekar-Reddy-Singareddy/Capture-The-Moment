package singareddy.productionapps.capturethemoment.card.delete;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import singareddy.productionapps.capturethemoment.utils.AppUtilities;

public class DeleteCardService {
    private static String TAG = "DeleteCardService";

    private FirebaseStorage storage;
    private FirebaseDatabase database;

    public DeleteCardService () {
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    public void deleteCardWithId(String cardId) {
        deleteCardFromFirebaseDB(cardId);
    }

    private void deleteCardFromFirebaseDB(String cardId) {
        DatabaseReference cardNodeToDelete = database.getReference().child(AppUtilities.Firebase.ALL_CARDS_NODE).child(cardId);
        cardNodeToDelete.removeValue().addOnSuccessListener(this::cardDeleteSuccess);
        
    }

    private void cardDeleteSuccess(Void aVoid) {
        Log.i(TAG, "cardDeleteSuccess: Card Deleted from Firebase DB");
    }
}
