package singareddy.productionapps.capturethemoment.card.add;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import singareddy.productionapps.capturethemoment.R;

public class AddCardMoreDetailsFragment extends Fragment {

    private static String TAG = "AddCardMoreDetailsFragment";

    private View fragView;
    private EditText desc;
    private Button create, back;
    private AddCardActivity parent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        parent = (AddCardActivity) getActivity();
        fragView = inflater.inflate(R.layout.fragment_add_card_more_details, container, false);
        desc = fragView.findViewById(R.id.frag_add_card_details_et_desc);
        create = fragView.findViewById(R.id.frag_add_card_details_bt_create);
        back = fragView.findViewById(R.id.frag_add_card_details_bt_back);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.cardDescription = desc.getText().toString();
                parent.saveCard();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: FRAG MANAGER: "+getFragmentManager().getFragments());
                getFragmentManager().popBackStack();
//                parent.photosFrag();
            }
        });
        return fragView;
    }
}
