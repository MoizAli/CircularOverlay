package com.example.circularoverlay.library;

/**
* @author Alex
*/
public interface OnCutoutButtonEventListener {

    /**
     * Called when the CutoutView has been told to hide. Use {@link #onCutoutViewDidHide(CutoutView)}
     * if you want to know when the CutoutView has been fully hidden.
     */
    public void onCutoutViewHide(CutoutView cutoutView);

    /**
     * Called when the animation hiding the CutoutView has finished, and it is no longer visible on the screen.
     */
    public void onCutoutViewDidHide(CutoutView cutoutView);

    /**
     * Called when the CutoutView is shown.
     */
    public void onCutoutViewShow(CutoutView cutoutView);

    /**
     * Empty implementation of OnSCutoutViewViewEventListener such that null
     * checks aren't needed
     */
    public static final OnCutoutButtonEventListener NONE = new OnCutoutButtonEventListener() {
        @Override
        public void onCutoutViewHide(CutoutView cutoutView) {

        }

        @Override
        public void onCutoutViewDidHide(CutoutView cutoutView) {

        }

        @Override
        public void onCutoutViewShow(CutoutView cutoutView) {

        }
    };

}
