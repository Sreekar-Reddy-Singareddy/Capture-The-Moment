package singareddy.productionapps.capturethemoment.book.details;

import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

import singareddy.productionapps.capturethemoment.book.addbook.AddBookListener;
import singareddy.productionapps.capturethemoment.book.addbook.AddBookService;
import singareddy.productionapps.capturethemoment.models.SecondaryOwner;

public class UpdateBookService extends AddBookService {
    private static String TAG = "AddBookService";

    private FirebaseDatabase mfirebaseDB;
    private HashMap<String, Boolean> validSecOwnersMap;
    private Integer mOwnersValidated;
    private UpdateBookListener updateBookListener;
    private Boolean mIsBookNameValid = false;
    private Boolean mAreSecOwnersValid = false;
    private String mNewBookName;
    private List<SecondaryOwner> mSecOwnersList;

    public UpdateBookService () {
        mfirebaseDB = FirebaseDatabase.getInstance();
    }

    public void setUpdateBookListener(UpdateBookListener updateBookListener) {
        this.updateBookListener = updateBookListener;
    }
}
