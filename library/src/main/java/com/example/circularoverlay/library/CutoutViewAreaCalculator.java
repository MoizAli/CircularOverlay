package com.example.circularoverlay.library;

import android.graphics.Rect;
import android.util.Log;

/**
 * Class responsible for calculating where the cutoutFeature should position itself
 */
class CutoutViewAreaCalculator {

    private final Rect mCutoutFeature = new Rect();

    /**
     * Creates a {@link android.graphics.Rect} which represents the area the cutoutFeature covers. Used
     * to calculate where best to place the text
     *
     * @return true if voidedArea has changed, false otherwise.
     */
    public boolean calculateCutoutViewRect(float x, float y, ICutoutViewDrawer cutoutFeatureDrawer) {

        int cx = (int) x, cy = (int) y;
        int dw = cutoutFeatureDrawer.getCutoutWidth();
        int dh = cutoutFeatureDrawer.getCutoutHeight();

        if (mCutoutFeature.left == cx - dw / 2 && mCutoutFeature.top == cy - dh / 2) {
            return false;
        }

        Log.d("CutoutView", "Recalculated");

        mCutoutFeature.left = cx - dw / 2;
        mCutoutFeature.top = cy - dh / 2;
        mCutoutFeature.right = cx + dw / 2;
        mCutoutFeature.bottom = cy + dh / 2;

        return true;

    }

    public Rect getmCutoutFeature() {
        return mCutoutFeature;
    }

}
