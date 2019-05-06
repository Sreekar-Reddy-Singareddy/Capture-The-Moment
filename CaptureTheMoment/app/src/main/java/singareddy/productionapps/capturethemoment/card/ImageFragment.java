package singareddy.productionapps.capturethemoment.card;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import singareddy.productionapps.capturethemoment.R;

public class ImageFragment extends Fragment {

    private Uri imageUri;
    private View fragView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragView = inflater.inflate(R.layout.page_item_image, container, false);
        ImageView photo = fragView.findViewById(R.id.page_item_image_iv_image);
        photo.setImageURI(imageUri);
        return fragView;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }
}
