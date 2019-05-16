package singareddy.productionapps.capturethemoment.book.get;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import singareddy.productionapps.capturethemoment.R;
import singareddy.productionapps.capturethemoment.card.get.SmallCardsActivity;
import singareddy.productionapps.capturethemoment.models.Book;

public class GetBooksAdapter extends RecyclerView.Adapter<GetBooksAdapter.AllBooksViewHolder> {
    private static String TAG = "GetBooksAdapter";

    public class AllBooksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView lastOpened;
        private TextView bookName;
        private ImageView shareIcon;

        public AllBooksViewHolder (View view) {
            super(view);
            lastOpened = view.findViewById(R.id.book_item_tv_date);
            bookName = view.findViewById(R.id.book_item_tv_bookname);
            shareIcon = view.findViewById(R.id.book_item_iv_share);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.i(TAG, "onClick: Book Selected: "+getAdapterPosition());
            // When a book is selected, take the user to next activity
            // that shows more information about the book.
            Intent insideBookIntent = new Intent(context, SmallCardsActivity.class);
            insideBookIntent.putExtra("OwnBook", bookData.get(getAdapterPosition()).doIOwnTheBook());
            insideBookIntent.putExtra("bookId", bookData.get(getAdapterPosition()).getBookId());
            insideBookIntent.putExtra("bookName", bookData.get(getAdapterPosition()).getName());
            context.startActivity(insideBookIntent);
        }
    }

    private Context context;
    private List<Book> bookData;
    private LayoutInflater inflater;

    public GetBooksAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public GetBooksAdapter(Context context, List<Book> books) {
        this(context);
        bookData = books;
    }

    @Override
    public int getItemCount() {
        if (bookData == null) {
            return 0;
        }
        Log.i(TAG, "getItemCount: Books: "+bookData.size());
        return bookData.size();
    }

    @NonNull
    @Override
    public AllBooksViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Log.i(TAG, "onCreateViewHolder: Item: "+i);
        View view = inflater.inflate(R.layout.list_item_book, viewGroup, false);
        return new AllBooksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllBooksViewHolder holder, int i) {
        Book book = bookData.get(i);
        if (book.getLastUpdatedDate() != null && book.getLastUpdatedDate() != 0) {
            Date date = new Date(book.getLastUpdatedDate());
            SimpleDateFormat format = new SimpleDateFormat();
            format.applyPattern("dd MMM YYYY");
            long diff = new Date().getTime() - book.getLastUpdatedDate().longValue();
            if (diff < 60*1000) {
                holder.lastOpened.setText("now");
            }
            else {
                holder.lastOpened.setText(String.valueOf(diff/(1000*60)) + " minutes ago");
            }
        }
        else {
            holder.lastOpened.setText("NA");
        }
        holder.bookName.setText(book.getName());
        if (book.doIOwnTheBook()) {
            // This is an owned book
            holder.shareIcon.setVisibility(View.INVISIBLE);
        }
        else {
            // This is a shared book
            holder.shareIcon.setVisibility(View.VISIBLE);
        }
    }

    public void setBookData(List<Book> bookData) {
        this.bookData = bookData;
    }
}
