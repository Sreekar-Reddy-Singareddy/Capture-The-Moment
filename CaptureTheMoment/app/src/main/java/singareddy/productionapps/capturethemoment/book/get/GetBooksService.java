package singareddy.productionapps.capturethemoment.book.get;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import singareddy.productionapps.capturethemoment.utils.AppUtilities;
import singareddy.productionapps.capturethemoment.models.Book;

import static singareddy.productionapps.capturethemoment.utils.AppUtilities.Firebase.*;
import static singareddy.productionapps.capturethemoment.utils.AppUtilities.Book.*;
import static singareddy.productionapps.capturethemoment.utils.AppUtilities.User.*;

public class GetBooksService {
    private static String TAG = "GetBooksService";

    private FirebaseDatabase mfirebaseDB;

    // Members used in book retrieval
    private GetBookListener mBookGetBookListenerListener;

    public GetBooksService(Context context) {
        mfirebaseDB = FirebaseDatabase.getInstance();
    }

    /**
     * This method retrieved all the book from firebase and
     * displays them in UI
     */
    public void getAllBooksFromFirebase() {
        getOwnedBooks();
        getSharedBooks();
    }

    /**
     * Retrieved ONLY owned books of the current user
     * from Firebase
     */
    private void getOwnedBooks() {
        mfirebaseDB.getReference()
                .child(ALL_USERS_NODE) // users
                .child(CURRENT_USER_ID) // owned UID
                .child(KEY_USER_PROFILE) // profile
                .child(KEY_USER_OWNED_BOOKS) // ownedBooks
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        String newAddedBookId = dataSnapshot.getValue(String.class);
                        downloadBookWithId(newAddedBookId);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        // Nothing here as bookIds wont update
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        String removedBookId = dataSnapshot.getValue(String.class);
                        mBookGetBookListenerListener.onBookRemoved(removedBookId);
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        // Nothing here
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    /**
     * Retrieved ONLY shared books of the current user
     * from Firebase
     */
    private void getSharedBooks() {
        mfirebaseDB.getReference()
                .child(ALL_USERS_NODE) // users
                .child(CURRENT_USER_ID) // owned UID
                .child(KEY_USER_SHARED_BOOKS) // sharedBooks
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        String sharedBookId = dataSnapshot.getKey();
                        downloadBookWithId(sharedBookId);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        // TODO: Shared book changed
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        String removedBookId = dataSnapshot.getKey();
                        mBookGetBookListenerListener.onBookRemoved(removedBookId);
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        // Nothing here
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    /**
     * Fetches a book from firebase given its book id
     * @param bookId
     */
    private void downloadBookWithId(String bookId) {
        // Fetch the book and add it to shared books list
        mfirebaseDB.getReference()
                .child(ALL_BOOKS_NODE) // books
                .child(bookId) // bookId
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            Book fetchedBook = dataSnapshot.getValue(Book.class);
                            mBookGetBookListenerListener.onBookDownloaded(fetchedBook);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    // MARK: Setters and listeners
    public void setGetBookListener(GetBookListener getBookListener) {
        this.mBookGetBookListenerListener = getBookListener;
    }
}
