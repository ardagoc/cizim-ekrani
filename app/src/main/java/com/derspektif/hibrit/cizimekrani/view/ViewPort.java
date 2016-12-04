package com.derspektif.hibrit.cizimekrani.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.derspektif.hibrit.cizimekrani.helper.gesturedetectors.MoveGestureDetector;
import com.derspektif.hibrit.cizimekrani.helper.gesturedetectors.RotateGestureDetector;


/**
 * Created by ardagoc on 3/12/2016.
 */
public class ViewPort extends ImageView {
    Layer layer;
    boolean onGestureMode = true;

    //    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    Context context;
    private Paint mPaint;
    private float mX, mY;
    private static final float TOLERANCE = 5;

    public ViewPort(Context context) {
        super(context);
        this.setWillNotDraw(false);
    }

    public ViewPort(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setWillNotDraw(false);

        this.context = context;
        // we set a new Path
        mPath = new Path();

        // and we set a new Paint with the desired attributes
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(4f);
    }

    public void setBitmap(Context context, Bitmap bitmap) {
        layer = new Layer(context, this, bitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (layer != null)
            layer.draw(canvas);
        if (!onGestureMode)
            canvas.drawPath(mPath, mPaint);
    }

    // when ACTION_DOWN start touch according to the x,y values
    private void startTouch(float x, float y) {
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    // when ACTION_MOVE move touch according to the x,y values
    private void moveTouch(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOLERANCE || dy >= TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    // when ACTION_UP stop touch
    private void upTouch() {
        mPath.lineTo(mX, mY);
    }

    public void clearCanvas() {
        mPath.reset();
        invalidate();
    }

    public void setOnGestureMode(boolean value){
        onGestureMode = value;
    }

    private Layer target;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (onGestureMode) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                target = null;
                if (layer != null && layer.contains(event)) {
                    target = layer;
                    invalidate();
                }
            }
            if (target == null)
                return false;
            return target.onTouchEvent(event);
        } else {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startTouch(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    moveTouch(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    upTouch();
                    invalidate();
                    break;
            }
            return true;
        }
    }

    class Layer {
        Matrix matrix = new Matrix();
        Matrix inverse = new Matrix();
        RectF bounds;
        View parent;
        Bitmap bitmap;
        MoveGestureDetector mgd;
        ScaleGestureDetector sgd;
        RotateGestureDetector rgd;

        boolean drawMode = false;

        public Layer(Context ctx, View p, Bitmap b) {
            parent = p;
            bitmap = b;
            bounds = new RectF(0, 0, b.getWidth(), b.getHeight());
            mgd = new MoveGestureDetector(ctx, mgl);
            sgd = new ScaleGestureDetector(ctx, sgl);
//        rgd = new RotateGestureDetector(ctx, rgl);
            matrix.postTranslate(50 + (float) Math.random() * 50, 50 + (float) Math.random() * 50);
        }

        public boolean contains(MotionEvent event) {
            matrix.invert(inverse);
            float[] pts = {event.getX(), event.getY()};
            inverse.mapPoints(pts);
            if (!bounds.contains(pts[0], pts[1])) {
                return false;
            }
            return Color.alpha(bitmap.getPixel((int) pts[0], (int) pts[1])) != 0;
        }

        public boolean onTouchEvent(MotionEvent event) {
            mgd.onTouchEvent(event);
            sgd.onTouchEvent(event);
//        rgd.onTouchEvent(event);
            return true;
        }

        public void draw(Canvas canvas) {
            canvas.drawBitmap(bitmap, matrix, null);
        }

        MoveGestureDetector.SimpleOnMoveGestureListener mgl = new MoveGestureDetector.SimpleOnMoveGestureListener() {
            @Override
            public boolean onMove(MoveGestureDetector detector) {
                PointF delta = detector.getFocusDelta();
                matrix.postTranslate(delta.x, delta.y);
                parent.invalidate();
                return true;
            }
        };

        ScaleGestureDetector.SimpleOnScaleGestureListener sgl = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float scale = detector.getScaleFactor();
                matrix.postScale(scale, scale, detector.getFocusX(), detector.getFocusY());
                parent.invalidate();
                return true;
            }
        };

/*    RotateGestureDetector.SimpleOnRotateGestureListener rgl = new RotateGestureDetector.SimpleOnRotateGestureListener() {
        @Override
        public boolean onRotate(RotateGestureDetector detector) {
            matrix.postRotate(-detector.getRotationDegreesDelta(), detector.getFocusX(), detector.getFocusY());
            parent.invalidate();
            return true;
        };
    };*/
    }
}
