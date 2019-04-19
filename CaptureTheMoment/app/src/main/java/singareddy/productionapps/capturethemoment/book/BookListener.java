package singareddy.productionapps.capturethemoment.book;

import singareddy.productionapps.capturethemoment.models.Book;

public interface BookListener {
    public void onBookNameInvalid (String code);
    public void onAllSecOwnersValidated ();
    public void onThisSecOwnerValidated ();
    default public void onNewBookCreated () {

    }

    interface UpdateBook {
        default public void onBookUpdatedWithNewId(String newBookId) {}
    }

    interface Retrieve {
        default public void onBookDownloaded(Book downloadedBook) {

        }
    }
}
