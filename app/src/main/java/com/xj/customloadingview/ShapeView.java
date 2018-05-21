package com.xj.customloadingview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class ShapeView extends View {

    private Paint mPaint;
    private Path mPath;
    private Shape mCurrentShape = Shape.CIRCLE;

    public ShapeView(Context context) {
        this(context, null);
    }

    public ShapeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShapeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (mCurrentShape) {
            case CIRCLE:
                mPaint.setColor(Color.RED);
                canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, mPaint);
                break;
            case RECTANGLE:
                mPaint.setColor(Color.GREEN);
                canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
                break;

            case TRIANGLE:
                mPaint.setColor(Color.BLUE);
                if (mPath == null) {
                    mPath = new Path();
                    mPath.moveTo(getWidth()/2,0);
                    mPath.lineTo(0, (float) (getWidth()/2*Math.sqrt(3)));
                    mPath.lineTo(getWidth(),(float) (getWidth()/2*Math.sqrt(3)));
                }
                canvas.drawPath(mPath,mPaint);
                break;
            default:
                break;
        }
    }

    public void exchange() {
        switch (mCurrentShape) {
            case CIRCLE:
                mCurrentShape = Shape.RECTANGLE;
                break;
            case RECTANGLE:
                mCurrentShape = Shape.TRIANGLE;
                break;
            case TRIANGLE:
                mCurrentShape = Shape.CIRCLE;
                break;
            default:
                break;
        }
        invalidate();
    }

    enum Shape {
        CIRCLE, RECTANGLE, TRIANGLE
    }

    public Shape getCurrentShape() {
        return mCurrentShape;
    }
}
