package singareddy.productionapps.customviews;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ActivityTwo extends AppCompatActivity {
    private static String TAG = "ActivityTwo";

    private Bitmap finalImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second);
        CustomViewOne c = findViewById(R.id.cust_view);
        c.setImageResource(R.drawable.vikas);
    }
}
