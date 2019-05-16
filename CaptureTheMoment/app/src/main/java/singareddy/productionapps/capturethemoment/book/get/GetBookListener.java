package singareddy.productionapps.capturethemoment.book.get;

import singareddy.productionapps.capturethemoment.models.Book;

public interface GetBookListener {
    default public void onBookDownloaded(Book downloadedBook) {}
    default public void onBookRemoved(String removedBookId){}
}
