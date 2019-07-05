package singareddy.productionapps.capturethemoment.card.get;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import singareddy.productionapps.capturethemoment.DataRepository;
import singareddy.productionapps.capturethemoment.DataSyncListener;
import singareddy.productionapps.capturethemoment.models.Card;
import singareddy.productionapps.capturethemoment.utils.AppUtilities;

public class GetCardsViewModel extends ViewModel implements SmallCardDownloadListener, DataSyncListener {
    private static String TAG = "GetCardsViewModel";

    private DataRepository dataRepo;
    private SmallCardDownloadListener smallCardDownloadListener;
    private DataSyncListener dataSyncListener;

    public GetCardsViewModel(DataRepository repository) {
        dataRepo = repository;
    }

    public LiveData<List<Card>> getAllCardsFor(String bookId) {
        dataRepo.setSmallCardDownloadListener(this);
        return dataRepo.getAllCardsFor(bookId);
    }

    public String getOneImagePathForCard(String cardId) {
        return dataRepo.getOneImagePathForCard(cardId);
    }

    public LiveData<Card> getCardWithId(String cardId) {
        return dataRepo.getCardWithId(cardId);
    }

    public List<String> getImagePathsForCardWithId(String cardId) {
        return dataRepo.getImagePathsForCardWithId(cardId);
    }

    public List<Uri> getUrisForPaths(Context context, List<String> bigCardImagePaths) {
        List<Uri> imageUris = new ArrayList<>();
        File rootDir = context.getFilesDir();
        for (String imagePath : bigCardImagePaths) {
            File imageFile = new File(rootDir, imagePath);
            Uri imageUri = Uri.fromFile(imageFile);
            imageUris.add(imageUri);
        }
        return imageUris;
    }

    public boolean canEditOrRemoveCard(String bookId) {
        return dataRepo.canEditOrRemoveCard(bookId);
    }

    public Boolean getCurrentUserEditAccessForThisBook(String bookId) {
        return dataRepo.getCurrentUserEditAccessForThisBook(bookId, AppUtilities.User.CURRENT_USER_ID);
    }

    public void reloadCardsOfBook(String bookId) {
        dataRepo.setDataSyncListener(this);
        dataRepo.reloadCardsOfBook(bookId);
    }

    public void setSmallCardDownloadListener(SmallCardDownloadListener smallCardDownloadListener) {
        this.smallCardDownloadListener = smallCardDownloadListener;
    }

    public void setDataSyncListener(DataSyncListener dataSyncListener) {
        this.dataSyncListener = dataSyncListener;
    }

    @Override
    public void onSmallCardDownloaded() {
        smallCardDownloadListener.onSmallCardDownloaded();
    }

    @Override
    public void shouldStopUILoader() {
        Log.i(TAG, "shouldStopUILoader: *");
        dataSyncListener.shouldStopUILoader();
    }
}
