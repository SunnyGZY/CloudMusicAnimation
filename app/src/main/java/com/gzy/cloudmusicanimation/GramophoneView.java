package com.gzy.cloudmusicanimation;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 网易云音乐动画
 * 寂寞星球
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
        int halfSize = Math.min(width, height) / 2;

        // 中间CD封面图半径
        cdCoverR = halfSize * 3 / 5;
        int translateLength = halfSize - cdCoverR;

        // 画CD
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

        // 画星球
        for (Ring ring : mRingList) {
            if (ring.isShouldDraw()) {
                int alpha = 110 * (ringPadding * 4 - (ring.getR() - cdCoverR)) / (ringPadding * 4) - 40;
                if (alpha != 0) {
                    mOrbitPaint.setColor(Color.argb(alpha, 255, 255, 255));
                    mOrbitPaint.setStrokeWidth(4);
                    mOrbitPaint.setStyle(Paint.Style.STROKE);
                    canvas.drawCircle(width / 2 - translateLength,
                            height / 2 - translateLength, ring.getR(), mOrbitPaint);
                    mOrbitPaint.setStyle(Paint.Style.FILL);
                    int x = (int) (Math.cos(ring.getAngle()) * (ring.getR()));
                    int y = (int) (Math.sin(ring.getAngle()) * (ring.getR()));
                    canvas.drawCircle(width / 2 + x - translateLength,
                            height / 2 + y - translateLength, ring.getPlanetR(), mOrbitPaint);

                    if (ring.getR() < cdCoverR + ringPadding * 4) {
                        ring.setR(ring.getR() + 1);
                        ring.setAngle(ring.getAngle() - 0.02);
                    }
                } else {
                    ring.setShouldDraw(false);
                }
            }
        }

        invalidate();
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                showPlanet();
                break;
            case MotionEvent.ACTION_UP:
                performClick();
                break;
            default:
                break;
        }
        return true;
    }

    private void showPlanet() {
        double angle = Math.random() * 360;
        int planetR = (int) (Math.random() * 10);
        for (Ring ring : mRingList) {
            if (!ring.isShouldDraw()) {
                ring.setR(cdCoverR);
                ring.setAngle(angle);
                ring.setPlanetR(planetR);
                ring.setShouldDraw(true);
                return;
            }
        }

        Ring ring = new Ring();
        ring.setR(cdCoverR);
        ring.setAngle(angle);
        ring.setPlanetR(planetR);
        ring.setShouldDraw(true);
        mRingList.add(ring);
    }

    private class Ring {
        int r;
        double angle;
        int planetR;
        private boolean shouldDraw;

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

        int getPlanetR() {
            return planetR;
        }

        void setPlanetR(int planetR) {
            this.planetR = planetR;
        }

        boolean isShouldDraw() {
            return shouldDraw;
        }

        void setShouldDraw(boolean shouldDraw) {
            this.shouldDraw = shouldDraw;
        }
    }
}
