package com.example.circularoverlay.library.targets;

import android.view.View;
import android.view.ViewParent;

interface Reflector {
    View getHomeButton();

    void cutoutActionItem(int itemId);

    ViewParent getActionBarView();

    public enum ActionBarType {
        STANDARD, APP_COMPAT, ACTIONBAR_SHERLOCK
    }
}
