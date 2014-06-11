package com.example.circularoverlay.library;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.circularoverlay.library.targets.Target;


/**
 * A view which allows you to show areas of your app with an explanation.
 */
public class CutoutView extends RelativeLayout
        implements View.OnClickListener, View.OnTouchListener, ViewTreeObserver.OnPreDrawListener, ViewTreeObserver.OnGlobalLayoutListener {

    private static final int HOLO_BLUE = Color.parseColor("#33B5E5");

    private final Button mEndButton;
    private final TextDrawer textDrawer;
    private final IconDrawer iconDrawer;
    private final ICutoutViewDrawer cutoutViewDrawer;
    private final CutoutViewAreaCalculator cutoutViewAreaCalculator;
    private final AnimationFactory animationFactory;


    // Cutout metrics
    private int cutoutXPoint = -1;
    private int cutoutYPoint = -1;
    private float scaleMultiplier = 1f;

    // Touch items
    private boolean hasCustomClickListener = false;
    private boolean blockTouches = true;
    private boolean hideOnTouch = false;
    private OnCutoutButtonEventListener mEventListener = OnCutoutButtonEventListener.NONE;

    private boolean hasAlteredText = false;
    private boolean hasNoTarget = false;
    private boolean shouldCentreText;
    private Bitmap bitmapBuffer;

    // Animation items
    private long fadeInMillis;
    private long fadeOutMillis;

    protected CutoutView(Context context, boolean newStyle) {
        this(context, null, R.styleable.CustomTheme_overlayViewStyle, newStyle);
    }

    protected CutoutView(Context context, AttributeSet attrs, int defStyle, boolean newStyle) {
        super(context, attrs, defStyle);

        ApiUtils apiUtils = new ApiUtils();
        animationFactory = new AnimatorAnimationFactory();
        cutoutViewAreaCalculator = new CutoutViewAreaCalculator();

        apiUtils.setFitsSystemWindowsCompat(this);
        getViewTreeObserver().addOnPreDrawListener(this);
        getViewTreeObserver().addOnGlobalLayoutListener(this);

        // Get the attributes for the CutoutView
        final TypedArray styled = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.OverlayView, R.attr.overlayViewStyle,
                        R.style.OverLayView);

        // Set the default animation times
        fadeInMillis = getResources().getInteger(android.R.integer.config_mediumAnimTime);
        fadeOutMillis = getResources().getInteger(android.R.integer.config_mediumAnimTime);

        mEndButton = (Button) LayoutInflater.from(context).inflate(R.layout.exit_button, null);
        Typeface btnTypeFace = Typeface.createFromAsset(getResources().getAssets(), "fonts/Roboto-Regular.ttf");
        mEndButton.setTypeface(btnTypeFace);
        mEndButton.setTextColor(Color.WHITE);
        if (newStyle) {
            cutoutViewDrawer = new CutoutViewDrawer(getResources());
        } else {
            cutoutViewDrawer = new BaseCutoutFeatureDrawer(getResources());
        }
        textDrawer = new TextDrawer(getResources(), cutoutViewAreaCalculator, getContext());
        iconDrawer = new IconDrawer(getResources(), cutoutViewAreaCalculator, getContext());
        updateStyle(styled, false);

        init();
    }

    private void init() {

        setOnTouchListener(this);

        if (mEndButton.getParent() == null) {
            int margin = (int) getResources().getDimension(R.dimen.button_margin);
            LayoutParams lps = (LayoutParams) generateDefaultLayoutParams();
            lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            lps.addRule(RelativeLayout.CENTER_HORIZONTAL);
            lps.setMargins(margin, margin, margin, margin);
            mEndButton.setLayoutParams(lps);
            mEndButton.setText(R.string.ok);
            if (!hasCustomClickListener) {
                mEndButton.setOnClickListener(this);
            }
            addView(mEndButton);
        }

    }


    void setCutoutPosition(Point point) {
        setCutoutPosition(point.x, point.y);
    }

    void setCutoutPosition(int x, int y) {
        cutoutXPoint = x;
        cutoutYPoint = y;
        //init();
        invalidate();
    }

    public void setTarget(final Target target) {
        setCutout(target, false);
    }

    public void setCutout(final Target target, final boolean animate) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                updateBitmap();
                Point targetPoint = target.getPoint();
                if (targetPoint != null) {
                    hasNoTarget = false;
                    if (animate) {
                        animationFactory.animateTargetToPoint(CutoutView.this, targetPoint);
                    } else {
                        setCutoutPosition(targetPoint);
                    }
                } else {
                    hasNoTarget = true;
                    invalidate();
                }

            }
        }, 100);
    }

    private void updateBitmap() {
        if (bitmapBuffer == null || haveBoundsChanged()) {
            bitmapBuffer = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        }
    }

    private boolean haveBoundsChanged() {
        return getMeasuredWidth() != bitmapBuffer.getWidth() ||
                getMeasuredHeight() != bitmapBuffer.getHeight();
    }

    public boolean hasCutoutView() {
        return (cutoutXPoint != 1000000 && cutoutYPoint != 1000000) || !hasNoTarget;
    }

    public void setCutoutXPoint(int x) {
        setCutoutPosition(x, cutoutYPoint);
    }

    public void setCutoutYPoint(int y) {
        setCutoutPosition(cutoutXPoint, y);
    }

    public int getCutoutXPoint() {
        return cutoutXPoint;
    }

    public int getCutoutYPoint() {
        return cutoutYPoint;
    }

    /**
     * Override the standard button click event
     *
     * @param listener Listener to listen to on click events
     */
    public void overrideButtonClick(OnClickListener listener) {
        if (mEndButton != null) {
            mEndButton.setOnClickListener(listener != null ? listener : this);
        }
        hasCustomClickListener = true;
    }

    public void setOnCutoutEventListener(OnCutoutButtonEventListener listener) {
        if (listener != null) {
            mEventListener = listener;
        } else {
            mEventListener = OnCutoutButtonEventListener.NONE;
        }
    }

    public void setButtonText(CharSequence text) {
        if (mEndButton != null) {
            mEndButton.setText(text);
        }
    }

    @Override
    public boolean onPreDraw() {
        boolean recalculatedCling = cutoutViewAreaCalculator.calculateCutoutViewRect(cutoutXPoint, cutoutYPoint, cutoutViewDrawer);
        boolean recalculateText = recalculatedCling || hasAlteredText;

        if (recalculateText) {
            textDrawer.calculateTextPosition(getMeasuredWidth(), getMeasuredHeight(), this, shouldCentreText);
            iconDrawer.calculateIconPosition(getMeasuredWidth(), getMeasuredHeight(), this, textDrawer.getBestTextPosition());
        }
        hasAlteredText = false;
        return true;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (cutoutXPoint < 0 || cutoutYPoint < 0) {
            super.dispatchDraw(canvas);
            return;
        }

        //Draw background color
        cutoutViewDrawer.erase(bitmapBuffer);

        // Draw the cutout drawable
        if (!hasNoTarget) {
            cutoutViewDrawer.drawCutoutView(bitmapBuffer, cutoutXPoint, cutoutYPoint, scaleMultiplier);
            cutoutViewDrawer.drawToCanvas(canvas, bitmapBuffer);
        }
        iconDrawer.draw(canvas, cutoutXPoint, cutoutYPoint);
        // Draw the text on the screen, recalculating its position if necessary
        textDrawer.draw(canvas);

        super.dispatchDraw(canvas);

    }

    @Override
    public void onClick(View view) {
        hide();
    }

    public void hide() {
        clearBitmap();
        mEventListener.onCutoutViewHide(this);
        fadeOutCutout();
    }

    private void clearBitmap() {
        if (bitmapBuffer != null && !bitmapBuffer.isRecycled()) {
            bitmapBuffer.recycle();
            bitmapBuffer = null;
        }
    }

    private void fadeOutCutout() {
        animationFactory.fadeOutView(this, fadeOutMillis, new AnimationFactory.AnimationEndListener() {
            @Override
            public void onAnimationEnd() {
                setVisibility(View.GONE);
                mEventListener.onCutoutViewDidHide(CutoutView.this);
            }
        });
    }

    //Shows Overlay with FadeIn Animation
    public void show() {
        mEventListener.onCutoutViewShow(this);
        fadeInCutout();
    }

    private void fadeInCutout() {
        animationFactory.fadeInView(this, fadeInMillis,
                new AnimationFactory.AnimationStartListener() {
                    @Override
                    public void onAnimationStart() {
                        setVisibility(View.VISIBLE);
                    }
                }
        );
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        float xDelta = Math.abs(motionEvent.getRawX() - cutoutXPoint);
        float yDelta = Math.abs(motionEvent.getRawY() - cutoutYPoint);
        double distanceFromFocus = Math.sqrt(Math.pow(xDelta, 2) + Math.pow(yDelta, 2));

        if (MotionEvent.ACTION_UP == motionEvent.getAction() &&
                hideOnTouch && distanceFromFocus > cutoutViewDrawer.getBlockedRadius()) {
            this.hide();
            return true;
        }

        return blockTouches && distanceFromFocus > cutoutViewDrawer.getBlockedRadius();
    }

    private static void insertCutoutView(CutoutView cutoutView, Activity activity) {
        ((ViewGroup) activity.getWindow().getDecorView()).addView(cutoutView);
        cutoutView.show();
//        if (!cutOutView.hasShot()) {
//        } else {
//            cutOutView.hideImmediate();
//        }
    }

    private void hideImmediate() {
        setVisibility(GONE);
    }

    public void setContentTitle(CharSequence title) {
        textDrawer.setContentTitle(title);
    }

    public void setContentText(CharSequence text) {
        textDrawer.setContentText(text);
    }

    private void setScaleMultiplier(float scaleMultiplier) {
        this.scaleMultiplier = scaleMultiplier;
    }

    @Override
    public void onGlobalLayout() {
            updateBitmap();
    }

    public void hideButton() {
        mEndButton.setVisibility(GONE);
    }

    public void showButton() {
        mEndButton.setVisibility(VISIBLE);
    }

    /**
     * Builder class which allows easier creation of {@link CutoutView}s.
     * It is recommended that you use this Builder class.
     */
    public static class Builder {

        final CutoutView cutOutView;
        private final Activity activity;

        public Builder(Activity activity) {
            this(activity, false);
        }

        public Builder(Activity activity, boolean useNewStyle) {
            this.activity = activity;
            this.cutOutView = new CutoutView(activity, useNewStyle);
            this.cutOutView.setTarget(Target.NONE);
        }

        /**
         * Create the {@link com.example.circularoverlay.library.CutoutView} and show it.
         *
         * @return the created CutoutView
         */
        public CutoutView build() {
            insertCutoutView(cutOutView, activity);
            return cutOutView;
        }

        /**
         * Set the title text shown on the CutoutView.
         */
        public Builder setContentTitle(int resId) {
            return setContentTitle(activity.getString(resId));
        }

        /**
         * Set the text to center
         *
         * @param shdCenterText
         */
        public Builder setShdCenterText(boolean shdCenterText) {
            return shdCenterText(shdCenterText);
        }

        /**
         * Set the title text shown on the CutoutView.
         */
        public Builder setContentTitle(CharSequence title) {
            cutOutView.setContentTitle(title);
            return this;
        }

        /**
         * Set the descriptive text shown on the CutoutView.
         */
        public Builder setContentText(int resId) {
            return setContentText(activity.getString(resId));
        }

        /**
         * Set the descriptive text shown on the CutoutView.
         */
        public Builder setContentText(CharSequence text) {
            cutOutView.setContentText(text);
            return this;
        }

        public Builder shdCenterText(boolean shdCenterText) {
            cutOutView.setShouldCentreText(shdCenterText);
            return this;
        }

        /**
         * Set the target of the CutoutView.
         *
         * @param target a {@link com.example.circularoverlay.library.targets.Target} representing
         *               the item to CutoutView (e.g., a button, or action item).
         */
        public Builder setTarget(Target target) {
            cutOutView.setTarget(target);
            return this;
        }

        /**
         * Set the style of the CutoutView. See the sample app for example styles.
         */
        public Builder setStyle(int theme) {
            cutOutView.setStyle(theme);
            return this;
        }

        /**
         * Set a listener which will override the button clicks.
         * <p/>
         * Note that you will have to manually hide the CutoutView
         */
        public Builder setOnClickListener(OnClickListener onClickListener) {
            cutOutView.overrideButtonClick(onClickListener);
            return this;
        }

        /**
         * Don't make the CutoutView block touches on itself. This doesn't
         * block touches in the CutoutView area.
         * <p/>
         * By default, the CutoutView does block touches
         */
        public Builder doNotBlockTouches() {
            cutOutView.setBlocksTouches(false);
            return this;
        }

        /**
         * Make this CutoutView hide when the user touches outside the CutoutView area.
         * This enables {@link #doNotBlockTouches()} as well.
         * <p/>
         * By default, the CutoutView doesn't hide on touch.
         */
        public Builder hideOnTouchOutside() {
            cutOutView.setBlocksTouches(true);
            cutOutView.setHideOnTouchOutside(true);
            return this;
        }

        public Builder setCutoutEventListener(OnCutoutButtonEventListener cutoutEventListener) {
            cutOutView.setOnCutoutEventListener(cutoutEventListener);
            return this;
        }


    }

    /**
     * Set whether the text should be centred in the screen, or left-aligned (which is the default).
     */
    public void setShouldCentreText(boolean shouldCentreText) {
        this.shouldCentreText = shouldCentreText;
        hasAlteredText = true;
        invalidate();
    }

    /**
     * Change the position of the CutoutView's button from the default bottom-right position.
     *
     * @param layoutParams a {@link android.widget.RelativeLayout.LayoutParams} representing
     *                     the new position of the button
     */
    public void setButtonPosition(LayoutParams layoutParams) {
        mEndButton.setLayoutParams(layoutParams);
    }

    /**
     * Set the duration of the fading in and fading out of the CutoutView
     */
    private void setFadeDurations(long fadeInMillis, long fadeOutMillis) {
        this.fadeInMillis = fadeInMillis;
        this.fadeOutMillis = fadeOutMillis;
    }

    /**
     * @see com.example.circularoverlay.library.CutoutView.Builder#hideOnTouchOutside()
     */
    public void setHideOnTouchOutside(boolean hideOnTouch) {
        this.hideOnTouch = hideOnTouch;
    }

    /**
     * @see com.example.circularoverlay.library.CutoutView.Builder#doNotBlockTouches()
     */
    public void setBlocksTouches(boolean blockTouches) {
        this.blockTouches = blockTouches;
    }

    /**
     * @see com.example.circularoverlay.library.CutoutView.Builder#setStyle(int)
     */
    public void setStyle(int theme) {
        TypedArray array = getContext().obtainStyledAttributes(theme, R.styleable.OverlayView);
        updateStyle(array, true);
    }

    private void updateStyle(TypedArray styled, boolean invalidate) {
        int backgroundColor = styled.getColor(R.styleable.OverlayView_backgroundColor, Color.argb(128, 80, 80, 80));
        int cutoutColor = styled.getColor(R.styleable.OverlayView_overlayColor, HOLO_BLUE);
        String buttonText = styled.getString(R.styleable.OverlayView_buttonText);
        if (TextUtils.isEmpty(buttonText)) {
            buttonText = getResources().getString(R.string.ok);
        }
        int titleTextAppearance = styled.getResourceId(R.styleable.OverlayView_titleTextAppearance,
                R.style.TextAppearance_OverlayView_Title);
        int detailTextAppearance = styled.getResourceId(R.styleable.OverlayView_detailTextAppearance,
                R.style.TextAppearance_OverlayView_Detail);

        styled.recycle();

        cutoutViewDrawer.setCutoutColour(cutoutColor);
        cutoutViewDrawer.setBackgroundColour(backgroundColor);
        //tintButton(cutoutColor, tintButton);
        mEndButton.setText(buttonText);
        textDrawer.setTitleStyling(titleTextAppearance);
        textDrawer.setDetailStyling(detailTextAppearance);
        hasAlteredText = true;

        if (invalidate) {
            invalidate();
        }
    }

    private void tintButton(int cutoutColor, boolean tintButton) {
        if (tintButton) {
            mEndButton.getBackground().setColorFilter(cutoutColor, PorterDuff.Mode.MULTIPLY);
        } else {
            mEndButton.getBackground().setColorFilter(HOLO_BLUE, PorterDuff.Mode.MULTIPLY);
        }
    }

}
