package singareddy.productionapps.capturethemoment.card.getcards;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;

import singareddy.productionapps.capturethemoment.R;
import singareddy.productionapps.capturethemoment.models.Card;

public class BigCardBackFragment extends Fragment {

    private Card cardToBeDisplayed;
    private View fragmentView;
    private TextView date;
    private TextView location;
    private TextView description;
    private ConstraintLayout constraintLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_big_card_back,container, false);
        if (getActivity() instanceof BigCardActivity) {
            cardToBeDisplayed = ((BigCardActivity) getActivity()).cardToBeDisplayed;
        }
        constraintLayout = fragmentView.findViewById(R.id.fragment_big_card_back_cl_layout);
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BigCardActivity parent = (BigCardActivity) getActivity();
                parent.bigCardClicked();
            }
        });
        date = fragmentView.findViewById(R.id.fragment_big_card_back_tv_date);
        location = fragmentView.findViewById(R.id.fragment_big_card_back_tv_location);
        description = fragmentView.findViewById(R.id.fragment_big_card_back_tv_desc);
        date.setText(new Date(cardToBeDisplayed.getCreatedTime()).toString());
        location.setText(cardToBeDisplayed.getLocation());
        description.setText(cardToBeDisplayed.getDescription());
        return fragmentView;
    }
}
