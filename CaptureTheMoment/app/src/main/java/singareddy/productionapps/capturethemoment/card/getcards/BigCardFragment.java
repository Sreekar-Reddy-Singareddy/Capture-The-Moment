package singareddy.productionapps.capturethemoment.card.getcards;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import singareddy.productionapps.capturethemoment.R;

public class BigCardFragment extends Fragment {
    private static String TAG = "BigCardFragment";

    private View fragmentView;
    private Button front, back;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.page_item_big_card, container, false);
        front = fragmentView.findViewById(R.id.page_item_big_card_bt_front);
        back = fragmentView.findViewById(R.id.page_item_big_card_bt_back);
        front.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BigCardFrontFragment frontFragment = new BigCardFrontFragment();
                getFragmentManager().beginTransaction().replace(R.id.page_item_big_card_cv_container, frontFragment).commit();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BigCardBackFragment backFragment = new BigCardBackFragment();
                getFragmentManager().beginTransaction().replace(R.id.page_item_big_card_cv_container, backFragment).commit();
            }
        });
        front.performClick();
        return fragmentView;
    }
}
