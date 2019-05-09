package singareddy.productionapps.capturethemoment.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Friend {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private long _id;
    private String cardId;
    private String name;

    public Friend() {

    }

    public Friend(String cardId, String name) {
        this.cardId = cardId;
        this.name = name;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }
}
