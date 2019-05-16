package singareddy.productionapps.capturethemoment.book.add;

import singareddy.productionapps.capturethemoment.book.BookListener;

public interface AddBookListener extends BookListener {
    default public void onNewBookCreated () {}
}
