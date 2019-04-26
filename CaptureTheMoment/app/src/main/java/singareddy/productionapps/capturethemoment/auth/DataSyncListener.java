package singareddy.productionapps.capturethemoment.auth;

import singareddy.productionapps.capturethemoment.models.Book;
import singareddy.productionapps.capturethemoment.models.User;

public interface DataSyncListener {
    default public void onUserProfileDownloaded(User currentUserProfile){}
    default public void onBookDownloadedFromFirebase(Book downloaedBook, Boolean sharedBookAccess){}
}
