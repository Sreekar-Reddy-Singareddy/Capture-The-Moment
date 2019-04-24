package singareddy.productionapps.capturethemoment.book.details;

import java.util.List;

import singareddy.productionapps.capturethemoment.models.SecondaryOwner;

public interface UpdateBookListener {
    default public void onExistingUsernamesRecieved(List<SecondaryOwner> secOwners){}
}
