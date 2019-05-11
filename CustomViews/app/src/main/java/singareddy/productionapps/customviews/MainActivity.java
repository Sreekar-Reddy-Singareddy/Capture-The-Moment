package singareddy.productionapps.customviews;

import android.graphics.Point;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private ImageView im;
    private CustomViewOne cvo;
    private ConstraintLayout cl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cl = findViewById(R.id.container_layout);

        // Get the screen dimensions
        int parentWidth = getWindowManager().getDefaultDisplay().getWidth();
        int parentHeight = getWindowManager().getDefaultDisplay().getHeight();
        System.out.println("Parent W: "+parentWidth);
        System.out.println("Parent H: "+parentHeight);

        // Create a view
        im = new ImageView(this);
        im.setImageDrawable(getResources().getDrawable(R.drawable.sreekar));
        cvo = new CustomViewOne(this);
        ConstraintLayout.LayoutParams imParams = new ConstraintLayout.LayoutParams(parentWidth-200, parentWidth-200);
        imParams.startToStart = R.id.container_layout;
        imParams.endToEnd = R.id.container_layout;
        imParams.topToTop = R.id.container_layout;
        imParams.bottomToBottom = R.id.container_layout;
        cvo.setLayoutParams(imParams);

        // Add the view to constrain layout
        cl.addView(cvo, imParams);
    }
}
