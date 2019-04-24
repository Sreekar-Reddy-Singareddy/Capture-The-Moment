package singareddy.productionapps.capturethemoment.book.addbook;

import java.util.HashMap;
import java.util.Map;

import singareddy.productionapps.capturethemoment.models.Book;

public interface AddBookListener {
    public void onBookNameInvalid (String code);
    public void onAllSecOwnersValidated ();
    public void onThisSecOwnerValidated ();
    default public void onNewBookCreated () {}
    default public void onSelfUsernameGiven(String returnValue) {}
    default public void onDuplicatesExist(){}
    default public void hasToSaveBookInCache(Book book) {}
    default public void hasToSaveUidInCache(Map<String, String> userMap) {}
}
