package com.skripsi.klinik;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.widget.LinearLayout;
 
/**
 * Created by ravi tamada on 29/07/17.
 * Ticket view creates view with view punches making circular holes
 * in the view
 */
 
public class TicketView extends LinearLayout {
    private Bitmap bm;
    private Canvas cv;
    private Paint eraser;
    private int holesBottomMargin = 70;
    private int holeRadius = 20;
 
    public TicketView(Context context) {
        super(context);
        Init();
    }
 
    public TicketView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init();
    }
 
    public TicketView(Context context, AttributeSet attrs,
                      int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init();
    }
 
    private void Init() {
        eraser = new Paint();
        eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        eraser.setAntiAlias(true);
    }
 
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w != oldw || h != oldh) {
            bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            cv = new Canvas(bm);
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }
 
    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth();
        int h = getHeight();
 
        bm.eraseColor(Color.TRANSPARENT);
 
        // set the view background color
        cv.drawColor(Color.WHITE);
 

        // adding punching holes on the ticket by erasing them
        cv.drawCircle(0, 0, holeRadius, eraser); // top-left hole
        cv.drawCircle(w, 0, holeRadius, eraser); // top-right
        cv.drawCircle(0, h , holeRadius, eraser); // bottom-left hole
        cv.drawCircle(w, h, holeRadius, eraser); // bottom right hole
 
        // drawing the image
        canvas.drawBitmap(bm, 0, 0, null);

 
        super.onDraw(canvas);
    }
 
    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
}