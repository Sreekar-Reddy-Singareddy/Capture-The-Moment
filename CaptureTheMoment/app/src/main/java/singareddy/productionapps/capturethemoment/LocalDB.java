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
import singareddy.productionapps.capturethemoment.models.Book;
import singareddy.productionapps.capturethemoment.models.ShareInfo;

@Database (entities = {Book.class, ShareInfo.class}, exportSchema = false, version = 1)
public abstract class LocalDB extends RoomDatabase {
    private static String TAG = "LocalDB";

    private static LocalDB mLocalDB;
    public abstract BookDao getBookDao();
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
