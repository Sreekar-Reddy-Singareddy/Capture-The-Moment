package singareddy.productionapps.capturethemoment.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.List;

import singareddy.productionapps.capturethemoment.AppUtilities;

@Entity
public class Book {
    @NonNull @PrimaryKey(autoGenerate = false)
    private String bookId;
    private String name;
    private String owner;
    private Long createdTime;
    private Long lastOpenedTime;

    @Ignore private List<ShareInfo> secOwners;
    @Ignore private List<Card> cards;

    public Book() {
    }

    public Book(String name, String owner, Long createdTime) {
        this.name = name;
        this.owner = owner;
        this.createdTime = createdTime;
    }

    public Book(String name, String owner, List<ShareInfo> secOwners, Long createdTime) {
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

    public List<ShareInfo> getSecOwners() {
        return secOwners;
    }

    public void setSecOwners(List<ShareInfo> secOwners) {
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
