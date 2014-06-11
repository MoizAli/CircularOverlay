package com.example.circularoverlay.library;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Class used to draw the Arrow over the Target component.
 */
public class IconDrawer {
    private final Bitmap bitmapIcon;
    private final Context context;
    private final CutoutViewAreaCalculator calculator;
    private final int iconHeight;
    private final float outerRadius;
    private float NEG_OFFSET_X = 0;
    private float NEG_OFFSET_Y = 0;
    private float angle = 0.0f;
    private int invertX = 1;
    private int invertY = 1;
    public int canvasMidX = 0;
    public int canvasMidY = 0;
    boolean isLeft = false;
    boolean isRight = false;
    boolean isBottom = false;
    boolean isTop = false;

    public IconDrawer(Resources resources, CutoutViewAreaCalculator calculator, Context context) {
        bitmapIcon= BitmapFactory.decodeResource(resources, R.drawable.arrow);
        iconHeight = bitmapIcon.getHeight();
        outerRadius = resources.getDimension(R.dimen.overlay_radius_outer);
        this.calculator = calculator;
        this.context = context;
    }

    /**
     * This function draws the Arrow on the canvas
     * @param canvas
     * @param cutoutX the x co ordinate of the center of the circle
     * @param cutoutY the x co ordinate of the center of the circle
     */
    public void draw(Canvas canvas , float cutoutX , float cutoutY) {
        // pX is the x co ordinate on the circumference of the cirlce.
        double pX = cutoutX + outerRadius * Math.cos(angle) + NEG_OFFSET_X;

        // pY is the Y co ordinate on the circumference of the cirlce.
        double pY = cutoutY + outerRadius * Math.sin(angle) + NEG_OFFSET_Y;

        Matrix inverter = new Matrix();
        inverter.preScale(invertX , invertY);
        Bitmap dst = Bitmap.createBitmap(bitmapIcon , 0 , 0, bitmapIcon.getWidth() , bitmapIcon.getHeight() , inverter , false);
        canvas.drawBitmap(dst, (float)pX  , (float)pY - dst.getHeight() , new Paint( ));
    }

    public void calculateIconPosition (int canvasW , int canvasH, CutoutView cutoutView , float[] textPosition){
        Rect rectCutoutView = cutoutView.hasCutoutView() ?
                calculator.getmCutoutFeature() :
                new Rect();
        canvasMidX = canvasW / 2;
        canvasMidY = canvasH / 2;
        int[] areas = new int[4]; //left, top, right, bottom
        areas[0] = rectCutoutView.left * canvasH;
        areas[1] = rectCutoutView.top * canvasW;
        areas[2] = (canvasW - rectCutoutView.right) * canvasH;
        areas[3] = (canvasH - rectCutoutView.bottom) * canvasW;

        isRight = isValueInExcess(rectCutoutView.right , canvasMidX);
        isBottom = isValueInExcess(rectCutoutView.bottom , canvasMidY);
        isLeft = !isRight;
        isTop = !isBottom;

        int largest = 0;
        for(int i = 1; i < areas.length; i++) {
            if(areas[i] > areas[largest])
                largest = i;
        }

        switch (largest){
            case 0:
                angle = 90;
                invertX = 1;
                NEG_OFFSET_Y = +bitmapIcon.getHeight();
                invertY = 1;
                break;

            case 1:
                if(isBottom && isRight){
                    invertX = -1;
                    NEG_OFFSET_X = -bitmapIcon.getWidth();
                    angle = 180;
                }else if(isBottom && isLeft){
                    invertX = 1;
                    NEG_OFFSET_X = 0;
                    angle = 30;
                } else {
                    invertX = 1;
                    angle = 30;
                }

                //invertX = 1;
                NEG_OFFSET_Y = +0;
                invertY = 1;
                break;

            case 2:
                if(isBottom && isLeft){
                    angle = 30;
                    invertX = 1;
                    NEG_OFFSET_Y = 0;
                    NEG_OFFSET_X = 0;
                    invertY = 1;
                }else {
                angle = 45;
                invertX = 1;
                NEG_OFFSET_Y = +bitmapIcon.getHeight();
                NEG_OFFSET_X = 0;
                invertY = -1;
                }
                break;

            case 3:
                angle = 90;
                invertX = -1;
                NEG_OFFSET_Y = +bitmapIcon.getHeight();
                NEG_OFFSET_X = -bitmapIcon.getWidth();
                invertY = -1;
                break;

        }
    }

    /**
     * Returns true if param1 value is greater than param2
     * @param param1 cutoutView Value (Could be Right , Bottom , Left , Top).
     * @param param2 Mid Point of Canvas
     * @return
     */
    private boolean isValueInExcess(int param1, int param2) {
       return param1 > param2 ? true : false;
    }

}
