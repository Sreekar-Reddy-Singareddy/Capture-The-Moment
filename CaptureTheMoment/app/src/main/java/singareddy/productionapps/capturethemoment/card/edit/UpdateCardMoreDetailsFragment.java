package singareddy.productionapps.capturethemoment.card.edit;

import android.arch.lifecycle.Observer;
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

public class UpdateCardMoreDetailsFragment extends Fragment {
    private static String TAG = "UpdateCardMoreDetails";

    private UpdateCardActivity parent;
    private View fragView;
    private EditText cardDescView;
    private Button saveButton;
    private Button backButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        parent = (UpdateCardActivity) getActivity();
        parent.cardUpdateSuccessFlag.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                Log.i(TAG, "onChanged: CARD UPDATE SUCCESSFUL = "+aBoolean);
            }
        });
        fragView = inflater.inflate(R.layout.fragment_add_card_more_details, container, false);
        initialiseUI();
        return fragView;
    }

    private void initialiseUI() {
        backButton = fragView.findViewById(R.id.frag_add_card_details_bt_back);
        saveButton = fragView.findViewById(R.id.frag_add_card_details_bt_create);
        cardDescView = fragView.findViewById(R.id.frag_add_card_details_et_desc);

        backButton.setOnClickListener(this::goBack);
        saveButton.setOnClickListener(this::saveUpdatedCard);
    }

    private void saveUpdatedCard(View saveButton) {
        saveButton.setEnabled(false);
        parent.cardToEdit.setDescription("This is the updated card description");
        parent.saveUpdatedCard();
    }

    private void goBack(View backButton) {
        getFragmentManager().popBackStack();
    }
}
