package com.gzy.cloudmusicanimation;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

public class RSBlurProcess {
    /**
     * 高斯模糊
     *
     * @param context 上下文
     * @param source  输入 Bitmap
     * @param radius  高斯模糊半径
     * @return 输出 Bitmap
     */
    public static Bitmap rsBlur(Context context, Bitmap source, int radius) {
        RenderScript renderScript = RenderScript.create(context);
        final Allocation input = Allocation.createFromBitmap(renderScript, source);
        final Allocation output = Allocation.createTyped(renderScript, input.getType());
        ScriptIntrinsicBlur scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        scriptIntrinsicBlur.setInput(input);
        scriptIntrinsicBlur.setRadius(radius);
        scriptIntrinsicBlur.forEach(output);
        output.copyTo(source);
        renderScript.destroy();

        return source;
    }
}