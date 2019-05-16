package singareddy.productionapps.capturethemoment.card.edit;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;
import java.util.function.Predicate;

import singareddy.productionapps.capturethemoment.models.Card;
import singareddy.productionapps.capturethemoment.user.auth.DataSyncListener;
import singareddy.productionapps.capturethemoment.utils.AppUtilities;

public class UpdateCardService {

    private static final String TAG = "UpdateCardService";
    private FirebaseDatabase firebaseDB;
    private FirebaseStorage firebaseStorage;
    private DataSyncListener dataSyncListener;
    private UpdateCardListener updateCardListener;

    public UpdateCardService() {
        firebaseDB = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
    }

    public void saveTheChangesOfCard(Card cardToEdit, List<Uri> activePhotoUris, List<String> removedPhotoPaths) {
        // Upload the card
        saveTheCard(cardToEdit, activePhotoUris, removedPhotoPaths);
    }

    private void deleteTheOldImages(Card card, List<String> removedPhotoUris) {
        for (String path: removedPhotoUris) {
            StorageReference cardNode = firebaseStorage.getReference().child(path);
            cardNode.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i(TAG, "onSuccess: Image Deleted");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG, "onFailure: Image Not Deleted. "+e.getLocalizedMessage());
                        }
                    });
        }
    }

    private void uploadTheNewImages(Card card, List<Uri> activePhotoUris) {
        for (int index=0; index<activePhotoUris.size(); index++) {
            Uri u = activePhotoUris.get(index);
            Log.i(TAG, "uploadTheNewImages: PATH: "+card.getImagePaths().get(index));
            Log.i(TAG, "uploadTheNewImages: URI : "+u);
            if (u.getPathSegments().contains(AppUtilities.User.CURRENT_USER_ID)) continue;
            StorageReference cardNode = firebaseStorage.getReference().child(card.getImagePaths().get(index));
            Log.i(TAG, "uploadTheNewImages: Firebase Path: "+cardNode.getPath());
            cardNode.putFile(u)
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i(TAG, "onSuccess: Image Updated.");
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i(TAG, "onFailure: Image Not Updated.");
                }
            });
        }
    }

    private void saveTheCard(Card cardToEdit, List<Uri> activePhotoUris, List<String> removedPhotoPaths) {
        DatabaseReference cardNode = firebaseDB.getReference().child(AppUtilities.Firebase.ALL_CARDS_NODE).child(cardToEdit.getCardId());
        cardNode.setValue(cardToEdit)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "onSuccess: Card Updated");
                        uploadTheNewImages(cardToEdit, activePhotoUris);
                        deleteTheOldImages(cardToEdit, removedPhotoPaths);
                        dataSyncListener.onCardDownloadedFromFirebase(cardToEdit, activePhotoUris);
                        dataSyncListener.hasToCleanUpUnwantedCardData(cardToEdit, removedPhotoPaths);
                        updateCardListener.onCardUpdated();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "onFailure: Card not updated");
                    }
                });
    }

    public void setDataSyncListener(DataSyncListener dataSyncListener) {
        this.dataSyncListener = dataSyncListener;
    }

    public void setUpdateCardListener(UpdateCardListener updateCardListener) {
        this.updateCardListener = updateCardListener;
    }
}
