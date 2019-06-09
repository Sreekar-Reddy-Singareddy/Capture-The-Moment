package singareddy.productionapps.capturethemoment;

import android.net.Uri;

import java.util.List;

import singareddy.productionapps.capturethemoment.models.Book;
import singareddy.productionapps.capturethemoment.models.Card;
import singareddy.productionapps.capturethemoment.models.User;

public interface DataSyncListener {
    default public void onUserProfileDownloaded(User currentUserProfile){}
    default public void onProfilePictureDownloaded(){}

    default public void onCardDownloadedFromFirebase(Card card, List<Uri> imageUris){}
    default public void hasToCleanUpUnwantedCardData(Card card, List<String> removedImagePaths) {}

    default public void onBookDownloadedFromFirebase(Book downloaedBook, Boolean sharedBookAccess){}
    default public void hasToRemoveSecOwnerFromRoomDB(String bookId, String uid){}

    default public void shouldStopUILoader(){}

}
