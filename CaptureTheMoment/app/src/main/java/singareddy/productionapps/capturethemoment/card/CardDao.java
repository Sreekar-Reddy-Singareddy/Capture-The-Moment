package singareddy.productionapps.capturethemoment.card;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import singareddy.productionapps.capturethemoment.models.Card;
import singareddy.productionapps.capturethemoment.models.Friend;
import singareddy.productionapps.capturethemoment.models.ImagePath;

@Dao
public interface CardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long insertCard(Card card);

    @Insert
    public long insertFriend (Friend friend);

    @Insert
    public long insertImagePath (ImagePath path);

    @Query("SELECT * FROM Card WHERE bookId = :bookId")
    public LiveData<List<Card>> getAllCardsUnderBook(String bookId);

    @Query("SELECT imagePath FROM ImagePath WHERE cardId = :cardId LIMIT 1")
    public String getOneImagePathForCard(String cardId);

    @Query("SELECT * FROM Card WHERE cardId = :cardId")
    public LiveData<Card> getCardWithId(String cardId);

    @Query("SELECT imagePath FROM ImagePath WHERE cardId = :cardId" )
    public List<String> getImagePathsForCard(String cardId);

    @Query("DELETE FROM Friend WHERE cardId = :cardId")
    public int deleteAllFriendsOfCard (String cardId);

    @Query("DELETE FROM ImagePath WHERE cardId = :cardId")
    public int deleteAllPathsOfCard (String cardId);

    @Query("DELETE FROM Card WHERE cardId = :cardId")
    public int deleteCard(String cardId);

    @Query("SELECT cardId FROM Card WHERE bookId = :bookId")
    public List<String> getAllCardIdsUnderBook (String bookId);

    @Query("DELETE FROM Card WHERE bookId = :bookId")
    public int deleteAllCardsUnderBook (String bookId);

    @Query("DELETE FROM CARD")
    public int eraseAllCardData();

    @Query("DELETE FROM Friend")
    public int eraseAllFriendData();

    @Query("DELETE FROM IMAGEPATH")
    public int eraseAllImagePathData();

    @Query("SELECT imagePath FROM ImagePath WHERE cardId = " +
            "(SELECT cardId FROM Card WHERE bookId = :bookId LIMIT 1) LIMIT 1")
    public LiveData<String> getImagePathForOneCardInTheBook(String bookId);
}
