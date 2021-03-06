package singareddy.productionapps.capturethemoment.book.add;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.List;

import singareddy.productionapps.capturethemoment.R;
import singareddy.productionapps.capturethemoment.book.edit.OwnerRemoveClickListener;
import singareddy.productionapps.capturethemoment.models.SecondaryOwner;

import static singareddy.productionapps.capturethemoment.utils.AppUtilities.Book.*;

public class SecOwnersAdapter extends RecyclerView.Adapter<SecOwnersAdapter.SecOwnersViewHolder> {
    private static String TAG = "SecOwnersAdapter";

    public class SecOwnersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, TextWatcher{
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
            removeOwner.setOnClickListener(this);
            editAccess.setOnCheckedChangeListener(this);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v == removeOwner) {
                SecondaryOwner removedOwner = data.remove(getAdapterPosition());
                notifyDataSetChanged();
                if (listener != null) listener.onOwnerRemoved(removedOwner);
            }
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
    }

    Context context;
    LayoutInflater inflater;
    List<SecondaryOwner> data;
    OwnerRemoveClickListener listener;

    public SecOwnersAdapter (Context context, List<SecondaryOwner> data) {
        this.context = context;
        this.data = data;
        this.inflater = LayoutInflater.from(context);
    }

    public SecOwnersAdapter(Context context, List<SecondaryOwner> data, OwnerRemoveClickListener listener) {
        this(context, data);
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
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
        if (secondaryOwnerObj.getValidated() == SEC_OWNER_INVALID) {
            secOwnersViewHolder.statusView.setImageResource(R.drawable.close_red);
            secOwnersViewHolder.statusView.setVisibility(View.VISIBLE);
            secOwnersViewHolder.username.setEnabled(true);
        }
        else if (secondaryOwnerObj.getValidated() == SEC_OWNER_VALID) {
            secOwnersViewHolder.statusView.setImageResource(R.drawable.done);
            secOwnersViewHolder.statusView.setVisibility(View.VISIBLE);
            secOwnersViewHolder.username.setEnabled(false);
        }
        else if (secondaryOwnerObj.getValidated() == SEC_OWNER_DUPLICATE) {
            secOwnersViewHolder.statusView.setImageResource(R.drawable.warning);
            secOwnersViewHolder.statusView.setVisibility(View.VISIBLE);
            secOwnersViewHolder.username.setEnabled(true);
        }
        else {
            secOwnersViewHolder.statusView.setVisibility(View.GONE);
            secOwnersViewHolder.username.setEnabled(true);
        }
        secOwnersViewHolder.username.setText(secondaryOwnerObj.getUsername());
        secOwnersViewHolder.editAccess.setChecked(secondaryOwnerObj.getCanEdit());
    }
}
