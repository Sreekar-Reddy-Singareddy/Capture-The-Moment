package singareddy.productionapps.capturethemoment.card.edit;

import android.arch.lifecycle.ViewModel;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import singareddy.productionapps.capturethemoment.DataRepository;
import singareddy.productionapps.capturethemoment.models.Card;
import singareddy.productionapps.capturethemoment.utils.AppUtilities;

public class UpdateCardViewModel extends ViewModel implements UpdateCardListener{
    private static String TAG = "UpdateCardViewModel";

    private final DataRepository dataRepo;
    private UpdateCardListener updateCardListener;

    public UpdateCardViewModel (DataRepository repository) {
        dataRepo = repository;
    }

    public void saveTheChangesOfCard(Card cardToEdit, List<Uri> activePhotoUris, List<Uri> removedPhotoUris) {
        List<String> activePhotoPaths = generatePathsForNewActiveImages(cardToEdit.getCardId(), activePhotoUris);
        List<String> removedPhotoPaths = generatePathsForRemovedImages(cardToEdit.getCardId(), removedPhotoUris);
        cardToEdit.setImagePaths(activePhotoPaths);
        dataRepo.setUpdateCardListener(this);
        dataRepo.saveTheChangesOfCard(cardToEdit, activePhotoUris, removedPhotoPaths);
    }

    private List<String> generatePathsForRemovedImages(String cardId, List<Uri> removedPhotoUris) {
        List<String> paths = new ArrayList<>();
        for (Uri u :removedPhotoUris) {
            List<String> pathComponents = u.getPathSegments();
            if (u.getPathSegments().contains(AppUtilities.User.CURRENT_USER_ID)) {
                // UID exists in the path. So this is old image
                String removeImagePath = AppUtilities.User.CURRENT_USER_ID+"/"+cardId+"/"+pathComponents.get(pathComponents.size()-1);
                paths.add(removeImagePath);
            }
        }
        return paths;
    }

    private List<String> generatePathsForNewActiveImages(String cardId, List<Uri> activePhotoUris) {
        List<String> paths = new ArrayList<>();
        for (Uri u: activePhotoUris) {
            Log.i(TAG, "URI: "+u.getPath());
            List<String> pathComponents = u.getPathSegments();
            if (pathComponents.contains(AppUtilities.User.CURRENT_USER_ID)) {
                // UID exists in the path. So this is old image
                String oldPath = AppUtilities.User.CURRENT_USER_ID+"/"+cardId+"/"+pathComponents.get(pathComponents.size()-1);
                paths.add(oldPath);
            }
            else {
                String newPath = AppUtilities.User.CURRENT_USER_ID+"/"+cardId+"/"+getRandomImagePath();
                paths.add(newPath);
            }
        }
        return paths;
    }

    private String getRandomImagePath() {
        Random randomGenerator = new Random();
        Integer randomInt = randomGenerator.nextInt(1000000000);
        return "image_"+randomInt.toString()+".jpg";
    }

    @Override
    public void onCardUpdated() {
        updateCardListener.onCardUpdated();
    }

    public void setUpdateCardListener(UpdateCardListener updateCardListener) {
        this.updateCardListener = updateCardListener;
    }
}
