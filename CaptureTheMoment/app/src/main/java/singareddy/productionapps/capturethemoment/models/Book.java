package singareddy.productionapps.capturethemoment.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.List;

import singareddy.productionapps.capturethemoment.AppUtilities;

@Entity
public class Book {
    @NonNull @PrimaryKey(autoGenerate = false)
    private String bookId;
    private String name;
    private String owner;
    private Long createdDate;
    private Long lastUpdatedDate;

    @Ignore private HashMap<String, Boolean> secOwners;
    @Ignore private List<Card> cards;

    public Book() {
    }

    public Book(String name, String owner, Long createdDate) {
        this.name = name;
        this.owner = owner;
        this.createdDate = createdDate;
    }

    public Book(String name, String owner, HashMap<String, Boolean> secOwners, Long createdDate) {
        this.name = name;
        this.owner = owner;
        this.secOwners = secOwners;
        this.createdDate = createdDate;
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

    public HashMap<String, Boolean> getSecOwners() {
        return secOwners;
    }

    public void setSecOwners(HashMap<String, Boolean> secOwners) {
        this.secOwners = secOwners;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public Long getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Long lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    @NonNull
    public String getBookId() {
        return bookId;
    }

    public void setBookId(@NonNull String bookId) {
        this.bookId = bookId;
    }

    @Override
    public String toString() {
        String s = "ID: "+bookId+" || Name: "+name+" || Owner: "+ owner+" || Sec owners: "+secOwners;
        return s;
    }

    public boolean doIOwnTheBook() {
        return bookId.equals(AppUtilities.User.CURRENT_USER.getUid()+"__"+this.name.toLowerCase().trim());
    }
}
