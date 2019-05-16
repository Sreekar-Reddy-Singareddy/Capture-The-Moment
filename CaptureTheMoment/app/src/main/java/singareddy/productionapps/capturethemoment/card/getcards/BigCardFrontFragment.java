package singareddy.productionapps.capturethemoment.card.getcards;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

import singareddy.productionapps.capturethemoment.R;
import singareddy.productionapps.capturethemoment.utils.DepthPageTransformer;
import singareddy.productionapps.capturethemoment.models.Card;

public class BigCardFrontFragment extends Fragment implements BigCardClickListener {
    private static String TAG = "BigCardFrontFragment";

    private Card cardToBeDisplayed;
    private View fragmentView;
    private ViewPager imagesViewPager;
    private LinearLayout constraintLayout;
    private ImagePageAdapter imagesAdapter;
    private List<Uri> imageUris;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_big_card_front,container, false);

        if (getActivity() instanceof BigCardActivity) {
            cardToBeDisplayed = ((BigCardActivity) getActivity()).cardToBeDisplayed;
            imageUris = ((BigCardActivity) getActivity()).imageUris;
        }
        imagesAdapter = new ImagePageAdapter(getContext(), getChildFragmentManager(), imageUris, this);
        imagesAdapter.setFramedImage(1);
        imagesViewPager = fragmentView.findViewById(R.id.fragment_big_card_front_pv_images);
        imagesViewPager.setAdapter(imagesAdapter);
        imagesAdapter.notifyDataSetChanged();
        Log.i(TAG, "onCreateView: ADAPTER: "+imagesAdapter);
        return fragmentView;
    }

    @Override
    public void bigCardClicked() {
        Log.i(TAG, "bigCardClicked: *");
        BigCardActivity parent = (BigCardActivity) getActivity();
        parent.bigCardClicked();
    }
}
