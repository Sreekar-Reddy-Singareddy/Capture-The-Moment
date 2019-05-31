package singareddy.productionapps.capturethemoment;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import singareddy.productionapps.capturethemoment.book.BookDao;
import singareddy.productionapps.capturethemoment.book.ShareInfoDao;
import singareddy.productionapps.capturethemoment.card.CardDao;
import singareddy.productionapps.capturethemoment.models.Book;
import singareddy.productionapps.capturethemoment.models.Card;
import singareddy.productionapps.capturethemoment.models.Friend;
import singareddy.productionapps.capturethemoment.models.ImagePath;
import singareddy.productionapps.capturethemoment.models.ShareInfo;

@Database (entities = {Book.class, ShareInfo.class, Card.class, Friend.class, ImagePath.class},
        exportSchema = false, version = 8)
public abstract class LocalDB extends RoomDatabase {
    private static String TAG = "LocalDB";

    private static LocalDB mLocalDB;
    public abstract BookDao getBookDao();
    public abstract CardDao getCardDao();
    public abstract ShareInfoDao getSharedInfoDao();

    public static LocalDB getInstance(Context context) {
        if (mLocalDB == null) {
            synchronized (new Object()) {
                mLocalDB = Room.databaseBuilder(context, LocalDB.class, "CaptureTheMomentDB")
                        .addCallback(new Callback() {
                            @Override
                            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                super.onCreate(db);
                                Log.i(TAG, "onCreate: Local Database Created.");
                            }
                        })
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        return mLocalDB;
    }
}
