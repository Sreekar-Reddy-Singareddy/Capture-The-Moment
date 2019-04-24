package singareddy.productionapps.capturethemoment.book.addbook;

import java.util.HashMap;
import java.util.Map;

import singareddy.productionapps.capturethemoment.book.BookListener;
import singareddy.productionapps.capturethemoment.models.Book;

public interface AddBookListener extends BookListener {
    default public void onNewBookCreated () {}
}
