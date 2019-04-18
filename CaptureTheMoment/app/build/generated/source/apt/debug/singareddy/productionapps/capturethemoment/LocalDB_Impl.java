package singareddy.productionapps.capturethemoment;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.db.SupportSQLiteOpenHelper.Callback;
import android.arch.persistence.db.SupportSQLiteOpenHelper.Configuration;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.RoomOpenHelper;
import android.arch.persistence.room.RoomOpenHelper.Delegate;
import android.arch.persistence.room.util.TableInfo;
import android.arch.persistence.room.util.TableInfo.Column;
import android.arch.persistence.room.util.TableInfo.ForeignKey;
import android.arch.persistence.room.util.TableInfo.Index;
import java.lang.IllegalStateException;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.HashMap;
import java.util.HashSet;
import singareddy.productionapps.capturethemoment.book.BookDao;
import singareddy.productionapps.capturethemoment.book.BookDao_Impl;
import singareddy.productionapps.capturethemoment.book.ShareInfoDao;
import singareddy.productionapps.capturethemoment.book.ShareInfoDao_Impl;

@SuppressWarnings("unchecked")
public class LocalDB_Impl extends LocalDB {
  private volatile BookDao _bookDao;

  private volatile ShareInfoDao _shareInfoDao;

  @Override
  protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration configuration) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(configuration, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("CREATE TABLE IF NOT EXISTS `Book` (`bookId` TEXT NOT NULL, `name` TEXT, `owner` TEXT, `createdTime` INTEGER, `lastOpenedTime` INTEGER, PRIMARY KEY(`bookId`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `ShareInfo` (`bookId` TEXT NOT NULL, `uid` TEXT NOT NULL, `canEdit` INTEGER NOT NULL, PRIMARY KEY(`bookId`, `uid`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        _db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, \"417f942d4bc814da1b9151a2cbe984db\")");
      }

      @Override
      public void dropAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("DROP TABLE IF EXISTS `Book`");
        _db.execSQL("DROP TABLE IF EXISTS `ShareInfo`");
      }

      @Override
      protected void onCreate(SupportSQLiteDatabase _db) {
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onCreate(_db);
          }
        }
      }

      @Override
      public void onOpen(SupportSQLiteDatabase _db) {
        mDatabase = _db;
        internalInitInvalidationTracker(_db);
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onOpen(_db);
          }
        }
      }

      @Override
      protected void validateMigration(SupportSQLiteDatabase _db) {
        final HashMap<String, TableInfo.Column> _columnsBook = new HashMap<String, TableInfo.Column>(5);
        _columnsBook.put("bookId", new TableInfo.Column("bookId", "TEXT", true, 1));
        _columnsBook.put("name", new TableInfo.Column("name", "TEXT", false, 0));
        _columnsBook.put("owner", new TableInfo.Column("owner", "TEXT", false, 0));
        _columnsBook.put("createdTime", new TableInfo.Column("createdTime", "INTEGER", false, 0));
        _columnsBook.put("lastOpenedTime", new TableInfo.Column("lastOpenedTime", "INTEGER", false, 0));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBook = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesBook = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoBook = new TableInfo("Book", _columnsBook, _foreignKeysBook, _indicesBook);
        final TableInfo _existingBook = TableInfo.read(_db, "Book");
        if (! _infoBook.equals(_existingBook)) {
          throw new IllegalStateException("Migration didn't properly handle Book(singareddy.productionapps.capturethemoment.models.Book).\n"
                  + " Expected:\n" + _infoBook + "\n"
                  + " Found:\n" + _existingBook);
        }
        final HashMap<String, TableInfo.Column> _columnsShareInfo = new HashMap<String, TableInfo.Column>(3);
        _columnsShareInfo.put("bookId", new TableInfo.Column("bookId", "TEXT", true, 1));
        _columnsShareInfo.put("uid", new TableInfo.Column("uid", "TEXT", true, 2));
        _columnsShareInfo.put("canEdit", new TableInfo.Column("canEdit", "INTEGER", true, 0));
        final HashSet<TableInfo.ForeignKey> _foreignKeysShareInfo = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesShareInfo = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoShareInfo = new TableInfo("ShareInfo", _columnsShareInfo, _foreignKeysShareInfo, _indicesShareInfo);
        final TableInfo _existingShareInfo = TableInfo.read(_db, "ShareInfo");
        if (! _infoShareInfo.equals(_existingShareInfo)) {
          throw new IllegalStateException("Migration didn't properly handle ShareInfo(singareddy.productionapps.capturethemoment.models.ShareInfo).\n"
                  + " Expected:\n" + _infoShareInfo + "\n"
                  + " Found:\n" + _existingShareInfo);
        }
      }
    }, "417f942d4bc814da1b9151a2cbe984db", "92d4187be47d947ab90243ef63ea6c1b");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(configuration.context)
        .name(configuration.name)
        .callback(_openCallback)
        .build();
    final SupportSQLiteOpenHelper _helper = configuration.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  protected InvalidationTracker createInvalidationTracker() {
    return new InvalidationTracker(this, "Book","ShareInfo");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `Book`");
      _db.execSQL("DELETE FROM `ShareInfo`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  public BookDao getBookDao() {
    if (_bookDao != null) {
      return _bookDao;
    } else {
      synchronized(this) {
        if(_bookDao == null) {
          _bookDao = new BookDao_Impl(this);
        }
        return _bookDao;
      }
    }
  }

  @Override
  public ShareInfoDao getSharedInfoDao() {
    if (_shareInfoDao != null) {
      return _shareInfoDao;
    } else {
      synchronized(this) {
        if(_shareInfoDao == null) {
          _shareInfoDao = new ShareInfoDao_Impl(this);
        }
        return _shareInfoDao;
      }
    }
  }
}
