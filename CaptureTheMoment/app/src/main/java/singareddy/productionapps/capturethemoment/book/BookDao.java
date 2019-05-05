package singareddy.productionapps.capturethemoment.book;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import java.util.List;

import singareddy.productionapps.capturethemoment.models.Book;

@Dao
public interface BookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long insert (Book book);

    @Update
    public void update (Book book);

    @Query("SELECT * FROM Book WHERE bookId = :id")
    public Book getBookWithId (String id);

    @Query("SELECT * FROM Book WHERE owner = :uid")
    public List<Book> getAllOwnedBooks (String uid);

    @Query("SELECT * FROM Book ORDER BY lastUpdatedDate DESC")
    public LiveData<List<Book>> getAllBooks ();

    @Query("SELECT COUNT(bookId) FROM Book WHERE owner <> :uid")
    public int getNumberOfSharedBooks(String uid);

    @Query("SELECT COUNT(bookId) FROM Book WHERE owner = :uid")
    public int getNumberOfOwnedBooks(String uid);

    @Query("DELETE FROM Book")
    public int deleteAllData();

}
