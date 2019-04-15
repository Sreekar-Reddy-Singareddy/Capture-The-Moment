package singareddy.productionapps.capturethemoment.book;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import singareddy.productionapps.capturethemoment.models.Book;

@Dao
public interface BookDao {
    @Insert
    public void insert (Book book);

    @Update
    public void update (Book book);

    @Query("SELECT * FROM Book WHERE bookId = :id")
    public Book getBookWithId (String id);

    @Query("SELECT * FROM Book")
    public List<Book> getAllBooks ();

}
