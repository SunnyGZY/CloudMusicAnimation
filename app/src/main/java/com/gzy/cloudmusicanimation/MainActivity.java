package com.gzy.cloudmusicanimation;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * demo
 *
 * @author gaozongyang
 * @date 2018/11/23
 */
public class MainActivity extends AppCompatActivity {

    private ImageView mBgImageView;
    private GramophoneView mGramophoneView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initBackGround();
    }

    private void initView() {
        mBgImageView = findViewById(R.id.bg_image_view);
        mGramophoneView = findViewById(R.id.gramophone_view);
        mGramophoneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("gzy", "mGramophoneView onClicked");
            }
        });
    }

    private void initBackGround() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cd_cover);
        Bitmap resizeBitmap = resizeImage(bitmap, bitmap.getWidth() / 18, bitmap.getHeight() / 18);
        Bitmap changeLumBitmap = changeLumBitmap(resizeBitmap, 0.5f);
        Bitmap blurBitmap = RSBlurProcess.rsBlur(MainActivity.this, changeLumBitmap
                , 16);
        mBgImageView.setImageDrawable(new BitmapDrawable(getResources(), blurBitmap));
    }

    public Bitmap resizeImage(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) w) / width;
        float scaleHeight = ((float) h) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(bitmap, 0, 0, width,
                height, matrix, true);
    }

    private Bitmap changeLumBitmap(Bitmap bitmap, float lum) {
        ColorMatrix lumMatrix = new ColorMatrix();
        lumMatrix.setScale(lum, lum, lum, 1);
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.postConcat(lumMatrix);
        Bitmap bmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return bmp;
    }
}
