package com.example.circularoverlay.library;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.Log;

/**
 * Draws the text as required by the CutoutView
 */
class TextDrawer {

    private final TextPaint titlePaint;
    private final TextPaint textPaint;
    private final Context context;
    private final CutoutViewAreaCalculator calculator;
    private final float padding;
    private final float iconHeight;
    private final float actionBarOffset;

    private CharSequence mTitle, mDetails;
    private float[] mBestTextPosition = new float[3];
    private DynamicLayout mDynamicTitleLayout;
    private DynamicLayout mDynamicDetailLayout;
    private TextAppearanceSpan mTitleSpan;
    private TextAppearanceSpan mDetailSpan;
    private boolean hasRecalculated;
    private int canvasMidX;
    private int canvasMidY;
    boolean isLeft = false;
    boolean isRight = false;
    boolean isBottom = false;
    boolean isTop = false;

    public TextDrawer(Resources resources, CutoutViewAreaCalculator calculator, Context context) {
        padding = resources.getDimension(R.dimen.text_padding);
        //Calculate height of arrow drawable. @param iconHeight used to adjust Text
        Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.arrow);
        iconHeight = bitmap.getHeight();
        actionBarOffset = resources.getDimension(R.dimen.action_bar_offset);

        this.calculator = calculator;
        this.context = context;

        Typeface fontFaceTitle = Typeface.createFromAsset(resources.getAssets(), "fonts/Roboto-Light.ttf");
        Typeface fontFaceText  = Typeface.createFromAsset(resources.getAssets(), "fonts/Roboto-MediumItalic.ttf");


        titlePaint = new TextPaint();
        titlePaint.setTypeface(fontFaceTitle);
        titlePaint.setAntiAlias(true);

        textPaint = new TextPaint();
        textPaint.setTypeface(fontFaceText);
        textPaint.setAntiAlias(true);
    }

    public void draw(Canvas canvas) {
        if (shouldDrawText()) {
            float[] textPosition = getBestTextPosition();

            if (!TextUtils.isEmpty(mTitle)) {
                canvas.save();
                if (hasRecalculated) {
                    mDynamicTitleLayout = new DynamicLayout(mTitle, titlePaint,
                            (int) textPosition[2], Layout.Alignment.ALIGN_CENTER,
                            1.0f, 1.0f, true);
                }
                if (mDynamicTitleLayout != null) {
                    canvas.translate(textPosition[0], textPosition[1]);
                    mDynamicTitleLayout.draw(canvas);
                    canvas.restore();
                }
            }

            if (!TextUtils.isEmpty(mDetails)) {
                canvas.save();
                if (hasRecalculated) {
                    mDynamicDetailLayout = new DynamicLayout(mDetails, textPaint,
                            (int) textPosition[2],
                            Layout.Alignment.ALIGN_CENTER,
                            1.2f, 1.0f, true);
                }
                float offsetForTitle = mDynamicTitleLayout != null ? mDynamicTitleLayout.getHeight() :
                        0;
                if (mDynamicDetailLayout != null) {
                    canvas.translate(textPosition[0], textPosition[1] + offsetForTitle);
                    mDynamicDetailLayout.draw(canvas);
                    canvas.restore();
                }

            }
        }
        hasRecalculated = false;
    }

    public void setContentText(CharSequence details) {
        if (details != null) {
            SpannableString ssbDetail = new SpannableString(details);
            ssbDetail.setSpan(mDetailSpan, 0, ssbDetail.length(), 0);
            mDetails = ssbDetail;
        }
    }

    public void setContentTitle(CharSequence title) {
        if (title != null) {
            SpannableString ssbTitle = new SpannableString(title);
            ssbTitle.setSpan(mTitleSpan, 0, ssbTitle.length(), 0);
            mTitle = ssbTitle;
        }
    }

    /**
     * Calculates the best place to position text
     *  @param canvasW width of the screen
     * @param canvasH height of the screen
     * @param shouldCentreText
     */
    public void calculateTextPosition(int canvasW, int canvasH, CutoutView cutoutView, boolean shouldCentreText) {

    	Rect rectCutoutView = cutoutView.hasCutoutView() ?
    			calculator.getmCutoutFeature() :
    			new Rect();
    	
    	int[] areas = new int[4]; //left, top, right, bottom
    	areas[0] = rectCutoutView.left * canvasH;
    	areas[1] = rectCutoutView.top * canvasW;
    	areas[2] = (canvasW - rectCutoutView.right) * canvasH;
    	areas[3] = (canvasH - rectCutoutView.bottom) * canvasW;

        canvasMidX = canvasW / 2;
        canvasMidY = canvasH / 2;

        isRight = isValueInExcess(rectCutoutView.right , canvasMidX);
        isBottom = isValueInExcess(rectCutoutView.bottom , canvasMidY);
        isLeft = !isRight;
        isTop = !isBottom;

    	
    	int largest = 0;
    	for(int i = 1; i < areas.length; i++) {
    		if(areas[i] > areas[largest])
    			largest = i;
    	}
    	
    	// Position text in largest area
    	switch(largest) {
        //Left Poistion
    	case 0:
            Log.e("Left" , "yea");
            mBestTextPosition[0] = padding;
    		mBestTextPosition[1] =  rectCutoutView.bottom ;
    		mBestTextPosition[2] = canvasW - 2 * padding;;//rectCutoutView.left - 2 * padding;
    		break;
    	case 1:
            //Top Position
            Log.e("Top" , "yea");
    		mBestTextPosition[0] = padding;
    		mBestTextPosition[1] =  rectCutoutView.top - iconHeight*2 - padding ;
    		mBestTextPosition[2] = canvasW - 2 * padding;
    		break;
    	case 2:
            //Right Position
            Log.e("Right" , "yea");
            if(isBottom && isLeft){
                mBestTextPosition[0] = padding;
                mBestTextPosition[1] =  rectCutoutView.top - iconHeight*3 - padding ;
                mBestTextPosition[2] = canvasW - 2 * padding;
            }else {
    		mBestTextPosition[0] = padding;//rectCutoutView.right + padding;
    		mBestTextPosition[1] = padding  + rectCutoutView.bottom  ;
    		mBestTextPosition[2] = canvasW - 2 * padding;//(canvasW - rectCutoutView.right) - 2 * padding;
            }
    		break;
    	case 3:
            //Bottom Position
            Log.e("Bottm" , "yea");
    		mBestTextPosition[0] = padding;
    		mBestTextPosition[1] = rectCutoutView.bottom + padding + iconHeight / 2;
    		mBestTextPosition[2] = canvasW - 2 * padding;
    		break;
    	}
    	if(shouldCentreText) {
	    	// Center text vertically or horizontally
	    	switch(largest) {
	    	case 0:
	    	case 2:
	    		mBestTextPosition[1] += canvasH / 4;
	    		break;
	    	case 1:
	    	case 3:
	    		mBestTextPosition[2] /= 2;
	    		mBestTextPosition[0] += canvasW / 4;
	    		break;
	    	} 
    	} else {
    		// As text is not centered add actionbar padding if the text is left or right
	    	switch(largest) {
	    		case 0:
	    		case 2:
	    			mBestTextPosition[1] += actionBarOffset;
	    			break;
	    	}
    	}

        hasRecalculated = true;
    }

    public void setTitleStyling(int styleId) {
        mTitleSpan = new TextAppearanceSpan(this.context, styleId);
        setContentTitle(mTitle);
    }

    public void setDetailStyling(int styleId) {
        mDetailSpan = new TextAppearanceSpan(this.context, styleId);
        setContentText(mDetails);
    }

    public CharSequence getContentTitle() {
        return mTitle;
    }

    public CharSequence getContentText() {
        return mDetails;
    }

    public float[] getBestTextPosition() {
        return mBestTextPosition;
    }

    public boolean shouldDrawText() {
        return !TextUtils.isEmpty(mTitle) || !TextUtils.isEmpty(mDetails);
    }

    /**
     * Returns true if param1 value is greater than param2
     * @param param1 CutoutView Value (Could be Right , Bottom , Left , Top).
     * @param param2 Mid Point of Canvas
     * @return
     */
    private boolean isValueInExcess(int param1, int param2) {
        return param1 > param2 ? true : false;
    }
}
