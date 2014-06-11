package com.example.circularoverlay.library.targets;

import android.app.Activity;
import android.view.View;
import android.view.ViewParent;


class AppCompatReflector implements Reflector {

    private Activity mActivity;

    public AppCompatReflector(Activity activity) {
        mActivity = activity;
    }

    @Override
    public ViewParent getActionBarView() {
        return getHomeButton().getParent().getParent();
    }

    @Override
    public View getHomeButton() {
        View homeButton = mActivity.findViewById(android.R.id.home);
        if (homeButton != null) {
            return homeButton;
        }
        int homeId = mActivity.getResources().getIdentifier("home", "id", mActivity.getPackageName());
        homeButton = mActivity.findViewById(homeId);
        if (homeButton == null) {
            throw new RuntimeException(
                    "insertShowcaseViewWithType cannot be used when the theme " +
                            "has no ActionBar");
        }
        return homeButton;
    }

    @Override
    public void cutoutActionItem(int itemId) {

    }
}
