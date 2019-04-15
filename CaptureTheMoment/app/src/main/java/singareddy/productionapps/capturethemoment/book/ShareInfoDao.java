package singareddy.productionapps.capturethemoment.book;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import singareddy.productionapps.capturethemoment.models.ShareInfo;

@Dao
public interface ShareInfoDao {

    @Insert
    public void insertShareInfo (ShareInfo info);

    @Update
    public void updateShareInfo (ShareInfo info);

    @Query("SELECT * FROM ShareInfo WHERE bookId = :id")
    public List<ShareInfo> getShareInfoForBookWithId (String id);
}
