package com.coswind.viewpagerindicator;

/**
 * Created by coswind on 11/29/13.
 */
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;

import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

/**
 * @see uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout
 */
public class CustomPullToRefreshLayout extends
        uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout {

    public CustomPullToRefreshLayout(Context context) {
        super(context);
    }

    public CustomPullToRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomPullToRefreshLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected PullToRefreshAttacher createPullToRefreshAttacher(Activity activity,
                                                                Options options) {
        return new CustomPullToRefreshAttacher(activity, options);
    }
}
