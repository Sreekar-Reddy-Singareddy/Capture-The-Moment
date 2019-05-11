package singareddy.productionapps.capturethemoment.card.getcards;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import singareddy.productionapps.capturethemoment.R;

public class CardImageFragment extends Fragment {

    private Uri imageUri;
    private View fragView;
    private BigCardClickListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        System.out.println("Card Frag cresting...r");
        fragView = inflater.inflate(R.layout.page_item_card_image, container, false);
        ImageView photo = fragView.findViewById(R.id.page_item_image_iv_image);
        photo.setImageURI(imageUri);
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Image clicked");
                listener.bigCardClicked();
            }
        });
        return fragView;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public void setListener(BigCardClickListener listener) {
        this.listener = listener;
    }
}
