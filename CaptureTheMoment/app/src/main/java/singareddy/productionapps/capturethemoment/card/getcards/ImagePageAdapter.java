package singareddy.productionapps.capturethemoment.card.getcards;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class ImagePageAdapter extends FragmentPagerAdapter {

    private Context context;
    private List<Uri> imageUris;
    private FragmentManager fragmentManager;

    public ImagePageAdapter(Context context, FragmentManager fragmentManager, List<Uri> imageUris) {
        super(fragmentManager);
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.imageUris = imageUris;
    }

    public void setImageUris(List<Uri> imageUris) {
        this.imageUris = imageUris;
    }

    @Override
    public Fragment getItem(int i) {
        ImageFragment fragment = new ImageFragment();
        fragment.setImageUri(imageUris.get(i));
        return fragment;
    }

    @Override
    public int getCount() {
        if (imageUris == null) {return 0;}
        return imageUris.size();
    }
}
