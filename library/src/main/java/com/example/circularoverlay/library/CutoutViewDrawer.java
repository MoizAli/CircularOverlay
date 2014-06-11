package com.example.circularoverlay.library;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;

/**
 * Class which draws the circle on top of the Target Component.
 *
 */
class CutoutViewDrawer extends BaseCutoutFeatureDrawer {

    private static final int ALPHA_30_PERCENT = 179;
    private static final float NEG_OFFSET_X = 0.0f;
    private static final float NEG_OFFSET_Y = 0.0f;
    private final float outerRadius;
    private final float innerRadius;

    public Resources getResources() {
        return resources;
    }

    public void setResources(Resources resources) {
        this.resources = resources;
    }

    Resources resources;

    public CutoutViewDrawer(Resources resources) {
        super(resources);
        setResources(resources);
        outerRadius = resources.getDimensionPixelSize(R.dimen.overlay_radius_outer);
        innerRadius = resources.getDimensionPixelSize(R.dimen.overlay_radius_inner);
    }

    @Override
    public void setCutoutColour(int color) {
        eraserPaint.setColor(color);
    }

    @Override
    public void drawCutoutView(Bitmap buffer, float x, float y, float scaleMultiplier) {
        Canvas bufferCanvas = new Canvas(buffer);
        eraserPaint.setAlpha(ALPHA_30_PERCENT);
        bufferCanvas.drawCircle(x, y, outerRadius, eraserPaint);
        eraserPaint.setAlpha(0);
        bufferCanvas.drawCircle(x, y, innerRadius, eraserPaint);
    }

    @Override
    public int getCutoutWidth() {
        return (int) (outerRadius * 2);
    }

    @Override
    public int getCutoutHeight() {
        return (int) (outerRadius * 2);
    }

    @Override
    public float getBlockedRadius() {
        return innerRadius;
    }

    @Override
    public void setBackgroundColour(int backgroundColor) {
        this.backgroundColour = backgroundColor;
    }
}
