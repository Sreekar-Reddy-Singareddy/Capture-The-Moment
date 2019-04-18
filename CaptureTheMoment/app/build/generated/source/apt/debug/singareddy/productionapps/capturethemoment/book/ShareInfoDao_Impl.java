package singareddy.productionapps.capturethemoment.book;

import android.arch.persistence.db.SupportSQLiteStatement;
import android.arch.persistence.room.EntityDeletionOrUpdateAdapter;
import android.arch.persistence.room.EntityInsertionAdapter;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.RoomSQLiteQuery;
import android.arch.persistence.room.SharedSQLiteStatement;
import android.database.Cursor;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;
import singareddy.productionapps.capturethemoment.models.ShareInfo;

@SuppressWarnings("unchecked")
public class ShareInfoDao_Impl implements ShareInfoDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter __insertionAdapterOfShareInfo;

  private final EntityDeletionOrUpdateAdapter __updateAdapterOfShareInfo;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllData;

  public ShareInfoDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfShareInfo = new EntityInsertionAdapter<ShareInfo>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `ShareInfo`(`bookId`,`uid`,`canEdit`) VALUES (?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, ShareInfo value) {
        if (value.getBookId() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getBookId());
        }
        if (value.getUid() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getUid());
        }
        final Integer _tmp;
        _tmp = value.getCanEdit() == null ? null : (value.getCanEdit() ? 1 : 0);
        if (_tmp == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindLong(3, _tmp);
        }
      }
    };
    this.__updateAdapterOfShareInfo = new EntityDeletionOrUpdateAdapter<ShareInfo>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `ShareInfo` SET `bookId` = ?,`uid` = ?,`canEdit` = ? WHERE `bookId` = ? AND `uid` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, ShareInfo value) {
        if (value.getBookId() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getBookId());
        }
        if (value.getUid() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getUid());
        }
        final Integer _tmp;
        _tmp = value.getCanEdit() == null ? null : (value.getCanEdit() ? 1 : 0);
        if (_tmp == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindLong(3, _tmp);
        }
        if (value.getBookId() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getBookId());
        }
        if (value.getUid() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getUid());
        }
      }
    };
    this.__preparedStmtOfDeleteAllData = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM ShareInfo";
        return _query;
      }
    };
  }

  @Override
  public long insertShareInfo(ShareInfo info) {
    __db.beginTransaction();
    try {
      long _result = __insertionAdapterOfShareInfo.insertAndReturnId(info);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void updateShareInfo(ShareInfo info) {
    __db.beginTransaction();
    try {
      __updateAdapterOfShareInfo.handle(info);
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
  public List<ShareInfo> getShareInfoForBookWithId(String id) {
    final String _sql = "SELECT * FROM ShareInfo WHERE bookId = ?";
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
      final int _cursorIndexOfUid = _cursor.getColumnIndexOrThrow("uid");
      final int _cursorIndexOfCanEdit = _cursor.getColumnIndexOrThrow("canEdit");
      final List<ShareInfo> _result = new ArrayList<ShareInfo>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final ShareInfo _item;
        _item = new ShareInfo();
        final String _tmpBookId;
        _tmpBookId = _cursor.getString(_cursorIndexOfBookId);
        _item.setBookId(_tmpBookId);
        final String _tmpUid;
        _tmpUid = _cursor.getString(_cursorIndexOfUid);
        _item.setUid(_tmpUid);
        final Boolean _tmpCanEdit;
        final Integer _tmp;
        if (_cursor.isNull(_cursorIndexOfCanEdit)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getInt(_cursorIndexOfCanEdit);
        }
        _tmpCanEdit = _tmp == null ? null : _tmp != 0;
        _item.setCanEdit(_tmpCanEdit);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<ShareInfo> getAllShareInfos() {
    final String _sql = "SELECT * FROM ShareInfo";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfBookId = _cursor.getColumnIndexOrThrow("bookId");
      final int _cursorIndexOfUid = _cursor.getColumnIndexOrThrow("uid");
      final int _cursorIndexOfCanEdit = _cursor.getColumnIndexOrThrow("canEdit");
      final List<ShareInfo> _result = new ArrayList<ShareInfo>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final ShareInfo _item;
        _item = new ShareInfo();
        final String _tmpBookId;
        _tmpBookId = _cursor.getString(_cursorIndexOfBookId);
        _item.setBookId(_tmpBookId);
        final String _tmpUid;
        _tmpUid = _cursor.getString(_cursorIndexOfUid);
        _item.setUid(_tmpUid);
        final Boolean _tmpCanEdit;
        final Integer _tmp;
        if (_cursor.isNull(_cursorIndexOfCanEdit)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getInt(_cursorIndexOfCanEdit);
        }
        _tmpCanEdit = _tmp == null ? null : _tmp != 0;
        _item.setCanEdit(_tmpCanEdit);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }
}
