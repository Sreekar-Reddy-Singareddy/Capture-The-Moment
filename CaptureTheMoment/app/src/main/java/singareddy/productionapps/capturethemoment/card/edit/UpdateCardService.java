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

import singareddy.productionapps.capturethemoment.models.Card;
import singareddy.productionapps.capturethemoment.DataSyncListener;
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
                            // TODO: ?
                        }
                    });
        }
    }

    private void uploadTheNewImages(Card card, List<Uri> activePhotoUris) {
        for (int index=0; index<activePhotoUris.size(); index++) {
            Uri u = activePhotoUris.get(index);
            if (u.getPathSegments().contains(AppUtilities.User.CURRENT_USER_ID)) continue;
            StorageReference cardNode = firebaseStorage.getReference()
                    .child(card.getImagePaths().get(index));
            cardNode.putFile(u)
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // TODO: ?
                }
            });
        }
    }

    private void saveTheCard(Card cardToEdit, List<Uri> activePhotoUris, List<String> removedPhotoPaths) {
        DatabaseReference cardNode = firebaseDB.getReference()
                .child(AppUtilities.Firebase.ALL_CARDS_NODE)
                .child(cardToEdit.getCardId());
        cardNode.setValue(cardToEdit)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        uploadTheNewImages(cardToEdit, activePhotoUris);
                        deleteTheOldImages(cardToEdit, removedPhotoPaths);
                        dataSyncListener.onCardDownloadedFromFirebase(cardToEdit, activePhotoUris);
                        dataSyncListener.hasToCleanUpUnwantedCardData(cardToEdit, removedPhotoPaths);
                        updateCardListener.onCardUpdated();
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
