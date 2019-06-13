package singareddy.productionapps.clicksdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.CheckBox;

public class CustomCheckBox extends android.support.v7.widget.AppCompatCheckBox {

    public CustomCheckBox (Context context) {
        super(context);
    }

    public CustomCheckBox (Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        Paint painter = new Paint();
        painter.setStyle(Paint.Style.STROKE);
        painter.setStrokeWidth(width/15);
        painter.setColor(getResources().getColor(android.R.color.holo_red_light));

        canvas.drawCircle(width/2,height/2, width/2-width/15, painter);

        Bitmap tick = BitmapFactory.decodeResource(getResources(), R.drawable.ic_done_black_24dp);
        canvas.drawBitmap(tick,width/2,width/2, null);
    }
}
