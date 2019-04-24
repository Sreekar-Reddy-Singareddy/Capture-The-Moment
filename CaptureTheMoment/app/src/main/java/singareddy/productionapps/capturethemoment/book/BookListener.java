package singareddy.productionapps.capturethemoment.book;

import java.util.Map;

import singareddy.productionapps.capturethemoment.models.Book;

public interface BookListener {
    default public void onSelfUsernameGiven(String returnValue) {}
    default public void onBookNameInvalid (String code){}
    default public void onAllSecOwnersValidated (){}
    default public void onThisSecOwnerValidated (){}
    default public void onDuplicatesExist(){}
    default public void hasToSaveBookInCache(Book book) {}
    default public void hasToSaveUidInCache(Map<String, String> userMap) {}
}
