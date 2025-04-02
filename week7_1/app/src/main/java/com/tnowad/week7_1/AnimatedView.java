package com.tnowad.week7_1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import androidx.annotation.NonNull;

public class AnimatedView extends View {
    private Paint paint;
    private int x = 0;
    private int dx = 10;

    public  AnimatedView(Context context) {
        super(context);
        paint = new Paint();
        paint.setColor(Color.RED);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(x, 300, 50, paint);
        x += dx;
        if(x > getWidth() || x < 0) {
            dx = -dx;
        }
        postInvalidateDelayed(30);
    }
}
