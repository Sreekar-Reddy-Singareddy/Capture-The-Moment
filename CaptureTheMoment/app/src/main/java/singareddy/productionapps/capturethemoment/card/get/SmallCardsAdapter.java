package singareddy.productionapps.capturethemoment.card.get;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.util.List;

import singareddy.productionapps.capturethemoment.R;
import singareddy.productionapps.capturethemoment.card.add.AddCardActivity;

public class SmallCardsAdapter extends RecyclerView.Adapter<SmallCardsAdapter.SmallCardVH> {
    private static String TAG = "SmallCardsAdapter";

    public class SmallCardVH extends RecyclerView.ViewHolder {
        ImageView image;

        public SmallCardVH(View view) {
            super(view);
            String itemType = (String) view.getTag();
            if (itemType.equals("add_card")) {
                image = null;
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println("Add New Card Under... "+bookId);
                        Intent intent = new Intent(context, AddCardActivity.class);
                        intent.putExtra("bookId", bookId);
                        context.startActivity(intent);
                    }
                });
            }
            else if (itemType.equals("small_card")) {
                image = view.findViewById(R.id.list_item_small_card_iv_image);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Small card has been clicked.
                        // Tell the listener which card has been clicked.
                        int cardPosition = ownerCanEdit ? getAdapterPosition()-1 : getAdapterPosition();
                        cardClickListener.onSmallCardClicked(cardPosition);
                    }
                });
            }
        }
    }

    private Context context;
    private LayoutInflater inflater;
    private List<String> data;
    private String bookId;
    private SmallCardClickListener cardClickListener;
    private Boolean ownerCanEdit  = true;

    public SmallCardsAdapter(Context context, List<String> data, String bookId, SmallCardClickListener listener) {
        this.context = context;
        this.bookId = bookId;
        this.inflater = LayoutInflater.from(context);
        this.cardClickListener = listener;
        this.data = data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    public void setOwnerCanEdit(Boolean ownerCanEdit) {
        this.ownerCanEdit = ownerCanEdit;
    }

    @Override
    public int getItemCount() {
        if (data == null) {
            if (ownerCanEdit) return 1;
            else return 0;
        }
        else {
            if (ownerCanEdit) return data.size() + 1;
            else return data.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && ownerCanEdit) return 1;
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public SmallCardVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView;
        if (i == 1) {
            itemView = inflater.inflate(R.layout.list_item_add_card, viewGroup, false);
            itemView.setTag("add_card");
            return new SmallCardVH(itemView);
        }
        itemView = inflater.inflate(R.layout.list_item_small_card, viewGroup, false);
        itemView.setTag("small_card");
        return new SmallCardVH(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SmallCardVH holder, int i) {
        if (i == 0 && ownerCanEdit) return;
        else i = ownerCanEdit ? i-1 : i;
        Uri parsedUri = Uri.fromFile(new File(context.getFilesDir(), data.get(i)));
        Log.i(TAG, "onBindViewHolder: Path: "+data.get(i));
        Log.i(TAG, "onBindViewHolder: URI: "+parsedUri);
        holder.image.setImageURI(parsedUri);
    }
}
