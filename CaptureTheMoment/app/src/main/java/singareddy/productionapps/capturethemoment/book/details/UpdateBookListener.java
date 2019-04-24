package singareddy.productionapps.capturethemoment.book.details;

import java.util.List;
import java.util.Map;

import singareddy.productionapps.capturethemoment.book.BookListener;
import singareddy.productionapps.capturethemoment.models.SecondaryOwner;

public interface UpdateBookListener extends BookListener {
    default public void onExistingUsernamesRecieved(List<SecondaryOwner> secOwners){}
    default public void onBookUpdated(){}
}
