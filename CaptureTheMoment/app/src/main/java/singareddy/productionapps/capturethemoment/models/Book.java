package singareddy.productionapps.capturethemoment.models;

import java.util.List;

public class Book {
    private String name;
    private String owner;
    private List<String> secOwners;
    private List<Card> cards;
    private Long createdTime;
    private Long lastOpenedTime;

    public Book() {
    }

    public Book(String name, String owner, Long createdTime) {
        this.name = name;
        this.owner = owner;
        this.createdTime = createdTime;
    }

    public Book(String name, String owner, List<String> secOwners, Long createdTime) {
        this.name = name;
        this.owner = owner;
        this.secOwners = secOwners;
        this.createdTime = createdTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List<String> getSecOwners() {
        return secOwners;
    }

    public void setSecOwners(List<String> secOwners) {
        this.secOwners = secOwners;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public Long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

    public Long getLastOpenedTime() {
        return lastOpenedTime;
    }

    public void setLastOpenedTime(Long lastOpenedTime) {
        this.lastOpenedTime = lastOpenedTime;
    }
}
