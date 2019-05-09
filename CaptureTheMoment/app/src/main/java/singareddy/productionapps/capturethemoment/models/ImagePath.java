package singareddy.productionapps.capturethemoment.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class ImagePath {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private long _id;
    private String cardId;
    private String imagePath;

    public ImagePath(String cardId, String imagePath) {
        this.cardId = cardId;
        this.imagePath = imagePath;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
