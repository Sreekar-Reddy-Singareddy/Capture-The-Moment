package singareddy.productionapps.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.widget.ImageView;

public class PhotoImageView extends android.support.v7.widget.AppCompatImageView {

    private Context con;

    public PhotoImageView (Context context, AttributeSet attrs) {
        super(context, attrs);
        con = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        System.out.println("Height: "+h);
        System.out.println("Width : "+w);
//        int sw = 1080;
//        int sh = 1776;
//        float left = (sw-w)/2-30, top = (sh-h)/2-30, right = (sw-w)/2+w+30, bottom = (sh-h)/2+h+30;
//        setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
//        mar(40, 40, 40, 40);
        Paint paint = new Paint();
        paint.setStrokeWidth(40);
        paint.setColor(getResources().getColor(android.R.color.background_dark));
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(0, 0, w, h, paint);
        canvas.save();

        paint.setStrokeWidth(5);
        paint.setColor(getResources().getColor(android.R.color.white));
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(new DashPathEffect(new float[] {10, 10}, 0));
        canvas.drawRect(10, 10, w-10, h-10, paint);
        canvas.save();
    }
}
