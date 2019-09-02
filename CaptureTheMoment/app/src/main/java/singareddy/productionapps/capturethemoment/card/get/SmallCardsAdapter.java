package singareddy.productionapps.capturethemoment.card.get;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import singareddy.productionapps.capturethemoment.R;
import singareddy.productionapps.capturethemoment.card.add.AddCardActivity;

public class SmallCardsAdapter extends RecyclerView.Adapter<SmallCardsAdapter.SmallCardVH> {
    private static String TAG = "SmallCardsAdapter";

    private static String PURPOSE_ADD_CARD = "add_card";
    private static String PURPOSE_SHOW_CARD = "small_card";
    private static final int ITEM_TYPE_ADD_CARD = 1;
    private static final int ITEM_TYPE_SHOW_CARD = 0;

    public class SmallCardVH extends RecyclerView.ViewHolder {
        ImageView image;

        public SmallCardVH(View view) {
            super(view);
            image = view.findViewById(R.id.list_item_small_card_iv_image);
            String itemType = (String) view.getTag();
            if (itemType.equals(PURPOSE_ADD_CARD)) {
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
            else if (itemType.equals(PURPOSE_SHOW_CARD)) {
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
    private int layoutMargin = 8;
    private float imageRadius = 25;
    private int cardSize;

    public SmallCardsAdapter(Context context, List<String> data, String bookId,
                             SmallCardClickListener listener) {
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
        Log.i(TAG, "getItemViewType: OWNER CAN EDIT: "+ownerCanEdit);
        if (position == 0 && ownerCanEdit) return ITEM_TYPE_ADD_CARD;
        else return ITEM_TYPE_SHOW_CARD;
    }

    @NonNull
    @Override
    public SmallCardVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int itemType) {
        View itemView;
        if (itemType == ITEM_TYPE_ADD_CARD) {
            itemView = createTailoredView(viewGroup);
            itemView.setTag(PURPOSE_ADD_CARD);
            return new SmallCardVH(itemView);
        }
        itemView = createTailoredView(viewGroup);
        itemView.setTag(PURPOSE_SHOW_CARD);
        return new SmallCardVH(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SmallCardVH holder, int position) {
        if (position == 0 && ownerCanEdit) {
            holder.image.setImageDrawable(context.getDrawable(R.drawable.add_card));
//            holder.image.setBackground(context.getDrawable(R.drawable.add_card_outline).);
            return;
        }
        else position = ownerCanEdit ? position-1 : position;
        BitmapFactory.Options imageOptions = new BitmapFactory.Options();
        imageOptions.inSampleSize = 2;
        Bitmap image = BitmapFactory.decodeFile(context.getFilesDir()+"/"+data.get(position), imageOptions);
        Log.i(TAG, "onBindViewHolder: Bitmap Contents: "+image);
        holder.image.setImageBitmap(image);
    }

    private View createTailoredView (ViewGroup parent) {
        View itemView = inflater.inflate(R.layout.list_item_small_card,parent, false);
        ConstraintLayout constraintLayout = itemView.findViewById(R.id.list_item_small_card_cl_container);
        ViewGroup.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        constraintLayout.setLayoutParams(params);
        // Create an image view now
        RoundedImageView rImageView = itemView.findViewById(R.id.list_item_small_card_iv_image);
        rImageView.setCornerRadius(cardSize/4);
        ConstraintLayout.LayoutParams imageParams = new ConstraintLayout.LayoutParams(cardSize, cardSize);
        imageParams.topToTop = R.id.list_item_small_card_cl_container;
        imageParams.startToStart = R.id.list_item_small_card_cl_container;
        imageParams.endToEnd = R.id.list_item_small_card_cl_container;
        imageParams.bottomToBottom = R.id.list_item_small_card_cl_container;
        imageParams.topMargin = layoutMargin;
        imageParams.bottomMargin = layoutMargin;
        imageParams.leftMargin = layoutMargin;
        imageParams.rightMargin = layoutMargin;
        rImageView.setLayoutParams(imageParams);
        return itemView;
    }

    public void setCardSize(int cardSize) {
        this.cardSize = cardSize;
    }
}
