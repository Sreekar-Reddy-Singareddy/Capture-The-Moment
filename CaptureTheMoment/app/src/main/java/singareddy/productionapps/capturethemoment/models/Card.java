package singareddy.productionapps.capturethemoment.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.List;

@Entity
public class Card {
    @PrimaryKey
    @NonNull
    private String cardId;
    private String bookId;
    private String description;
    private String location;
    private Long createdTime;

    @Ignore
    private List<String> imagePaths;
    @Ignore
    private List<String> friends;
    @Ignore
    private Long modifiedTime;

    public Card() {
    }

    public Card(List<String> imagePaths, String description, String location, List<String> friends) {
        this.imagePaths = imagePaths;
        this.description = description;
        this.location = location;
        this.friends = friends;
    }

    public Card(List<String> imagePaths, String description) {
        this.imagePaths = imagePaths;
        this.description = description;
    }

    public Card(List<String> imagePaths, String description, Long createdTime) {
        this.imagePaths = imagePaths;
        this.description = description;
        this.createdTime = createdTime;
    }

    public Card(List<String> imagePaths, String description, String location, List<String> friends, Long createdTime) {
        this.imagePaths = imagePaths;
        this.description = description;
        this.location = location;
        this.friends = friends;
        this.createdTime = createdTime;
    }

    public Card(String cardId) {
        this.cardId = cardId;
    }

    public List<String> getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(List<String> imagePaths) {
        this.imagePaths = imagePaths;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public Long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

    public Long getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Long modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    // TODO: Override equals method for Card
}
