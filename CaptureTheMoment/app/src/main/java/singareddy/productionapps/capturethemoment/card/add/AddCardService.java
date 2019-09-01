package singareddy.productionapps.capturethemoment.card.add;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import singareddy.productionapps.capturethemoment.card.edit.UpdateCardListener;
import singareddy.productionapps.capturethemoment.card.edit.UpdateCardService;
import singareddy.productionapps.capturethemoment.utils.AppUtilities;
import singareddy.productionapps.capturethemoment.models.Card;
import singareddy.productionapps.capturethemoment.DataSyncListener;

public class AddCardService {
    private static String TAG = "AddCardService";

    private FirebaseStorage storage;
    private FirebaseDatabase database;
    private AddCardListener addCardListener;
    private DataSyncListener dataSyncListener;

    public AddCardService() {
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    public void createNewCard(Card newCard, List<Uri> imageUris) {
        // Upload the images first
        uploadImages(newCard.getImagePaths(), imageUris);
        // Save the card instance in database
        saveCardInFirebaseDB(newCard,imageUris);
    }

    private void saveCardInFirebaseDB(Card newCard, List<Uri> imageUris) {
        DatabaseReference newCardRef = database.getReference()
                .child(AppUtilities.Firebase.ALL_CARDS_NODE)
                .child(newCard.getCardId());
        newCardRef.setValue(newCard)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        addNewCardIdInBook(newCard, imageUris);
                    }
                });
    }

    private void addNewCardIdInBook(Card newCard, List<Uri> imageUris) {
        DatabaseReference bookRef = database.getReference()
                .child(AppUtilities.Firebase.ALL_BOOKS_NODE)
                .child(newCard.getBookId())
                .child(AppUtilities.Firebase.KEY_BOOK_CARDS);
        bookRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> existingCards = (ArrayList<String>) dataSnapshot.getValue();
                if (existingCards == null) existingCards = new ArrayList<>();
                existingCards.add(newCard.getCardId());
                bookRef.setValue(existingCards)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                UpdateCardService updateCardService = new UpdateCardService();
                                updateCardService.updateBooksModifiedTimeInFirebase(newCard.getBookId());
                                addCardListener.onCardCreated(newCard.getBookId());
                                // Add this book in the local storage
                                dataSyncListener.onCardDownloadedFromFirebase(newCard, imageUris);
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void uploadImages(List<String> imagePaths, List<Uri> imageUris) {
        // PATHS and URIs have 1-1 correspondence
        for (int position=0; position<imagePaths.size(); position++) {
            int finalPosition = position;
            storage.getReference().child(imagePaths.get(position))
                    .putFile(imageUris.get(position))
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            // TODO: ?
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // TODO: ?
                        }
                    });
        }
    }

    public void setAddCardListener(AddCardListener addCardListener) {
        this.addCardListener = addCardListener;
    }

    public void setDataSyncListener(DataSyncListener dataSyncListener) {
        this.dataSyncListener = dataSyncListener;
    }
}
