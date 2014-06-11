package com.example.circularoverlay.library.targets;

import android.app.Activity;
import android.view.View;
import android.view.ViewParent;

/**
 * Reflector which finds action items in the standard API 11 ActionBar implementation
 */
class ActionBarReflector implements Reflector {

    private Activity mActivity;

    public ActionBarReflector(Activity activity) {
        mActivity = activity;
    }

    @Override
    public ViewParent getActionBarView() {
        return getHomeButton().getParent().getParent();
    }

    @Override
    public View getHomeButton() {
        View homeButton = mActivity.findViewById(android.R.id.home);
        if (homeButton == null) {
            throw new RuntimeException(
                    "insertCutoutViewWithType cannot be used when the theme " +
                            "has no ActionBar");
        }
        return homeButton;
    }

    @Override
    public void cutoutActionItem(int itemId) {

    }
}
