package singareddy.productionapps.capturethemoment.book;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import singareddy.productionapps.capturethemoment.DataRepository;
import singareddy.productionapps.capturethemoment.models.Book;
import singareddy.productionapps.capturethemoment.models.ShareInfo;

import static singareddy.productionapps.capturethemoment.AppUtilities.Firebase.*;
import static singareddy.productionapps.capturethemoment.AppUtilities.User.*;

public class BookDataWebService {
    private static String TAG = "BookDataWebService";

    private FirebaseDatabase mFirebaseDB;
    private DataRepository mDataRepo;
    private Gson mGson = new Gson();
    private ObjectMapper mMapper = new ObjectMapper();

    public BookDataWebService (Context context) {
        mFirebaseDB = FirebaseDatabase.getInstance();
        mDataRepo = DataRepository.getInstance(context);
    }

    /**
     * This is the starting point of the data loading.
     * From here, other internal methods are called for
     * loading different types of book data.
     * There are two types of books - Owned & Shared.
     */
    public void loadCurrentUserBookData () {
        loadAllOwnedBooks();
        loadAllSharedBooks();
    }

    /**
     * This method loads ONLY the owned books of
     * the current user. The references to these books are
     * available in "ownedBooks" key of the current user.
     */
    private void loadAllOwnedBooks() {
        DatabaseReference currentUserNode = mFirebaseDB.getReference()
                .child(ALL_USERS_NODE)
                .child(CURRENT_USER.getUid())
                .child("profile")
                .child("ownedBooks");
        Log.i(TAG, "loadAllOwnedBooks: Data Ref: "+currentUserNode.getPath().toString());

        // Create a one time value listener for this reference
        ValueEventListener ownedBooksValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i(TAG, "onDataChange: Owned Books Data Snapshot: "+dataSnapshot.getValue());
                // The data is an array of strings
                Object data = dataSnapshot.getValue();
                loadDetailsOfOwnedBook(data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i(TAG, "onCancelled: Owned Books Error: "+ databaseError.getMessage());
            }
        };

        // Add the above listener to this data reference
        currentUserNode.addListenerForSingleValueEvent(ownedBooksValueListener);
    }

    private void loadDetailsOfOwnedBook (final Object data) {
        List<String> ownedBooks = null;
        if (data != null) {
            // There is some data
            ownedBooks = (ArrayList<String>) data;
        }

        // Check if array is null and proceed
        if (ownedBooks == null || ownedBooks.size() == 0) {
            return;
        }

        // Proceed and load the books one by one
        fetchBooksWithIds(ownedBooks);
    }

    /**
     * This method loads ONLY the shared books of
     * the current user. The references to these books are
     * available in "sharedBooks" key of the current user.
     */
    private void loadAllSharedBooks() {
        DatabaseReference sharedBooksNode = mFirebaseDB.getReference()
                .child(ALL_USERS_NODE)
                .child(CURRENT_USER.getUid())
                .child("sharedBooks");
        Log.i(TAG, "loadAllSharedBooks: Data Ref: "+sharedBooksNode.getPath().toString());

        // Create a one time value listener for this reference
        ValueEventListener sharedBooksValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i(TAG, "onDataChange: Shared books data snapshot: \n"+dataSnapshot.getValue());
                Object data = dataSnapshot.getValue();
                loadDetailsOfSharedBook(data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i(TAG, "onCancelled: Shared books error: \n"+databaseError.getMessage());
            }
        };

        // Add the above listener to reference
        sharedBooksNode.addListenerForSingleValueEvent(sharedBooksValueListener);

    }

    private void loadDetailsOfSharedBook(Object data) {
        List<Map> jsonStrings = null;
        List<ShareInfo> sharedBooks = new ArrayList<>();
        List<String> sharedBookIds = new ArrayList<>();
        if (data != null) {
            // There is some data.
            // The data is in Map form.
            // Convert it into an array of ShareInfo objects.
            jsonStrings = (ArrayList<Map>) data;
            for (Map map: jsonStrings) {
                ShareInfo shareInfo = mMapper.convertValue(map, ShareInfo.class);
                sharedBooks.add(shareInfo);
                sharedBookIds.add(shareInfo.getBookId());
            }
        }

        // Check if array is null and proceed
        if (jsonStrings == null || jsonStrings.size() == 0) {
            return;
        }

        // Proceed and load the books
        fetchBooksWithIds(sharedBookIds);
    }

    /**
     * This is a final method that loads all the books
     * from the firebase using the book id.
     * Both owned and shared books are fetched in this method.
     * @param bookIds
     */
    private void fetchBooksWithIds(List<String> bookIds) {
        for (final String bookId : bookIds) {
            DatabaseReference bookReference = mFirebaseDB.getReference()
                    .child(ALL_BOOKS_NODE)
                    .child(bookId);

            bookReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.i(TAG, "onDataChange: Book data fetched: "+dataSnapshot.getValue());
                    Book book = dataSnapshot.getValue(Book.class);
                    book.setBookId(bookId);
                    Log.i(TAG, "onDataChange: Fetched Book: "+book.toString());
                    // Insert this book into Room DB
                    mDataRepo.insertBookInRoom(book);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.i(TAG, "onCancelled: Book data fetch failed: "+databaseError.getMessage());
                }
            });
        }
    }
}
