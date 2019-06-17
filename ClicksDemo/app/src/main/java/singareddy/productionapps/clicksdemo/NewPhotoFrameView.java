package singareddy.productionapps.clicksdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.support.annotation.StyleableRes;
import android.util.AttributeSet;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class NewPhotoFrameView extends android.support.v7.widget.AppCompatImageView {
    private static String TAG = "CustomViewOne";
    private int DEFAULT_FRAME_COLOR;
    private int DEFUALT_LINES_COLOR;

    private TypedArray attrArray;
    private Bitmap image;
    private Context context;
    @StyleableRes private int frameColor = R.styleable.NewPhotoFrameView_frameColor;
    @StyleableRes private int linesColor = R.styleable.NewPhotoFrameView_linesColor;

    public NewPhotoFrameView(Context context) {
        super(context);
        this.context = context;
        Log.i(TAG, "CustomViewOne: *");
    }

    public NewPhotoFrameView(Context con, AttributeSet attrs) {
        super(con, attrs);
        Log.i(TAG, "CustomViewOne: **");
        DEFAULT_FRAME_COLOR = getResources().getColor(android.R.color.holo_red_light);
        DEFUALT_LINES_COLOR = getResources().getColor(android.R.color.holo_red_dark);
        attrArray = con.obtainStyledAttributes(attrs, R.styleable.NewPhotoFrameView);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        Log.i(TAG, "onDraw: Width : "+width);
        Log.i(TAG, "onDraw: Height: "+height);

        // Compute all the parameteres needed
        int strokeColor = attrArray.getColor(frameColor, DEFAULT_FRAME_COLOR);
        int strokeWidth = Double.valueOf(width*0.05).intValue();
        int frameMargin = 16;
        int frameWidth = width - frameMargin;
        int frameHeight = frameWidth;
        int wStart = frameMargin/2;
        int wEnd = frameMargin/2 + frameWidth;
        int hStart = frameMargin/2;
        int hEnd = frameMargin/2 + frameHeight;
        int bottomSpace = frameWidth/4;
        int horizontalLineStrokeWidth = strokeWidth/5;

        // Draw the BITMAP on canvas
        Rect rect = new Rect();
        rect.left=wStart;
        rect.top=hStart;
        rect.right=wEnd;
        rect.bottom=hEnd;
        if (image != null) {
            canvas.drawBitmap(image,null,rect,null);
            canvas.save();
        }

        // Configure the painter for main frame
        Paint painter = new Paint();
        painter.setStrokeWidth(strokeWidth);
        painter.setColor(strokeColor);
        painter.setStyle(Paint.Style.STROKE);

        // Draw PART 1 on canvas
        RectF frameRect = new RectF();
        frameRect.left=wStart;
        frameRect.top=hStart;
        frameRect.right=wEnd;
//        frameRect.bottom=hEnd+bottomSpace;
        frameRect.bottom=hEnd;
        canvas.drawRect(frameRect, painter);
        canvas.save();

//        // Configure the painter bottom space
//        painter.setStyle(Paint.Style.FILL_AND_STROKE);

//        // Draw the PART 2 on canvas
//        canvas.drawRect(wStart, hEnd, wEnd, hEnd+bottomSpace, painter);
//        canvas.save();

//        // Configure the painter for horizontal lines
//        painter.setStyle(Paint.Style.STROKE);
//        painter.setStrokeWidth(horizontalLineStrokeWidth);
//        painter.setColor(attrArray.getColor(linesColor, DEFAULT_FRAME_COLOR));

//        // Draw the horizontal lines on the frame
//        int currentLinePosition = hStart + 5;
//        int lastLinePosition = hEnd + bottomSpace;
//        Log.i(TAG, "onDraw: Last Line Position: "+lastLinePosition);
//        while (currentLinePosition <= lastLinePosition) {
//            if (currentLinePosition < hStart+strokeWidth/2 || currentLinePosition > hEnd-strokeWidth/2) {
//                canvas.drawLine(wStart-strokeWidth/2, currentLinePosition, wEnd+strokeWidth/2, currentLinePosition, painter);
//            }
//            canvas.drawLine(wStart-strokeWidth/2, currentLinePosition, wStart+strokeWidth/2, currentLinePosition, painter);
//            canvas.drawLine(wEnd-strokeWidth/2, currentLinePosition, wEnd+strokeWidth/2, currentLinePosition, painter);
//            currentLinePosition += horizontalLineStrokeWidth*2;
//            Log.i(TAG, "onDraw: Current Line Position: "+currentLinePosition);
//        }
//        if (currentLinePosition - lastLinePosition < horizontalLineStrokeWidth*2) {
//            canvas.drawLine(wStart-strokeWidth/2, currentLinePosition, wEnd+strokeWidth/2, currentLinePosition, painter);
//        }
//        canvas.save();

    }

    public void setFramedImageUri(Uri imageUri) {
        try {
            InputStream imageRawData = context.getContentResolver().openInputStream(imageUri);
            image = BitmapFactory.decodeStream(imageRawData);
            invalidate();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
