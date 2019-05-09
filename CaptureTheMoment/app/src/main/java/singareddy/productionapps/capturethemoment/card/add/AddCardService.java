package singareddy.productionapps.capturethemoment.card.add;

import android.net.Uri;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import singareddy.productionapps.capturethemoment.Utils.AppUtilities;
import singareddy.productionapps.capturethemoment.models.Card;
import singareddy.productionapps.capturethemoment.user.auth.DataSyncListener;

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
                        Log.i(TAG, "onSuccess: Card saved in Firebase DB");
                        addNewCardIdInBook(newCard, imageUris);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "onFailure: Card save failed. Error: "+e.getLocalizedMessage());
                    }
                });
    }

    private void addNewCardIdInBook(Card newCard, List<Uri> imageUris) {
        DatabaseReference bookRef = database.getReference()
                .child(AppUtilities.Firebase.ALL_BOOKS_NODE)
                .child(newCard.getBookId())
                .child("cards");
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
                                Log.i(TAG, "onSuccess: Card added in its book");
                                addCardListener.onCardCreated();
                                // Add this book in the local storage
                                dataSyncListener.onCardDownloadedFromFirebase(newCard, imageUris);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i(TAG, "onFailure: Card not added in its book. Error: "+e.getLocalizedMessage());
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
            Log.i(TAG, "uploadImages: URI : "+imageUris.get(position));
            Log.i(TAG, "uploadImages: PATH: "+imagePaths.get(position));
            int finalPosition = position;
            storage.getReference().child(imagePaths.get(position))
                    .putFile(imageUris.get(position))
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.i(TAG, "onProgress: IMAGE "+ finalPosition + " Progress: "+ taskSnapshot.getBytesTransferred());
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.i(TAG, "onSuccess: IMAGE "+ finalPosition + " Uploaded!");
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
