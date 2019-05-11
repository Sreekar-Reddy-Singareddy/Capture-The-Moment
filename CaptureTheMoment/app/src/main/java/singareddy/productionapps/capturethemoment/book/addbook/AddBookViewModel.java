package singareddy.productionapps.capturethemoment.book.addbook;

import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import singareddy.productionapps.capturethemoment.utils.AppUtilities;
import singareddy.productionapps.capturethemoment.DataRepository;
import singareddy.productionapps.capturethemoment.models.SecondaryOwner;

import static singareddy.productionapps.capturethemoment.utils.AppUtilities.Book.BOOK_NAME_EMPTY;
import static singareddy.productionapps.capturethemoment.utils.AppUtilities.Book.BOOK_NAME_INVALID;
import static singareddy.productionapps.capturethemoment.utils.AppUtilities.Book.BOOK_NAME_VALID;
import static singareddy.productionapps.capturethemoment.utils.AppUtilities.User.*;

public class AddBookViewModel extends ViewModel implements AddBookListener {
    private static String TAG = "AddBookViewModel";

    private DataRepository mRepository;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mCurrentUser;
    private AddBookListener mAddBookListener;

    public AddBookViewModel (DataRepository repository) {
        mRepository = repository;
    }

    /**
     * This method takes book details from the UI and
     * validates them with the existing data in the Room DB for duplicate book.
     * Also validates all the secondary owner details with the Firebase data.
     * @param bookName: Name of the book given in the UI
     * @param secOwners: List of secondary owner details put in a java object.
     */
    public void createThisBook (String bookName, List<SecondaryOwner> secOwners) {
        // Here I will not do any data related validations.
        // I will redirect that to a special web service.
        // Only simple logical validation will be done here
        String bookValidationCode = isBookNameValid(bookName);
        if (!bookValidationCode.equals(BOOK_NAME_VALID)) {
            mAddBookListener.onBookNameInvalid(bookValidationCode);
            return;
        }

        // Remove self-given usernames
        String returnValue = removeSelfUsername(secOwners);
        if (returnValue != null) {
            mAddBookListener.onSelfUsernameGiven(returnValue);
            return;
        }

        // Remove duplicate usernames
        if (removeDuplicateUsernames(secOwners)) {
            mAddBookListener.onDuplicatesExist();
            return;
        }

        // Redirect the other validations to the data repo
        mRepository.setAddBookListener(this);
        mRepository.createThisBook(bookName, secOwners);

    }

    /**
     * Remove any duplicate usernames.
     * @param secOwners
     */
    private boolean removeDuplicateUsernames(List<SecondaryOwner> secOwners) {
        boolean duplicatesExist = false;
        Set<SecondaryOwner> distinctOwners = new HashSet<>(secOwners);
        for (SecondaryOwner owner : distinctOwners) {
            int freq = Collections.frequency(secOwners, owner);
            if (freq > 1) {
                duplicatesExist = true;
                Log.i(TAG, "removeDuplicateUsernames: FREQUENCY: "+freq);
                // Update objects in the main list
                secOwners.forEach((o) -> {
                    if (o.equals(owner)) o.setValidated(2);
                });
            }
        }
        return duplicatesExist;
    }

    /**
     * Check if the user has given their own name in usernames.
     * @param secOwners
     */
    private String removeSelfUsername(List<SecondaryOwner> secOwners) {
        String ownerName = null;
        if (AppUtilities.User.LOGIN_PROVIDER.equals(AppUtilities.Firebase.EMAIL_PROVIDER)) {
            ownerName = CURRENT_USER_EMAIL;
        }
        else if (AppUtilities.User.LOGIN_PROVIDER.equals(AppUtilities.Firebase.PHONE_PROVIDER)) {
            ownerName = CURRENT_USER_MOBILE;
        }
        Log.i(TAG, "removeSelfUsername: SELF_SEC_OWNER: "+secOwners.contains(ownerName));
        SecondaryOwner owner = new SecondaryOwner();
        owner.setUsername(ownerName);
        if (secOwners.contains(owner)) return ownerName;
        else return null;
    }

    /** STATUS - WORKING
     * This method only validates if the book name is valid.
     * It does NOT validate for duplicate book names. That is
     * the job of some other method
     * @return
     */
    private String isBookNameValid (String bookName) {
        bookName = bookName.trim();
        if (bookName == null || bookName.equals("")) {
            return BOOK_NAME_EMPTY;
        }
        else if (!bookName.matches("[a-zA-Z0-9_ ]+")) {
            return BOOK_NAME_INVALID;
        }
        else {
            return BOOK_NAME_VALID;
        }
    }

    public void setBookListener(AddBookListener addBookListener) {
        this.mAddBookListener = addBookListener;
    }

    @Override
    public void onBookNameInvalid(String code) {
        mAddBookListener.onBookNameInvalid(code);
    }

    @Override
    public void onAllSecOwnersValidated() {
        Log.i(TAG, "onAllSecOwnersValidated: *");
        mAddBookListener.onAllSecOwnersValidated();
    }

    @Override
    public void onThisSecOwnerValidated() {
        Log.i(TAG, "onThisSecOwnerValidated: *");
        mAddBookListener.onThisSecOwnerValidated();
    }

    @Override
    public void onNewBookCreated() {
        Log.i(TAG, "onNewBookCreated: *");
        mAddBookListener.onNewBookCreated();
    }
}
