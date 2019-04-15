package singareddy.productionapps.capturethemoment.models;

import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

@Entity (primaryKeys = {"bookId", "uid"})
public class ShareInfo {

    @NonNull private String bookId;
    @NonNull private String uid;
    @NonNull private Boolean canEdit;

    public ShareInfo() {
    }

    public ShareInfo(String bookId, Boolean canEdit) {
        this.bookId = bookId;
        this.canEdit = canEdit;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public Boolean getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(Boolean canEdit) {
        this.canEdit = canEdit;
    }

    @NonNull
    public String getUid() {
        return uid;
    }

    public void setUid(@NonNull String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        String s = "Book ID: "+bookId+" || Owned UID: "+ uid +" || "+canEdit;
        return s;
    }
}
