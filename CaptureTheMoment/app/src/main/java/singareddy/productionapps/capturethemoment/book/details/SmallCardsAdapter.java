package singareddy.productionapps.capturethemoment.book.details;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import singareddy.productionapps.capturethemoment.R;

public class SmallCardsAdapter extends RecyclerView.Adapter<SmallCardsAdapter.SmallCardVH> {

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
                        System.out.println("Add New Card...");
                    }
                });
            }
            else if (itemType.equals("small_card")) {
                image = view.findViewById(R.id.list_item_small_card_iv_image);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println("Showing full card for: "+(getAdapterPosition()-1));
                        Intent fullCardIntent = new Intent(context, FullCardActivity.class);
                        context.startActivity(fullCardIntent);
                    }
                });
            }
        }
    }

    private Context context;
    private LayoutInflater inflater;
    private List data;

    public SmallCardsAdapter(Context context, List data) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.data = new ArrayList();
        this.data.add(new Object()); this.data.add(new Object());
        this.data.add(new Object()); this.data.add(new Object());
        this.data.add(new Object()); this.data.add(new Object());
        this.data.add(new Object()); this.data.add(new Object());
        this.data.add(new Object()); this.data.add(new Object());
        this.data.add(new Object()); this.data.add(new Object());
        this.data.add(new Object()); this.data.add(new Object());
        this.data.add(new Object()); this.data.add(new Object());
        this.data.add(new Object()); this.data.add(new Object());
        this.data.add(new Object()); this.data.add(new Object());
        this.data.add(new Object()); this.data.add(new Object());
        this.data.add(new Object()); this.data.add(new Object());
    }

    @Override
    public int getItemCount() {
        return data.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {return 1;}
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
        if (i == 0) {
            return;
        }
    }
}
