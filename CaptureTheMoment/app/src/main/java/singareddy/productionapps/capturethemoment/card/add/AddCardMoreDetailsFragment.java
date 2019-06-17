package singareddy.productionapps.capturethemoment.card.add;

import android.app.DatePickerDialog;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import singareddy.productionapps.capturethemoment.R;

public class AddCardMoreDetailsFragment extends Fragment {

    private static String TAG = "AddCardMoreDetailsFragment";

    private View fragView;
    private EditText desc;
    private Button create, back;
    private AddCardActivity parent;
    private EditText location;
    private TextView momentDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragView = inflater.inflate(R.layout.fragment_add_card_more_details, container, false);
        initialiseUI();
        return fragView;
    }

    private void initialiseUI() {
        parent = (AddCardActivity) getActivity();
        desc = fragView.findViewById(R.id.frag_add_card_details_et_desc);
        create = fragView.findViewById(R.id.frag_add_card_details_bt_create);
        back = fragView.findViewById(R.id.frag_add_card_details_bt_back);
        location = fragView.findViewById(R.id.frag_add_card_details_et_location);
        momentDate = fragView.findViewById(R.id.frag_add_card_details_tv_date);
        momentDate.setOnClickListener(this::selectMomentDate);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.cardDescription = desc.getText().toString();
                parent.cardLocation = location.getText().toString();
                parent.saveCard();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
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
                parent.cardDate = calendar.getTimeInMillis();

                SimpleDateFormat format = new SimpleDateFormat();
                format.applyPattern("dd MMM YYYY");
                momentDate.setText(format.format(calendar.getTime()));
            }
        });
        dialog.show();
    }
}
