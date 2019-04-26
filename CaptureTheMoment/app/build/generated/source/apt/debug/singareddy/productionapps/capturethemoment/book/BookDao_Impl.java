package singareddy.productionapps.capturethemoment.book;

import android.arch.lifecycle.ComputableLiveData;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.db.SupportSQLiteStatement;
import android.arch.persistence.room.EntityDeletionOrUpdateAdapter;
import android.arch.persistence.room.EntityInsertionAdapter;
import android.arch.persistence.room.InvalidationTracker.Observer;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.RoomSQLiteQuery;
import android.arch.persistence.room.SharedSQLiteStatement;
import android.database.Cursor;
import android.support.annotation.NonNull;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import singareddy.productionapps.capturethemoment.models.Book;

@SuppressWarnings("unchecked")
public class BookDao_Impl implements BookDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter __insertionAdapterOfBook;

  private final EntityDeletionOrUpdateAdapter __updateAdapterOfBook;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllData;

  public BookDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfBook = new EntityInsertionAdapter<Book>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `Book`(`bookId`,`name`,`owner`,`createdDate`,`lastUpdatedDate`) VALUES (?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Book value) {
        if (value.getBookId() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getBookId());
        }
        if (value.getName() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getName());
        }
        if (value.getOwner() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getOwner());
        }
        if (value.getCreatedDate() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindLong(4, value.getCreatedDate());
        }
        if (value.getLastUpdatedDate() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindLong(5, value.getLastUpdatedDate());
        }
      }
    };
    this.__updateAdapterOfBook = new EntityDeletionOrUpdateAdapter<Book>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `Book` SET `bookId` = ?,`name` = ?,`owner` = ?,`createdDate` = ?,`lastUpdatedDate` = ? WHERE `bookId` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Book value) {
        if (value.getBookId() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getBookId());
        }
        if (value.getName() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getName());
        }
        if (value.getOwner() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getOwner());
        }
        if (value.getCreatedDate() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindLong(4, value.getCreatedDate());
        }
        if (value.getLastUpdatedDate() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindLong(5, value.getLastUpdatedDate());
        }
        if (value.getBookId() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getBookId());
        }
      }
    };
    this.__preparedStmtOfDeleteAllData = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM Book";
        return _query;
      }
    };
  }

  @Override
  public long insert(Book book) {
    __db.beginTransaction();
    try {
      long _result = __insertionAdapterOfBook.insertAndReturnId(book);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(Book book) {
    __db.beginTransaction();
    try {
      __updateAdapterOfBook.handle(book);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public int deleteAllData() {
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllData.acquire();
    __db.beginTransaction();
    try {
      final int _result = _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
      __preparedStmtOfDeleteAllData.release(_stmt);
    }
  }

  @Override
  public Book getBookWithId(String id) {
    final String _sql = "SELECT * FROM Book WHERE bookId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (id == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, id);
    }
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfBookId = _cursor.getColumnIndexOrThrow("bookId");
      final int _cursorIndexOfName = _cursor.getColumnIndexOrThrow("name");
      final int _cursorIndexOfOwner = _cursor.getColumnIndexOrThrow("owner");
      final int _cursorIndexOfCreatedDate = _cursor.getColumnIndexOrThrow("createdDate");
      final int _cursorIndexOfLastUpdatedDate = _cursor.getColumnIndexOrThrow("lastUpdatedDate");
      final Book _result;
      if(_cursor.moveToFirst()) {
        _result = new Book();
        final String _tmpBookId;
        _tmpBookId = _cursor.getString(_cursorIndexOfBookId);
        _result.setBookId(_tmpBookId);
        final String _tmpName;
        _tmpName = _cursor.getString(_cursorIndexOfName);
        _result.setName(_tmpName);
        final String _tmpOwner;
        _tmpOwner = _cursor.getString(_cursorIndexOfOwner);
        _result.setOwner(_tmpOwner);
        final Long _tmpCreatedDate;
        if (_cursor.isNull(_cursorIndexOfCreatedDate)) {
          _tmpCreatedDate = null;
        } else {
          _tmpCreatedDate = _cursor.getLong(_cursorIndexOfCreatedDate);
        }
        _result.setCreatedDate(_tmpCreatedDate);
        final Long _tmpLastUpdatedDate;
        if (_cursor.isNull(_cursorIndexOfLastUpdatedDate)) {
          _tmpLastUpdatedDate = null;
        } else {
          _tmpLastUpdatedDate = _cursor.getLong(_cursorIndexOfLastUpdatedDate);
        }
        _result.setLastUpdatedDate(_tmpLastUpdatedDate);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<Book> getAllOwnedBooks(String uid) {
    final String _sql = "SELECT * FROM Book WHERE owner = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (uid == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, uid);
    }
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfBookId = _cursor.getColumnIndexOrThrow("bookId");
      final int _cursorIndexOfName = _cursor.getColumnIndexOrThrow("name");
      final int _cursorIndexOfOwner = _cursor.getColumnIndexOrThrow("owner");
      final int _cursorIndexOfCreatedDate = _cursor.getColumnIndexOrThrow("createdDate");
      final int _cursorIndexOfLastUpdatedDate = _cursor.getColumnIndexOrThrow("lastUpdatedDate");
      final List<Book> _result = new ArrayList<Book>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final Book _item;
        _item = new Book();
        final String _tmpBookId;
        _tmpBookId = _cursor.getString(_cursorIndexOfBookId);
        _item.setBookId(_tmpBookId);
        final String _tmpName;
        _tmpName = _cursor.getString(_cursorIndexOfName);
        _item.setName(_tmpName);
        final String _tmpOwner;
        _tmpOwner = _cursor.getString(_cursorIndexOfOwner);
        _item.setOwner(_tmpOwner);
        final Long _tmpCreatedDate;
        if (_cursor.isNull(_cursorIndexOfCreatedDate)) {
          _tmpCreatedDate = null;
        } else {
          _tmpCreatedDate = _cursor.getLong(_cursorIndexOfCreatedDate);
        }
        _item.setCreatedDate(_tmpCreatedDate);
        final Long _tmpLastUpdatedDate;
        if (_cursor.isNull(_cursorIndexOfLastUpdatedDate)) {
          _tmpLastUpdatedDate = null;
        } else {
          _tmpLastUpdatedDate = _cursor.getLong(_cursorIndexOfLastUpdatedDate);
        }
        _item.setLastUpdatedDate(_tmpLastUpdatedDate);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public LiveData<List<Book>> getAllBooks() {
    final String _sql = "SELECT * FROM Book ORDER BY lastUpdatedDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return new ComputableLiveData<List<Book>>() {
      private Observer _observer;

      @Override
      protected List<Book> compute() {
        if (_observer == null) {
          _observer = new Observer("Book") {
            @Override
            public void onInvalidated(@NonNull Set<String> tables) {
              invalidate();
            }
          };
          __db.getInvalidationTracker().addWeakObserver(_observer);
        }
        final Cursor _cursor = __db.query(_statement);
        try {
          final int _cursorIndexOfBookId = _cursor.getColumnIndexOrThrow("bookId");
          final int _cursorIndexOfName = _cursor.getColumnIndexOrThrow("name");
          final int _cursorIndexOfOwner = _cursor.getColumnIndexOrThrow("owner");
          final int _cursorIndexOfCreatedDate = _cursor.getColumnIndexOrThrow("createdDate");
          final int _cursorIndexOfLastUpdatedDate = _cursor.getColumnIndexOrThrow("lastUpdatedDate");
          final List<Book> _result = new ArrayList<Book>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final Book _item;
            _item = new Book();
            final String _tmpBookId;
            _tmpBookId = _cursor.getString(_cursorIndexOfBookId);
            _item.setBookId(_tmpBookId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            _item.setName(_tmpName);
            final String _tmpOwner;
            _tmpOwner = _cursor.getString(_cursorIndexOfOwner);
            _item.setOwner(_tmpOwner);
            final Long _tmpCreatedDate;
            if (_cursor.isNull(_cursorIndexOfCreatedDate)) {
              _tmpCreatedDate = null;
            } else {
              _tmpCreatedDate = _cursor.getLong(_cursorIndexOfCreatedDate);
            }
            _item.setCreatedDate(_tmpCreatedDate);
            final Long _tmpLastUpdatedDate;
            if (_cursor.isNull(_cursorIndexOfLastUpdatedDate)) {
              _tmpLastUpdatedDate = null;
            } else {
              _tmpLastUpdatedDate = _cursor.getLong(_cursorIndexOfLastUpdatedDate);
            }
            _item.setLastUpdatedDate(_tmpLastUpdatedDate);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    }.getLiveData();
  }
}
