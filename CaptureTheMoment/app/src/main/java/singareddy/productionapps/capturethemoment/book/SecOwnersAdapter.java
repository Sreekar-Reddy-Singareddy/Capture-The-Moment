package singareddy.productionapps.capturethemoment.book;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

import singareddy.productionapps.capturethemoment.R;
import singareddy.productionapps.capturethemoment.models.SecondaryOwner;

import static singareddy.productionapps.capturethemoment.AppUtilities.User.*;
import static singareddy.productionapps.capturethemoment.AppUtilities.Firebase.*;

public class SecOwnersAdapter extends RecyclerView.Adapter<SecOwnersAdapter.SecOwnersViewHolder> {
    private static String TAG = "SecOwnersAdapter";

    public class SecOwnersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, TextWatcher, View.OnFocusChangeListener {
        EditText username;
        ImageButton removeOwner;
        CheckBox editAccess;
        ImageView statusView;

        public SecOwnersViewHolder (View view) {
            super(view);
            username = view.findViewById(R.id.sec_owner_et_username);
            removeOwner = view.findViewById(R.id.sec_owner_ib_cancel);
            editAccess = view.findViewById(R.id.sec_owner_cb_edit);
            statusView = view.findViewById(R.id.sec_owner_iv_status);
            statusView.setVisibility(View.GONE);

            username.addTextChangedListener(this);
            username.setOnFocusChangeListener(this);
            removeOwner.setOnClickListener(this);
            editAccess.setOnCheckedChangeListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.i(TAG, "onClick: Adapter Position: "+getAdapterPosition());
            Log.i(TAG, "onClick: Layout Position : "+getLayoutPosition());
            data.remove(getAdapterPosition());
            notifyDataSetChanged();
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            data.get(getAdapterPosition()).setCanEdit(isChecked);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            data.get(getAdapterPosition()).setUsername((username.getText().toString()));
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (v == username) {
                if (LOGIN_PROVIDER.equals(EMAIL_PROVIDER) && username.getText() != null) {
                    if (CURRENT_USER_EMAIL.equals(username.getText().toString().toLowerCase())) {
                        // Same mobile number given
                        Toast.makeText(context, "Your email id cannot be given here.", Toast.LENGTH_SHORT).show();
                        username.setText("");
                    }
                }
                else if (LOGIN_PROVIDER.equals(PHONE_PROVIDER) && username.getText() != null) {
                    if (CURRENT_USER_MOBILE.equals(username.getText().toString().toLowerCase())) {
                        // Same email id given
                        Toast.makeText(context, "Your mobile number cannot be given here.", Toast.LENGTH_SHORT).show();
                        username.setText("");
                    }
                }
            }
        }
    }

    Context context;
    LayoutInflater inflater;
    List<SecondaryOwner> data;

    public SecOwnersAdapter (Context context, List<SecondaryOwner> data) {
        this.context = context;
        this.data = data;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemCount() {
        Log.i(TAG, "getItemCount: "+data.size());
        return data.size();
    }

    @NonNull
    @Override
    public SecOwnersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = inflater.inflate(R.layout.list_item_sec_owner, viewGroup, false);
        return new SecOwnersViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SecOwnersViewHolder secOwnersViewHolder, int i) {
        SecondaryOwner secondaryOwnerObj = data.get(i);
        if (secondaryOwnerObj.getValidated() == -1) {
            secOwnersViewHolder.statusView.setImageResource(R.drawable.ic_close_red_24dp);
            secOwnersViewHolder.statusView.setVisibility(View.VISIBLE);
            secOwnersViewHolder.username.setEnabled(true);
        }
        else if (secondaryOwnerObj.getValidated() == 1) {
            secOwnersViewHolder.statusView.setImageResource(R.drawable.ic_done_green_24dp);
            secOwnersViewHolder.statusView.setVisibility(View.VISIBLE);
            secOwnersViewHolder.username.setEnabled(false);
        }
        else {
            secOwnersViewHolder.statusView.setVisibility(View.GONE);
            secOwnersViewHolder.username.setEnabled(true);
        }
        secOwnersViewHolder.username.setText(secondaryOwnerObj.getUsername());
        secOwnersViewHolder.editAccess.setChecked(secondaryOwnerObj.getCanEdit());
    }
}
