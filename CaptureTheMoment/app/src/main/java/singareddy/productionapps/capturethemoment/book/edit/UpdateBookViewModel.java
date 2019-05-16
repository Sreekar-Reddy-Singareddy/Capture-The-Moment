package singareddy.productionapps.capturethemoment.book.edit;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import singareddy.productionapps.capturethemoment.models.ShareInfo;
import singareddy.productionapps.capturethemoment.utils.AppUtilities;
import singareddy.productionapps.capturethemoment.DataRepository;
import singareddy.productionapps.capturethemoment.models.Book;
import singareddy.productionapps.capturethemoment.models.SecondaryOwner;
import static singareddy.productionapps.capturethemoment.utils.AppUtilities.Book.*;
import static singareddy.productionapps.capturethemoment.utils.AppUtilities.User.CURRENT_USER_EMAIL;
import static singareddy.productionapps.capturethemoment.utils.AppUtilities.User.CURRENT_USER_MOBILE;

public class UpdateBookViewModel extends ViewModel implements UpdateBookListener {
    private static String TAG = "UpdateBookViewModel";

    private DataRepository mRepository;
    private UpdateBookListener updateBookListener;

    public UpdateBookViewModel(DataRepository repository) {
        mRepository = repository;
    }

    /**
     * In order to update a book, I first need to get all the details
     * of the book and display them in UI.
     * 1. Get the selected book id from calling intent.
     * 2. Use this bookId to get name and secOnwners of the book.
     * 3. Store the old name and owners somewhere.
     * 4. Get the new name and owners from UI.
     * 5. Validate for same name, duplicate name, and owners.
     * 6. If everything is perfect, update only the name and owners of the book.
     */
    public Book getBookDetailsFor (String bookId) {
        if (bookId == null || mRepository == null) return null;
        // Get the book from repository
        Book book = mRepository.getBookDetailsFor(bookId);
        return book;
    }

    public LiveData<List<ShareInfo>> getSecondaryOwners(String bookId) {
        return mRepository.getSecondaryOwners(bookId);
    }

    public List<SecondaryOwner> getUsernamesForUids(Context context, List<ShareInfo> shareInfos) {
        SharedPreferences uidsCache = context.getSharedPreferences(AppUtilities.FileNames.UIDS_CACHE, Context.MODE_PRIVATE);
        List<SecondaryOwner> secondaryOwners = new ArrayList<>();
        for (ShareInfo info: shareInfos) {
            SecondaryOwner owner = new SecondaryOwner();
            owner.setValidated(1);
            owner.setCanEdit(info.getCanEdit());
            owner.setUsername(uidsCache.getString(info.getUid(), "Who??"));
            secondaryOwners.add(owner);
        }
        return secondaryOwners;
    }

    public void setUpdateBookListener(UpdateBookListener updateBookListener) {
        this.updateBookListener = updateBookListener;
    }

    public void updateBook(String bookId, String newName, String oldName, List<SecondaryOwner> activeOwners, List<SecondaryOwner> removedOwners) {
        Boolean sameBookName = oldName.toLowerCase().trim().equals(newName.toLowerCase().trim());
        String code = isBookNameValid(newName, oldName);
        if (!code.equals(BOOK_NAME_VALID)) {
            updateBookListener.onBookNameInvalid(code);
            return;
        }

        if (removeDuplicateUsernames(activeOwners)) {
            updateBookListener.onDuplicatesExist();
            return;
        }

        String selfName = removeSelfUsername(activeOwners);
        if(selfName != null) {
            updateBookListener.onSelfUsernameGiven(selfName);
            return;
        }

        // Proceed and validate book name and sec owners
        mRepository.setUpdateBookListener(this);
        mRepository.updateBook(bookId, sameBookName, newName, activeOwners, removedOwners);
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
                for (SecondaryOwner o: secOwners) {
                    if (o.equals(owner)) o.setValidated(2);
                }
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
        Log.i(TAG, "removeSelfUsername: "+AppUtilities.User.LOGIN_PROVIDER);
        if (AppUtilities.User.LOGIN_PROVIDER.equals(AppUtilities.Firebase.EMAIL_PROVIDER)) {
            ownerName = CURRENT_USER_EMAIL;
        }
        else if (AppUtilities.User.LOGIN_PROVIDER.equals(AppUtilities.Firebase.PHONE_PROVIDER)) {
            ownerName = CURRENT_USER_MOBILE;
        }
        Log.i(TAG, "removeSelfUsername: OWNER NAME: "+ownerName);
        Log.i(TAG, "removeSelfUsername: SELF_SEC_OWNER: "+secOwners.contains(ownerName));
        SecondaryOwner owner = new SecondaryOwner();
        owner.setUsername(ownerName);
        if (secOwners.contains(owner)) return ownerName;
        else return null;
    }

    private String isBookNameValid(String newName,  String oldName) {
        if (newName == null || newName.isEmpty()) {
            return BOOK_NAME_EMPTY;
        }
        newName = newName.toLowerCase().trim();
        oldName = oldName.toLowerCase().trim();
        if (!newName.matches("[a-z0-9_ ]+")) {
            return BOOK_NAME_INVALID;
        }
        return BOOK_NAME_VALID;
    }
    // Listener methods

    @Override
    public void onBookNameInvalid(String code) {
        Log.i(TAG, "onBookNameInvalid: *");
        updateBookListener.onBookNameInvalid(code);
    }

    @Override
    public void onAllSecOwnersValidated() {
        Log.i(TAG, "onAllSecOwnersValidated: *");
        updateBookListener.onAllSecOwnersValidated();
    }

    @Override
    public void onThisSecOwnerValidated() {
        Log.i(TAG, "onThisSecOwnerValidated: *");
        updateBookListener.onThisSecOwnerValidated();
    }

    @Override
    public void onBookUpdated() {
        updateBookListener.onBookUpdated();
    }
}
