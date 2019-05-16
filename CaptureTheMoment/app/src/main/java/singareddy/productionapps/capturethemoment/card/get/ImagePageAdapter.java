package singareddy.productionapps.capturethemoment.card.get;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import singareddy.productionapps.capturethemoment.card.add.AddImageFragment;

public class ImagePageAdapter extends FragmentStatePagerAdapter {
    private static String TAG = "ImagePageAdapter";

    private BigCardClickListener bigCardListener;
    private Context context;
    private List<Uri> imageUris;
    private FragmentManager fragmentManager;
    private int framedImage = 0;

    public ImagePageAdapter(Context context, FragmentManager fragmentManager, List<Uri> imageUris) {
        super(fragmentManager);
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.imageUris = imageUris;
    }

    public ImagePageAdapter(Context context, FragmentManager fragmentManager, List<Uri> imageUris, BigCardClickListener listener) {
        this(context, fragmentManager, imageUris);
        this.bigCardListener = listener;
    }

    public void setImageUris(List<Uri> imageUris) {
        this.imageUris = imageUris;
    }

    @Override
    public Fragment getItem(int position) {
        if (framedImage == 0) {
            AddImageFragment fragment = new AddImageFragment();
            fragment.setImageUri(imageUris.get(position));
            return fragment;
        }
        else {
            CardImageFragment fragment = new CardImageFragment();
            fragment.setImageUri(imageUris.get(position));
            fragment.setListener(bigCardListener);
            return fragment;
        }
    }

    @Override
    public int getCount() {
        if (imageUris == null) {return 0;}
        return imageUris.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    public void setFramedImage(int framedImage) {
        this.framedImage = framedImage;
    }

}
