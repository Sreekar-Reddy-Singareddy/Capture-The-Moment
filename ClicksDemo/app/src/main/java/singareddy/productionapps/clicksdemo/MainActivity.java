package singareddy.productionapps.clicksdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "MainActivity";
    private float previousX = 0;
    private float currentX = 0;
    private ArrayList<Float> touchData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int touchResult = SwipeComputer.computeTouchEvent(event);
        switch (touchResult) {
            case SwipeComputer.SWIPED_LEFT:
                Log.i(TAG, "onTouchEvent: Swiped Left");
                break;
            case SwipeComputer.SWIPED_RIGHT:
                Log.i(TAG, "onTouchEvent: Swiped Right");
                break;
        }
        return super.onTouchEvent(event);
    }
}
