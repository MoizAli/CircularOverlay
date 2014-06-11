package com.example.circularoverlay.library.targets;

import android.app.Activity;
import android.graphics.Point;
import android.view.ViewParent;

/**
 * Represents an Action item to highlight as a cutout component (e.g., one of the buttons on an ActionBar).
 * To showcase specific action views such as the home button, use {@link com.example.circularoverlay.library.targets.ActionItemTarget}
 *
 * @see com.example.circularoverlay.library.targets.ActionItemTarget
 */
public class ActionItemTarget implements Target {

    private final Activity mActivity;
    private final int mItemId;

    ActionBarViewWrapper mActionBarWrapper;

    public ActionItemTarget(Activity activity, int itemId) {
        mActivity = activity;
        mItemId = itemId;
    }

    @Override
    public Point getPoint() {
        setUp();
        return new ViewTarget(mActionBarWrapper.getActionItem(mItemId)).getPoint();
    }

    protected void setUp() {
        Reflector reflector = ReflectorFactory.getReflectorForActivity(mActivity);
        ViewParent p = reflector.getActionBarView(); //ActionBarView
        mActionBarWrapper = new ActionBarViewWrapper(p);
    }

}
