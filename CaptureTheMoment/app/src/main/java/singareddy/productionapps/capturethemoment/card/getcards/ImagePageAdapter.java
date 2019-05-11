package singareddy.productionapps.capturethemoment.card.getcards;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class ImagePageAdapter extends FragmentPagerAdapter {
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
        Log.i(TAG, "getCount: URIs: "+imageUris.size());
        if (imageUris == null) {return 0;}
        return imageUris.size();
    }

    public void setFramedImage(int framedImage) {
        this.framedImage = framedImage;
    }
}
