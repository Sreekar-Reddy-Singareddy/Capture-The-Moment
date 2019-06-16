package singareddy.productionapps.capturethemoment.utils;

import android.view.MotionEvent;

import java.util.ArrayList;

public class SwipeComputer {
    public static final int INVALID_SWIPE = 0;
    public static final int SWIPED_LEFT = -1;
    public static final int SWIPED_RIGHT = 1;
    private static ArrayList<Float> touchData = null;

    public static int computeTouchEvent(MotionEvent event){
        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (touchData == null) touchData = new ArrayList<>();
                else touchData.clear();
                break;
            case MotionEvent.ACTION_MOVE:
                touchData.add(event.getAxisValue(MotionEvent.AXIS_X));
                break;
            case MotionEvent.ACTION_UP:
                if (touchData == null || touchData.size() < 2) return INVALID_SWIPE;
                float axisDiff = touchData.get(0) - touchData.get(1);
                int direction;
                if (axisDiff > 0) {
                    direction = SWIPED_LEFT;
                }
                else {
                    direction = SWIPED_RIGHT;
                }
                for (int i=1; i<touchData.size(); i++) {
                    float diff = touchData.get(i-1) - touchData.get(i);
                    if (!((direction == SWIPED_LEFT && diff > 0) || (direction == SWIPED_RIGHT && diff < 0))) {
                        return INVALID_SWIPE;
                    }
                }
                if (direction == -1) return SWIPED_LEFT;
                else if (direction == 1) return SWIPED_RIGHT;
                break;
        }
        return INVALID_SWIPE;
    }

}
