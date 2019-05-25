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

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DeleteCardListener deleteCardListener;
    private Card card;

    public void deleteCardWithId(Card card) {
        deleteCardFromFirebaseDB(card);
    }

    private void deleteCardFromFirebaseDB(Card card) {
        this.card = card;
        DatabaseReference cardNodeToDelete = database.getReference().child(AppUtilities.Firebase.ALL_CARDS_NODE).child(card.getCardId());
        cardNodeToDelete.removeValue()
                .addOnSuccessListener(this::cardDeleteSuccess);
        
    }

    private void deleteCardIDFromItsBook(Card card) {
        DatabaseReference bookOfThisCard = database.getReference().child(AppUtilities.Firebase.ALL_BOOKS_NODE)
                .child(card.getBookId())
                .child(AppUtilities.Firebase.KEY_BOOK_CARDS);
        bookOfThisCard.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> cardIds = (ArrayList<String>) dataSnapshot.getValue();
                cardIds.remove(card.getCardId());
                bookOfThisCard.setValue(cardIds)
                        .addOnSuccessListener(DeleteCardService.this::cardIdDeleteSuccess);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteCardImagesFromFirebaseStorage(Card card) {
        for (String path: card.getImagePaths()) {
            StorageReference ref = storage.getReference().child(path);
            ref.delete()
                    .addOnSuccessListener(this::cardImagesDeleteSuccess);
        }
    }

    private void cardImagesDeleteSuccess(Void aVoid) {
        deleteCardListener.onCardDeleted(card.getCardId());
    }

    private void cardDeleteSuccess(Void aVoid) {
        deleteCardIDFromItsBook(card);
    }

    private void cardIdDeleteSuccess(Void aVoid) {
        deleteCardImagesFromFirebaseStorage(card);
    }

    public void setDeleteCardListener(DeleteCardListener deleteCardListener) {
        this.deleteCardListener = deleteCardListener;
    }
}
