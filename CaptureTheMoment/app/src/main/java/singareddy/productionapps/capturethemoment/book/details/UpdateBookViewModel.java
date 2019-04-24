package singareddy.productionapps.capturethemoment.book.details;

import android.arch.lifecycle.ViewModel;

import java.util.List;

import singareddy.productionapps.capturethemoment.DataRepository;
import singareddy.productionapps.capturethemoment.models.Book;
import singareddy.productionapps.capturethemoment.models.SecondaryOwner;

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
        // Using this book, get their usernames
        List<SecondaryOwner> secondaryOwners = mRepository.getUsernamesFor(bookId); // This runs in background thread
        updateBookListener.onExistingUsernamesRecieved(secondaryOwners);
        return book;
    }

    public void setUpdateBookListener(UpdateBookListener updateBookListener) {
        this.updateBookListener = updateBookListener;
    }

    public void updateBook (String newName, String oldName, List<SecondaryOwner> secOwners) {
        if (newName.toLowerCase().trim().equals(oldName.toLowerCase().trim())) {
            return;
        }
        // Proceed and validate book name and sec owners

    }

    private void isBookNameValid(String newName,  String oldName) {
        
    }


}
