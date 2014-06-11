package com.example.circularoverlay.library;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;


class BaseCutoutFeatureDrawer implements ICutoutViewDrawer {

    protected final Paint eraserPaint;
    protected final Drawable cutoutDrawable;
    private final Paint basicPaint;
    private final float cutoutRadius;
    protected int backgroundColour;

    public BaseCutoutFeatureDrawer(Resources resources) {
        PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY);
        eraserPaint = new Paint();
        eraserPaint.setColor(0x000000);
        eraserPaint.setAlpha(77);
        eraserPaint.setXfermode(xfermode);
        eraserPaint.setAntiAlias(true);
        basicPaint = new Paint();
        cutoutRadius = resources.getDimension(R.dimen.overlay_radius);
        cutoutDrawable = resources.getDrawable(R.drawable.cling_bleached);
    }

    @Override
    public void setCutoutColour(int color) {
        cutoutDrawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
    }

    @Override
    public void drawCutoutView(Bitmap buffer, float x, float y, float scaleMultiplier) {
    }

    @Override
    public int getCutoutWidth() {
        return cutoutDrawable.getIntrinsicWidth();
    }

    @Override
    public int getCutoutHeight() {
        return cutoutDrawable.getIntrinsicHeight();
    }

    @Override
    public float getBlockedRadius() {
        return cutoutRadius;
    }

    @Override
    public void setBackgroundColour(int backgroundColor) {
        this.backgroundColour = backgroundColor;
    }

    @Override
    public void erase(Bitmap bitmapBuffer) {
        bitmapBuffer.eraseColor(backgroundColour);
    }

    @Override
    public void drawToCanvas(Canvas canvas, Bitmap bitmapBuffer) {
        canvas.drawBitmap(bitmapBuffer, 0, 0, basicPaint);
    }

}
