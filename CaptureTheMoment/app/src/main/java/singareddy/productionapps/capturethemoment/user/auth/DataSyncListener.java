package singareddy.productionapps.capturethemoment.user.auth;

import android.net.Uri;

import java.util.List;

import singareddy.productionapps.capturethemoment.models.Book;
import singareddy.productionapps.capturethemoment.models.Card;
import singareddy.productionapps.capturethemoment.models.User;

public interface DataSyncListener {
    default public void onUserProfileDownloaded(User currentUserProfile){}
    default public void onBookDownloadedFromFirebase(Book downloaedBook, Boolean sharedBookAccess){}
    default public void onProfilePictureDownloaded(){}

    default public void onCardDownloadedFromFirebase(Card card, List<Uri> imageUris){}
}
