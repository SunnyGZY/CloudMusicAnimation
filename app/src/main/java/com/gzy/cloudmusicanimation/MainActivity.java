package com.gzy.cloudmusicanimation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
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

    // 定义播放声音的MediaPlayer
    private MediaPlayer mPlayer;
    // 定义系统的频谱
    private Visualizer mVisualizer;

    private ImageView mBgImageView;
    private GramophoneView mGramophoneView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initAudioPlayer();
    }

    private void initView() {
        mBgImageView = findViewById(R.id.bg_image_view);
        mGramophoneView = findViewById(R.id.gramophone_view);
        Drawable drawable = getDrawable(R.drawable.lemon);
        mGramophoneView.setCdCoverDrawable(drawable);
        mGramophoneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("gzy", "mGramophoneView onClicked");
            }
        });
        initBackGround(drawable);
    }

    private void initAudioPlayer() {
        //设置音频流 - STREAM_MUSIC：音乐回放即媒体音量
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mPlayer = MediaPlayer.create(this, R.raw.lemon);
        setupVisualizer();
        mPlayer.start();
    }

    /**
     * 初始化频谱
     */
    private void setupVisualizer() {
        mVisualizer = new Visualizer(mPlayer.getAudioSessionId());
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        mVisualizer.setDataCaptureListener(
                new Visualizer.OnDataCaptureListener() {
                    //这个回调应该采集的是快速傅里叶变换有关的数据
                    @Override
                    public void onFftDataCapture(Visualizer visualizer,
                                                 byte[] fft, int samplingRate) {
                    }

                    //这个回调应该采集的是波形数据
                    @Override
                    public void onWaveFormDataCapture(Visualizer visualizer,
                                                      byte[] waveform, int samplingRate) {
                        for (int i = 0; i < waveform.length - 1; i++) {
                            // 根据波形值计算该矩形的高度
                            float top = (byte) (waveform[i + 1] + 128);
                            if (top <= -80) {
                                mGramophoneView.showPlanet();
                            }
                            Log.e("gaozy", top + "");
                        }
                    }
                }, Visualizer.getMaxCaptureRate() / 2, true, false);
        mVisualizer.setEnabled(true);
    }

    private void initBackGround(Drawable drawable) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        Bitmap bitmap = bitmapDrawable.getBitmap();
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
