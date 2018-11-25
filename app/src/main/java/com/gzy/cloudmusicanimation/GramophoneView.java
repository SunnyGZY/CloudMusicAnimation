package com.gzy.cloudmusicanimation;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 唱片机 View
 *
 * @author gaozongyang
 * @date 2018/11/23
 */
public class GramophoneView extends View {

    private Drawable mDrawable;
    private Paint mCdCoverPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mCdOutPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mOrbitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private BitmapShader mBitmapShader;
    private Bitmap mBitmap;
    private Matrix mMatrix;
    private List<Ring> mRingList = new ArrayList<>();
    private List<Ring> mRemoveRingList = new ArrayList<>();
    private float mRotateDegrees = 0;
    private int ringPadding = DisplayUtil.dp2px(getContext(), 20);
    private int cdCoverR;

    public GramophoneView(Context context) {
        super(context);
    }

    public GramophoneView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initData();
    }

    public GramophoneView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initData();
    }

    public GramophoneView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttrs(context, attrs);
        initData();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.GramophoneView);
        if (attributes != null) {
            mDrawable = attributes.getDrawable(R.styleable.GramophoneView_src);

            attributes.recycle();
        }
    }

    private void initData() {
        if (mDrawable == null) {
            mDrawable = ContextCompat.getDrawable(getContext(), R.drawable.cd_cover);
        }
        mBitmap = ((BitmapDrawable) mDrawable).getBitmap();
        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mMatrix = new Matrix();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size;
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            size = DisplayUtil.dp2px(getContext(), 340);
            setMeasuredDimension(size, size);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(heightSpecSize, heightSpecSize);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, widthSpecSize);
        } else {
            size = Math.min(widthMeasureSpec, heightMeasureSpec);
            super.onMeasure(size, size);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingLeft();
        final int paddingTop = getPaddingLeft();
        final int paddingBottom = getPaddingLeft();
        int width = getWidth() - paddingLeft - paddingRight;
        int height = getHeight() - paddingTop - paddingBottom;
        Log.e("gzy", "width=" + width + " height=" + height);
        int halfSize = Math.min(width, height) / 2;

        // 中间CD封面图半径
        cdCoverR = halfSize * 3 / 5;
        int translateLength = halfSize - cdCoverR;

        float mScale = (cdCoverR * 2.0f) / Math.min(mBitmap.getHeight(), mBitmap.getWidth());
        mMatrix.setScale(mScale, mScale);
        mBitmapShader.setLocalMatrix(mMatrix);
        mCdCoverPaint.setShader(mBitmapShader);
        canvas.translate(translateLength, translateLength);
        canvas.rotate(mRotateDegrees, width / 2 - translateLength, height / 2 - translateLength);
        mCdOutPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorCdOutRing));
        canvas.drawCircle(cdCoverR, cdCoverR, cdCoverR + DisplayUtil.dp2px(getContext(), 4), mCdOutPaint);
        canvas.drawCircle(cdCoverR, cdCoverR, cdCoverR, mCdCoverPaint);
        mRotateDegrees += 0.5;


//        if (mRingList.size() == 0 || mRingList.get(mRingList.size() - 1).getR() - cdCoverR >= ringPadding) {
//            double angle = Math.random() * 360;
//            Ring ring = new Ring();
//            ring.setR(cdCoverR);
//            ring.setAngle(angle);
//            mRingList.add(ring);
//        }

        mRemoveRingList.clear();
        for (Ring ring1 : mRingList) {

            float[] colorMatrix = {1, 0, 0, 0, 0,
                    0, 1, 0, 0, 0,
                    0, 0, 1, 0, 0,
                    0, 0, 0, ((float) (ringPadding * 4) - (ring1.getR() - cdCoverR)) / 100, 0};
            mOrbitPaint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));

            mOrbitPaint.setStrokeWidth(4);
            mOrbitPaint.setStyle(Paint.Style.STROKE);

            canvas.drawCircle(width / 2 - translateLength,
                    height / 2 - translateLength, ring1.getR(), mOrbitPaint);

            mOrbitPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorAccentLight));
            mOrbitPaint.setStyle(Paint.Style.FILL);
            int x = (int) (Math.cos(ring1.getAngle()) * (ring1.getR()));
            int y = (int) (Math.sin(ring1.getAngle()) * (ring1.getR()));
            canvas.drawCircle(width / 2 + x - translateLength,
                    height / 2 + y - translateLength, ring1.getPlanetR(), mOrbitPaint);

            if (ring1.getR() < cdCoverR + ringPadding * 4) {
                ring1.setR(ring1.getR() + 1);
                ring1.setAngle(ring1.getAngle() - 0.02);
            } else {
                mRemoveRingList.add(ring1);
            }
        }

        mRingList.removeAll(mRemoveRingList);

        postDelayed(new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        }, 20);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                double angle = Math.random() * 360;
                Ring ring = new Ring();
                ring.setR(cdCoverR);
                ring.setAngle(angle);
                ring.setPlanetR((int) (Math.random() * 10));
                mRingList.add(ring);
                return true;
            default:
                return true;
        }
    }

    private class Ring {
        int r;
        double angle;
        int planetR;

        public int getR() {
            return r;
        }

        public void setR(int r) {
            this.r = r;
        }

        double getAngle() {
            return angle;
        }

        void setAngle(double angle) {
            this.angle = angle;
        }

        public int getPlanetR() {
            return planetR;
        }

        public void setPlanetR(int planetR) {
            this.planetR = planetR;
        }
    }
}
