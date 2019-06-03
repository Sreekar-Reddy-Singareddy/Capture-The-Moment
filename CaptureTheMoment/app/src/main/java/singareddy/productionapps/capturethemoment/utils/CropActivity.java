package singareddy.productionapps.capturethemoment.utils;

import android.os.Bundle;

import com.theartofdev.edmodo.cropper.CropImageActivity;

public class CropActivity extends CropImageActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(AppUtilities.CURRENT_THEME);
        super.onCreate(savedInstanceState);
    }
}
