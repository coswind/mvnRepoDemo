package com.coswind.viewpagerindicator;

/**
 * Created by coswind on 11/29/13.
 */
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockExpandableListActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.app.SherlockPreferenceActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.FrameLayout;

import uk.co.senab.actionbarpulltorefresh.library.EnvironmentDelegate;
import uk.co.senab.actionbarpulltorefresh.library.HeaderTransformer;
import uk.co.senab.actionbarpulltorefresh.library.Options;

class CustomPullToRefreshAttacher extends
        uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher {

    private FrameLayout mHeaderViewWrapper;

    protected CustomPullToRefreshAttacher(Activity activity, Options options) {
        super(activity, options);
    }

    @Override
    protected void addHeaderViewToActivity(View headerViewLayout, Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            super.addHeaderViewToActivity(headerViewLayout, activity);
        } else {
            // On older devices we need to wrap the HeaderView in a FrameLayout otherwise
            // visibility changes do not take effect
            mHeaderViewWrapper = new FrameLayout(activity);
            mHeaderViewWrapper.addView(headerViewLayout);
            super.addHeaderViewToActivity(mHeaderViewWrapper, activity);
        }
    }

    @Override
    protected void removeHeaderViewFromActivity(View headerViewLayout, Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            super.removeHeaderViewFromActivity(headerViewLayout, activity);
        } else if (mHeaderViewWrapper != null) {
            super.removeHeaderViewFromActivity(mHeaderViewWrapper, activity);
            mHeaderViewWrapper = null;
        }
    }

    @Override
    protected EnvironmentDelegate createDefaultEnvironmentDelegate() {
        return new AbsEnvironmentDelegate();
    }

    @Override
    protected HeaderTransformer createDefaultHeaderTransformer() {
        return new CustomHeaderTransformer();
    }

    public static class AbsEnvironmentDelegate implements EnvironmentDelegate {
        /**
         * @return Context which should be used for inflating the header layout
         */
        public Context getContextForInflater(Activity activity) {
            ActionBar ab = null;
            if (activity instanceof SherlockActivity) {
                ab = ((SherlockActivity) activity).getSupportActionBar();
            } else if (activity instanceof SherlockListActivity) {
                ab = ((SherlockListActivity) activity).getSupportActionBar();
            } else if (activity instanceof SherlockFragmentActivity) {
                ab = ((SherlockFragmentActivity) activity).getSupportActionBar();
            } else if (activity instanceof SherlockExpandableListActivity) {
                ab = ((SherlockExpandableListActivity) activity).getSupportActionBar();
            } else if (activity instanceof SherlockPreferenceActivity) {
                ab = ((SherlockPreferenceActivity) activity).getSupportActionBar();
            }

            Context context = null;
            if (ab != null) {
                context = ab.getThemedContext();
            }
            if (context == null) {
                context = activity;
            }
            return context;
        }
    }
}
