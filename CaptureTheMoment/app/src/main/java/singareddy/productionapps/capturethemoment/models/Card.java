package singareddy.productionapps.capturethemoment.models;

import java.util.List;

public class Card {
    private String cardId;
    private List<String> imagePaths;
    private String description;
    private String location;
    private List<String> friends;
    private Long createdTime;
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

    // TODO: Override equals method for Card
}
