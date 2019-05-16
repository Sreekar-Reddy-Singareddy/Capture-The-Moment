package singareddy.productionapps.capturethemoment.card.get;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import singareddy.productionapps.capturethemoment.R;

public class IndicatorAdapter extends RecyclerView.Adapter<IndicatorAdapter.IndicatorVH> {

    private int pages = 0;
    private int selectedPage = 0;
    private LayoutInflater inflater;
    private Context context;

    public IndicatorAdapter( Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return pages;
    }

    @NonNull
    @Override
    public IndicatorVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = inflater.inflate(R.layout.list_item_page_indicator, viewGroup, false);
        return new IndicatorVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull IndicatorVH indicatorVH, int i) {
        if (i == selectedPage) {
            indicatorVH.card.setCardBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_dark));
            indicatorVH.card.setScaleX(1.5f);
            indicatorVH.card.setScaleY(1.5f);
        }
        else {
            indicatorVH.card.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
            indicatorVH.card.setScaleX(1f);
            indicatorVH.card.setScaleY(1f);
        }
    }



    public void setPages(int pages) {
        this.pages = pages;
    }

    public void setSelectedPage(int selectedPage) {
        this.selectedPage = selectedPage;
    }

    public class IndicatorVH extends RecyclerView.ViewHolder {
        CardView card;
        public IndicatorVH(View v){
            super(v);
            card = v.findViewById(R.id.indicator_card);
        }
    }
}
