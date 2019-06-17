package singareddy.productionapps.capturethemoment.card.edit;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import singareddy.productionapps.capturethemoment.R;

public class UpdateCardMoreDetailsFragment extends Fragment {
    private static String TAG = "UpdateCardMoreDetails";

    private UpdateCardActivity parent;
    private View fragView;
    private EditText cardDescView;
    private Button saveButton;
    private Button backButton;
    private EditText location;
    private TextView momentDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        parent = (UpdateCardActivity) getActivity();
        parent.cardUpdateSuccessFlag.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {

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
        cardDescView.setText(parent.cardToEdit.getDescription());
        momentDate = fragView.findViewById(R.id.frag_add_card_details_tv_date);
        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern("dd MMM YYYY");
        String formattedDate = format.format(new Date(parent.cardToEdit.getCreatedTime()));
        momentDate.setText(formattedDate);
        momentDate.setOnClickListener(this::selectMomentDate);
        location = fragView.findViewById(R.id.frag_add_card_details_et_location);
        location.setText(parent.cardToEdit.getLocation());


        backButton.setOnClickListener(this::goBack);
        saveButton.setOnClickListener(this::saveUpdatedCard);
    }

    private void saveUpdatedCard(View saveButton) {
        saveButton.setEnabled(false);
        parent.cardToEdit.setDescription(cardDescView.getText().toString());
        parent.cardToEdit.setLocation(location.getText().toString());
        parent.saveUpdatedCard();
    }

    private void selectMomentDate(View view) {
        DatePickerDialog dialog = new DatePickerDialog(getContext());
        dialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Log.i(TAG, "onDateSet: Day : "+dayOfMonth);
                Log.i(TAG, "onDateSet: Mon : "+month);
                Log.i(TAG, "onDateSet: Year: "+year);
                Date date = new Date();
                Calendar calendar= Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                parent.cardToEdit.setCreatedTime(calendar.getTimeInMillis());

                SimpleDateFormat format = new SimpleDateFormat();
                format.applyPattern("dd MMM YYYY");
                momentDate.setText(format.format(calendar.getTime()));
            }
        });
        dialog.show();
    }

    private void goBack(View backButton) {
        getFragmentManager().popBackStack();
    }
}
