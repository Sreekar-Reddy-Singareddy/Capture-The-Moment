package singareddy.productionapps.capturethemoment.book.get;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import singareddy.productionapps.capturethemoment.R;
import singareddy.productionapps.capturethemoment.card.get.SmallCardsActivity;
import singareddy.productionapps.capturethemoment.models.Book;
import singareddy.productionapps.capturethemoment.utils.AppUtilities;

import static singareddy.productionapps.capturethemoment.card.get.SmallCardsActivity.*;

public class GetBooksAdapter extends RecyclerView.Adapter<GetBooksAdapter.AllBooksViewHolder> {
    private static String TAG = "GetBooksAdapter";

    public class AllBooksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView lastOpened;
        private TextView bookName;
        private TextView ownerName;
        private ImageView shareIcon;
        private ImageView coverPhoto;

        public AllBooksViewHolder (View view) {
            super(view);
            lastOpened = view.findViewById(R.id.book_item_tv_date);
            bookName = view.findViewById(R.id.book_item_tv_bookname);
            ownerName = view.findViewById(R.id.book_item_tv_owner_name);
            shareIcon = view.findViewById(R.id.book_item_iv_share);
            coverPhoto = view.findViewById(R.id.book_item_iv_coverphoto);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // When a book is selected, take the user to next activity
            // that shows more information about the book.
            Intent insideBookIntent = new Intent(context, SmallCardsActivity.class);
            insideBookIntent.putExtra(IS_THIS_OWN_BOOK, bookData.get(getAdapterPosition()).doIOwnTheBook());
            insideBookIntent.putExtra(BOOK_ID, bookData.get(getAdapterPosition()).getBookId());
            insideBookIntent.putExtra(BOOK_NAME, bookData.get(getAdapterPosition()).getName());
            context.startActivity(insideBookIntent);
        }
    }

    private Context context;
    private List<Book> bookData;
    private LayoutInflater inflater;
    private GetBooksViewModel getBooksViewModel;

    public GetBooksAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public GetBooksAdapter(Context context, List<Book> books, GetBooksViewModel getBooksViewModel) {
        this(context);
        bookData = books;
        this.getBooksViewModel = getBooksViewModel;
    }

    @Override
    public int getItemCount() {
        if (bookData == null) {
            return 0;
        }
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
            long diff = new Date().getTime() - book.getLastUpdatedDate().longValue();
            if (diff < 60000) {
                holder.lastOpened.setText("Now");
            }
            else if (diff >= 60000 && diff < 1.2e5) {
                holder.lastOpened.setText("1 minute ago");
            }
            else if (diff >= 1.2e5 && diff < 3.6e6) {
                Long minutes = diff/60000;
                holder.lastOpened.setText(minutes.toString()+" minutes ago");
            }
            else if (diff >= 3.6e6 && diff < 7.2e6) {
                holder.lastOpened.setText("1 hour ago");
            }
            else if (diff >= 7.2e6 && diff < 8.64e7) {
                Long hours = diff/3600000;
                holder.lastOpened.setText(hours.toString()+" hours ago");
            }
            else if (diff >= 8.64e7 && diff < 1.728e8) {
                holder.lastOpened.setText("Yesterday");
            }
            else if (diff >= 1.728e8) {
                String formattedDate = format.format(new Date(book.getLastUpdatedDate()));
                holder.lastOpened.setText(formattedDate);
            }
        }
        else {
            holder.lastOpened.setText(AppUtilities.Defaults.DEFAULT_STRING);
        }
        holder.bookName.setText(book.getName());
        if (book.doIOwnTheBook()) {
            // This is an owned book
            holder.shareIcon.setVisibility(View.INVISIBLE);
            holder.ownerName.setVisibility(View.INVISIBLE);
        }
        else {
            // This is a shared book
            holder.shareIcon.setVisibility(View.VISIBLE);
            holder.ownerName.setVisibility(View.VISIBLE);
            getBooksViewModel.getOwnerNameForBook(book.getOwner());
            FirebaseDatabase.getInstance().getReference()
                    .child("usernames")
                    .child(book.getOwner())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot != null) {
                                String ownerName = dataSnapshot.getValue(String.class);
                                holder.ownerName.setText(ownerName);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }

        // If the view model is not null, then use
        // it to get the cover photo
        if (book.getCards() != null && book.getCards().size() != 0) {
            String cardId = book.getCards().get(0);
            String coverPhotoPath = getBooksViewModel.getOneImagePathForCard(cardId);
            Log.i(TAG, "onBindViewHolder: Cover Photo: "+coverPhotoPath);
        }
    }

    public void setBookData(List<Book> bookData) {
        this.bookData = bookData;
    }
}
