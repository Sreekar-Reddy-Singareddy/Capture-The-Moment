package singareddy.productionapps.capturethemoment.card.delete;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import singareddy.productionapps.capturethemoment.models.Card;
import singareddy.productionapps.capturethemoment.utils.AppUtilities;

public class DeleteCardService {
    private static String TAG = "DeleteCardService";

    private FirebaseStorage storage;
    private FirebaseDatabase database;
    private DeleteCardListener deleteCardListener;
    private Card card;

    public DeleteCardService () {
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    public void deleteCardWithId(Card card) {
        deleteCardFromFirebaseDB(card);
    }

    private void deleteCardFromFirebaseDB(Card card) {
        this.card = card;
        DatabaseReference cardNodeToDelete = database.getReference().child(AppUtilities.Firebase.ALL_CARDS_NODE).child(card.getCardId());
        cardNodeToDelete.removeValue()
                .addOnSuccessListener(this::cardDeleteSuccess)
                .addOnFailureListener(this::cardDeleteFailure);
        
    }

    private void deleteCardIDFromItsBook(Card card) {
        DatabaseReference bookOfThisCard = database.getReference().child(AppUtilities.Firebase.ALL_BOOKS_NODE)
                .child(card.getBookId())
                .child("cards");
        bookOfThisCard.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> cardIds = (ArrayList<String>) dataSnapshot.getValue();
                cardIds.remove(card.getCardId());
                bookOfThisCard.setValue(cardIds)
                        .addOnSuccessListener(DeleteCardService.this::cardIdDeleteSuccess)
                        .addOnFailureListener(DeleteCardService.this::cardIdDeleteFailure);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteCardImagesFromFirebaseStorage(Card card) {
        Log.i(TAG, "deleteCardImagesFromFirebaseStorage: PATHS: \n"+card.getImagePaths());
        for (String path: card.getImagePaths()) {
            StorageReference ref = storage.getReference().child(path);
            ref.delete()
                    .addOnFailureListener(this::cardImagesDeleteFailure)
                    .addOnSuccessListener(this::cardImagesDeleteSuccess);
        }
    }

    private void cardImagesDeleteFailure(Exception e) {
        Log.i(TAG, "cardImagesDeleteFailure: Image NOT deleted. "+e.getLocalizedMessage());
    }

    private void cardImagesDeleteSuccess(Void aVoid) {
        Log.i(TAG, "cardImagesDeleteSuccess: Image deleted.");
        deleteCardListener.onCardDeleted(card.getCardId());
    }

    private void cardDeleteFailure(Exception e) {
        Log.i(TAG, "cardDeleteFailure: Card NOT deleted from Firebase DB: "+e.getLocalizedMessage());
    }

    private void cardDeleteSuccess(Void aVoid) {
        Log.i(TAG, "cardDeleteSuccess: Card Deleted from Firebase DB");
        deleteCardIDFromItsBook(card);
    }

    private void cardIdDeleteFailure(Exception e) {
        Log.i(TAG, "onFailure: Card ID not deleted from its book: " +e.getLocalizedMessage());
    }

    private void cardIdDeleteSuccess(Void aVoid) {
        Log.i(TAG, "onSuccess: Card ID deleted from its book");
        deleteCardImagesFromFirebaseStorage(card);
    }

    public void setDeleteCardListener(DeleteCardListener deleteCardListener) {
        this.deleteCardListener = deleteCardListener;
    }
}
