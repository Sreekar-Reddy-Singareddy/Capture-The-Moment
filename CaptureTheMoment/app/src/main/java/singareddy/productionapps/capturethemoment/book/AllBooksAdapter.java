package singareddy.productionapps.capturethemoment.book;

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

import singareddy.productionapps.capturethemoment.AppUtilities;
import singareddy.productionapps.capturethemoment.R;
import singareddy.productionapps.capturethemoment.models.Book;

public class AllBooksAdapter extends RecyclerView.Adapter<AllBooksAdapter.AllBooksViewHolder> {
    private static String TAG = "AllBooksAdapter";

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
            Intent insideBookIntent = new Intent(context, InsideBookActivity.class);
            insideBookIntent.putExtra("OwnBook", bookData.get(getAdapterPosition()).doIOwnTheBook());
            insideBookIntent.putExtra("bookId", bookData.get(getAdapterPosition()).getBookId());
            context.startActivity(insideBookIntent);
        }
    }

    private Context context;
    private List<Book> bookData;
    private LayoutInflater inflater;

    public AllBooksAdapter (Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
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
            String formattedTime = format.format(date);
            holder.lastOpened.setText(formattedTime);
        }
        else {
            holder.lastOpened.setText("NA");
        }
        holder.bookName.setText(book.getName());
        if (AppUtilities.User.CURRENT_USER.getUid().equals(book.getOwner())) {
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
