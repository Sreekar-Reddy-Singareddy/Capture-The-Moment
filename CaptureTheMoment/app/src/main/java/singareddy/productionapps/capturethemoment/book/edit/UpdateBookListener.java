package singareddy.productionapps.capturethemoment.book.edit;

import java.util.List;

import singareddy.productionapps.capturethemoment.book.BookListener;
import singareddy.productionapps.capturethemoment.models.SecondaryOwner;

public interface UpdateBookListener extends BookListener {
    default public void onBookUpdated(){}
}
