package singareddy.productionapps.capturethemoment.custom_views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.StyleableRes;
import android.util.AttributeSet;
import android.util.Log;

import singareddy.productionapps.capturethemoment.R;

public class FramedImageView extends android.support.v7.widget.AppCompatImageView {
    private static String TAG = "CustomViewOne";

    private TypedArray attrArray;
    private int DEF_BORDER_COLOR;
    private int DEF_DOTS_COLOR;
    @StyleableRes private int borderColor = R.styleable.FramedImageView_borderColor;
    @StyleableRes private int dottedColor = R.styleable.FramedImageView_dotsColor;

    public FramedImageView(Context context) {
        super(context);
        Log.i(TAG, "CustomViewOne: *");
    }

    public FramedImageView(Context con, AttributeSet attrs) {
        super(con, attrs);
        Log.i(TAG, "CustomViewOne: **");
        DEF_BORDER_COLOR = getResources().getColor(R.color.colorPrimaryDark);
        DEF_DOTS_COLOR = getResources().getColor(android.R.color.white);
        attrArray = con.obtainStyledAttributes(attrs, R.styleable.FramedImageView);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        Log.i(TAG, "onDraw: Width : "+width);
        Log.i(TAG, "onDraw: Height: "+height);
        int borderWidth = computeBorderWidth(width);
        int borderHeight = computeBorderHeight(height);
        Paint paint = new Paint();

        // Draw the border layer
        paint.setColor(attrArray.getColor(R.styleable.FramedImageView_borderColor, DEF_BORDER_COLOR));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(borderHeight);
        canvas.drawRect(0,0,width,height,paint);
        canvas.save();

        // Draw the inner border layer
        paint.setColor(attrArray.getColor(R.styleable.FramedImageView_dotsColor, DEF_DOTS_COLOR));
        paint.setStrokeWidth(borderHeight/6);
        canvas.drawRect(borderHeight/2+borderHeight/12, borderHeight/2+borderHeight/12, width-borderHeight/2-borderHeight/12, height-borderHeight/2-borderHeight/12, paint);
        canvas.save();

//        // Draw the dotted layer
//        paint.setColor(attrArray.getColor(dottedColor, DEF_DOTS_COLOR));
//        paint.setStrokeWidth(borderHeight*0.09f);
//        paint.setPathEffect(new DashPathEffect(new float[] {borderHeight*0.2f, borderHeight*0.2f}, 0));
//        canvas.drawRect(borderHeight/4, borderHeight/4, width-borderHeight/4, height-borderHeight/4, paint);
//        canvas.save();
    }

    private int computeBorderWidth(int width) {
        return Double.valueOf(width*0.08).intValue();
    }

    private int computeBorderHeight(int height) {
        return Double.valueOf(height*0.06).intValue();
    }
}
