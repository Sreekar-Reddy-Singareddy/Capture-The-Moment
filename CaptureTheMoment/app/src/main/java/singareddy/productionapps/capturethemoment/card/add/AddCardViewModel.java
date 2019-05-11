package singareddy.productionapps.capturethemoment.card.add;

import android.arch.lifecycle.ViewModel;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import singareddy.productionapps.capturethemoment.DataRepository;
import singareddy.productionapps.capturethemoment.utils.AppUtilities;
import singareddy.productionapps.capturethemoment.models.Card;

public class AddCardViewModel extends ViewModel implements AddCardListener{
    private static String TAG = "AddCardViewModel";

    private DataRepository dataRepo;
    private AddCardListener addCardListener;

    public AddCardViewModel (DataRepository repository) {
        dataRepo = repository;
    }

    public void createNewCard(Card newCard, List<Uri> imageUris) {
        if (newCard.getDescription() == null || newCard.getDescription().isEmpty()) {
            // TODO: Tell the listener
            return;
        }

        // Create random integers and hence paths for each image
        // and set that list of paths to card instance.
        List<String> imagePaths = generateImagePathsFor(newCard, imageUris);
        newCard.setImagePaths(imagePaths);
        dataRepo.setAddCardListener(this);
        dataRepo.createNewCard(newCard, imageUris);

    }

    private List<String> generateImagePathsFor(Card newCard, List<Uri> imageUris) {
        List<String> imagePaths = new ArrayList<>();
        // Set a base path
        String generatedCardId = FirebaseDatabase.getInstance().getReference()
                .child("cards").push().getKey();
        newCard.setCardId(generatedCardId);
        String basePath = AppUtilities.User.CURRENT_USER_ID+"/"+generatedCardId;
        Log.i(TAG, "generateImagePathsFor: BASE PATH: "+basePath);
        // Generate a random number
        Random randomGenerator = new Random();
        imageUris.forEach(uri -> {
            Integer randomInt = randomGenerator.nextInt(1000000000);
            String fullImagePath = basePath + "/image_" + randomInt.toString() + ".jpg";
            // Once the path is generated, add it to the list
            imagePaths.add(fullImagePath);
        } );
        return imagePaths;
    }

    public void setAddCardListener(AddCardListener addCardListener) {
        this.addCardListener = addCardListener;
    }

    @Override
    public void onCardCreated() {
        addCardListener.onCardCreated();
    }
}
