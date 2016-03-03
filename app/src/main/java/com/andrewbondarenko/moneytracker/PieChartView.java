package com.andrewbondarenko.moneytracker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

public class PieChartView extends View {

    private Paint paint;
    private RectF rectangle;
    private float[] dataPoints;

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (dataPoints != null) {
            Random random = new Random();
            int startTop = 0, startLeft = 0,
                    endBottom = getWidth(), endRight = endBottom;

            rectangle = new RectF(startLeft, startTop, endRight, endBottom);
            float[] scale = scale();

            float startPoint = 0;
            for (float value : scale) {
                int color = Color.argb(100, random.nextInt(255),random.nextInt(255),random.nextInt(255));
                paint.setColor(color);

                canvas.drawArc(rectangle, startPoint, value, true, paint);
                startPoint += value;
            }

        }
    }

    public void setDataPoints(float[] dataPoints) {
        this.dataPoints = dataPoints;
        invalidate();
    }

    private float[] scale() {
        int sum = sum();
        float[] scale = new float[dataPoints.length];

        for (int i = 0; i < scale.length; i++) {
            scale[i] = (dataPoints[i]/sum) * 360;
        }

        return scale;
    }

    private int sum() {
        int sum = 0;

        for (float dataPoint : dataPoints) {
            sum += dataPoint;
        }

        return sum;
    }

}
