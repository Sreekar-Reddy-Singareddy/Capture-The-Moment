package singareddy.productionapps.capturethemoment.card.getcards;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.List;

import singareddy.productionapps.capturethemoment.models.Card;

public class BigCardAdapter extends FragmentPagerAdapter {
    private static String TAG = "BigCardAdapter";

    private Context context;
    private FragmentManager fragmentManager;
    private List<Card> allCards;
    private Integer positionOfSelectedCard;

    public BigCardAdapter (Context context, FragmentManager manager, List<Card> allCards) {
        super(manager);
        this.context = context;
        this.fragmentManager = manager;
        this.allCards = allCards;
    }

    public void setAllCards(List<Card> allCards) {
        this.allCards = allCards;
    }

    @Override
    public int getCount() {
        if (allCards == null) return 0;
        return allCards.size();
    }

    @Override
    public Fragment getItem(int i) {
        Log.i(TAG, "getItem: Card No: "+i);
        BigCardFragment pageFragment = new BigCardFragment();
        return pageFragment;
    }
}
