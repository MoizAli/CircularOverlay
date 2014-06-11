package com.example.circularoverlay.library;

import android.graphics.Bitmap;
import android.graphics.Canvas;

interface ICutoutViewDrawer {

    void setCutoutColour(int color);

    void drawCutoutView(Bitmap buffer, float x, float y, float scaleMultiplier);

    int getCutoutWidth();

    int getCutoutHeight();

    float getBlockedRadius();

    void setBackgroundColour(int backgroundColor);

    void erase(Bitmap bitmapBuffer);

    void drawToCanvas(Canvas canvas, Bitmap bitmapBuffer);
}
