package singareddy.productionapps.capturethemoment.book.delete;

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

import static singareddy.productionapps.capturethemoment.utils.AppUtilities.Firebase.*;
import static singareddy.productionapps.capturethemoment.utils.AppUtilities.User.*;

public class DeleteBookService {
    private static String TAG = "DeleteBookService";

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private DeleteBookListener deleteBookListener;

    private void deleteFromOwner(String bookId) {
        DatabaseReference ownerNode = database.getReference()
                .child(ALL_USERS_NODE)
                .child(CURRENT_USER_ID)
                .child(KEY_USER_OWNED_BOOKS);
        ownerNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot == null) return;
                List<String> allOwnedBookIds = (ArrayList<String>) dataSnapshot.getValue();
                allOwnedBookIds.remove(bookId);
                ownerNode.setValue(allOwnedBookIds)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // TODO: ?
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteFromSecOwners(String bookId, List<String> secOwnerUids) {
        for (String uid: secOwnerUids) {
            DatabaseReference secOwnerNode = database.getReference()
                    .child(ALL_USERS_NODE)
                    .child(uid)
                    .child(KEY_USER_SHARED_BOOKS)
                    .child(bookId);
            secOwnerNode.removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // TODO: ?
                        }
                    });
        }
    }

    private void deleteCards(String bookId, List<String> allCardIds) {
        for (String cardId: allCardIds) {
            DatabaseReference cardNode = database.getReference()
                    .child(ALL_CARDS_NODE)
                    .child(cardId);
            cardNode.removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // TODO: ?
                        }
                    });
        }
    }

    private void deleteCardImages(String bookId, List<String> allCardIds) {
        for (String cardId: allCardIds) {
            StorageReference cardNode = storage.getReference()
                    .child(CURRENT_USER_ID)
                    .child(cardId);
        }
    }

    public void deleteBookFromServer(String bookId, List<String> secOwnerUids,  List<String> allCardIds) {
        // Delete the book from owners owned books
        deleteFromOwner(bookId);

        // Delete the book from sec owners shared books
        deleteFromSecOwners(bookId, secOwnerUids);

        // Delete all cards from the firebase
        deleteCards(bookId, allCardIds);

        // Delete all images under each card from firebase storage
        deleteCardImages(bookId, allCardIds);
        
        // Delete the book
        database.getReference()
                .child(ALL_BOOKS_NODE)
                .child(bookId)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        deleteBookListener.onBookDeleted();
                    }
                });
    }

    public void setDeleteBookListener(DeleteBookListener deleteBookListener) {
        this.deleteBookListener = deleteBookListener;
    }
}
