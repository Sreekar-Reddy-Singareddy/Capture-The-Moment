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
import singareddy.productionapps.capturethemoment.card.CardDao;
import singareddy.productionapps.capturethemoment.card.CardDao_Impl;

@SuppressWarnings("unchecked")
public class LocalDB_Impl extends LocalDB {
  private volatile BookDao _bookDao;

  private volatile CardDao _cardDao;

  private volatile ShareInfoDao _shareInfoDao;

  @Override
  protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration configuration) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(configuration, new RoomOpenHelper.Delegate(8) {
      @Override
      public void createAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("CREATE TABLE IF NOT EXISTS `Book` (`bookId` TEXT NOT NULL, `name` TEXT, `owner` TEXT, `ownerName` TEXT, `createdDate` INTEGER, `lastUpdatedDate` INTEGER, PRIMARY KEY(`bookId`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `ShareInfo` (`bookId` TEXT NOT NULL, `uid` TEXT NOT NULL, `canEdit` INTEGER NOT NULL, PRIMARY KEY(`bookId`, `uid`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `Card` (`cardId` TEXT NOT NULL, `bookId` TEXT, `description` TEXT, `location` TEXT, `createdTime` INTEGER, PRIMARY KEY(`cardId`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `Friend` (`_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `cardId` TEXT, `name` TEXT)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `ImagePath` (`_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `cardId` TEXT, `imagePath` TEXT)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        _db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, \"27a7ff88827289f75e83aae16ea21d49\")");
      }

      @Override
      public void dropAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("DROP TABLE IF EXISTS `Book`");
        _db.execSQL("DROP TABLE IF EXISTS `ShareInfo`");
        _db.execSQL("DROP TABLE IF EXISTS `Card`");
        _db.execSQL("DROP TABLE IF EXISTS `Friend`");
        _db.execSQL("DROP TABLE IF EXISTS `ImagePath`");
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
        final HashMap<String, TableInfo.Column> _columnsBook = new HashMap<String, TableInfo.Column>(6);
        _columnsBook.put("bookId", new TableInfo.Column("bookId", "TEXT", true, 1));
        _columnsBook.put("name", new TableInfo.Column("name", "TEXT", false, 0));
        _columnsBook.put("owner", new TableInfo.Column("owner", "TEXT", false, 0));
        _columnsBook.put("ownerName", new TableInfo.Column("ownerName", "TEXT", false, 0));
        _columnsBook.put("createdDate", new TableInfo.Column("createdDate", "INTEGER", false, 0));
        _columnsBook.put("lastUpdatedDate", new TableInfo.Column("lastUpdatedDate", "INTEGER", false, 0));
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
        final HashMap<String, TableInfo.Column> _columnsCard = new HashMap<String, TableInfo.Column>(5);
        _columnsCard.put("cardId", new TableInfo.Column("cardId", "TEXT", true, 1));
        _columnsCard.put("bookId", new TableInfo.Column("bookId", "TEXT", false, 0));
        _columnsCard.put("description", new TableInfo.Column("description", "TEXT", false, 0));
        _columnsCard.put("location", new TableInfo.Column("location", "TEXT", false, 0));
        _columnsCard.put("createdTime", new TableInfo.Column("createdTime", "INTEGER", false, 0));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCard = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCard = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCard = new TableInfo("Card", _columnsCard, _foreignKeysCard, _indicesCard);
        final TableInfo _existingCard = TableInfo.read(_db, "Card");
        if (! _infoCard.equals(_existingCard)) {
          throw new IllegalStateException("Migration didn't properly handle Card(singareddy.productionapps.capturethemoment.models.Card).\n"
                  + " Expected:\n" + _infoCard + "\n"
                  + " Found:\n" + _existingCard);
        }
        final HashMap<String, TableInfo.Column> _columnsFriend = new HashMap<String, TableInfo.Column>(3);
        _columnsFriend.put("_id", new TableInfo.Column("_id", "INTEGER", true, 1));
        _columnsFriend.put("cardId", new TableInfo.Column("cardId", "TEXT", false, 0));
        _columnsFriend.put("name", new TableInfo.Column("name", "TEXT", false, 0));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFriend = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesFriend = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoFriend = new TableInfo("Friend", _columnsFriend, _foreignKeysFriend, _indicesFriend);
        final TableInfo _existingFriend = TableInfo.read(_db, "Friend");
        if (! _infoFriend.equals(_existingFriend)) {
          throw new IllegalStateException("Migration didn't properly handle Friend(singareddy.productionapps.capturethemoment.models.Friend).\n"
                  + " Expected:\n" + _infoFriend + "\n"
                  + " Found:\n" + _existingFriend);
        }
        final HashMap<String, TableInfo.Column> _columnsImagePath = new HashMap<String, TableInfo.Column>(3);
        _columnsImagePath.put("_id", new TableInfo.Column("_id", "INTEGER", true, 1));
        _columnsImagePath.put("cardId", new TableInfo.Column("cardId", "TEXT", false, 0));
        _columnsImagePath.put("imagePath", new TableInfo.Column("imagePath", "TEXT", false, 0));
        final HashSet<TableInfo.ForeignKey> _foreignKeysImagePath = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesImagePath = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoImagePath = new TableInfo("ImagePath", _columnsImagePath, _foreignKeysImagePath, _indicesImagePath);
        final TableInfo _existingImagePath = TableInfo.read(_db, "ImagePath");
        if (! _infoImagePath.equals(_existingImagePath)) {
          throw new IllegalStateException("Migration didn't properly handle ImagePath(singareddy.productionapps.capturethemoment.models.ImagePath).\n"
                  + " Expected:\n" + _infoImagePath + "\n"
                  + " Found:\n" + _existingImagePath);
        }
      }
    }, "27a7ff88827289f75e83aae16ea21d49", "37a66d66daa31e7b2dcd104156b3f7ae");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(configuration.context)
        .name(configuration.name)
        .callback(_openCallback)
        .build();
    final SupportSQLiteOpenHelper _helper = configuration.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  protected InvalidationTracker createInvalidationTracker() {
    return new InvalidationTracker(this, "Book","ShareInfo","Card","Friend","ImagePath");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `Book`");
      _db.execSQL("DELETE FROM `ShareInfo`");
      _db.execSQL("DELETE FROM `Card`");
      _db.execSQL("DELETE FROM `Friend`");
      _db.execSQL("DELETE FROM `ImagePath`");
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
  public CardDao getCardDao() {
    if (_cardDao != null) {
      return _cardDao;
    } else {
      synchronized(this) {
        if(_cardDao == null) {
          _cardDao = new CardDao_Impl(this);
        }
        return _cardDao;
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
