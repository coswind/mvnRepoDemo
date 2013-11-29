package com.coswind.viewpagerindicator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import uk.co.senab.actionbarpulltorefresh.library.HeaderTransformer;
import uk.co.senab.actionbarpulltorefresh.library.sdk.Compat;

/**
 * Default Header Transformer.
 */
public class CustomHeaderTransformer extends HeaderTransformer {

    public static final int PROGRESS_BAR_STYLE_INSIDE = 0;
    public static final int PROGRESS_BAR_STYLE_OUTSIDE = 1;

    private View mHeaderView;
    private ViewGroup mContentLayout;
    private TextView mHeaderTextView;
    private CustomPullRefreshProgressBar mHeaderProgressBar;

    private CharSequence mPullRefreshLabel, mRefreshingLabel, mReleaseLabel;

    private int mProgressDrawableColor;
    private float mProgressCornerRadius;

    private long mAnimationDuration;
    private int mProgressBarStyle;
    private int mProgressBarHeight = RelativeLayout.LayoutParams.WRAP_CONTENT;

    private final Interpolator mInterpolator = new AccelerateInterpolator();

    private Animation mHeaderInAnimation, mHeaderOutAnimation;

    protected CustomHeaderTransformer() {
        final int min = getMinimumApiLevel();
    }

    @Override
    public void onViewCreated(Activity activity, View headerView) {
        mHeaderView = headerView;

        // Get ProgressBar and TextView
        mHeaderProgressBar = (CustomPullRefreshProgressBar) headerView.findViewById(R.id.ptr_progress);
        mHeaderTextView = (TextView) headerView.findViewById(R.id.ptr_text);
        mContentLayout = (ViewGroup) headerView.findViewById(R.id.ptr_content);

        // Default Labels to display
        mPullRefreshLabel = activity.getString(R.string.pull_to_refresh_pull_label);
        mRefreshingLabel = activity.getString(R.string.pull_to_refresh_refreshing_label);
        mReleaseLabel = activity.getString(R.string.pull_to_refresh_release_label);

        mAnimationDuration = activity.getResources()
                .getInteger(android.R.integer.config_shortAnimTime);

        mProgressDrawableColor = activity.getResources()
                .getColor(R.color.default_progress_bar_color);

        mProgressCornerRadius = activity.getResources()
                .getDimensionPixelSize(R.dimen.default_corner_radius);

        // Setup the View styles
        setupViewsFromStyles(activity, headerView);

        applyProgressBarStyle();

        // Apply any custom ProgressBar colors and corner radius
        applyProgressBarSettings();

        // FIXME: I do not like this call here
        onReset();

        // Create animations for use later
        mHeaderInAnimation = AnimationUtils.loadAnimation(activity, R.anim.fade_in);
        mHeaderOutAnimation = AnimationUtils.loadAnimation(activity, R.anim.fade_out);

        if (mHeaderOutAnimation != null || mHeaderInAnimation != null) {
            final AnimationCallback callback = new AnimationCallback();
            if (mHeaderOutAnimation != null) {
                mHeaderOutAnimation.setAnimationListener(callback);
            }
        }
    }

    class AnimationCallback implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (animation == mHeaderOutAnimation) {
                View headerView = getHeaderView();
                if (headerView != null) {
                    headerView.setVisibility(View.GONE);
                }
                onReset();
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }

    @Override
    public void onConfigurationChanged(Activity activity, Configuration newConfig) {
        setupViewsFromStyles(activity, getHeaderView());
    }

    @Override
    public void onReset() {
        // Reset Progress Bar
        if (mHeaderProgressBar != null) {
            mHeaderProgressBar.setVisibility(View.GONE);
            mHeaderProgressBar.setProgress(0);
            mHeaderProgressBar.setIndeterminate(false);
        }

        // Reset Text View
        if (mHeaderTextView != null) {
            mHeaderTextView.setVisibility(View.VISIBLE);
            mHeaderTextView.setText(mPullRefreshLabel);
        }

        // Reset the Content Layout
        if (mContentLayout != null) {
            mContentLayout.setVisibility(View.VISIBLE);
            Compat.setAlpha(mContentLayout, 1f);
        }
    }

    @Override
    public void onPulled(float percentagePulled) {
        if (mHeaderProgressBar != null) {
            mHeaderProgressBar.setVisibility(View.VISIBLE);
            final float progress = mInterpolator.getInterpolation(percentagePulled);
            mHeaderProgressBar.setProgress(Math.round(mHeaderProgressBar.getMax() * progress));
        }
    }

    @Override
    public void onRefreshStarted() {
        if (mHeaderTextView != null) {
            mHeaderTextView.setText(mRefreshingLabel);
        }
        if (mHeaderProgressBar != null) {
            mHeaderProgressBar.setVisibility(View.VISIBLE);
            mHeaderProgressBar.setIndeterminate(true);
        }
    }

    @Override
    public void onReleaseToRefresh() {
        if (mHeaderTextView != null) {
            mHeaderTextView.setText(mReleaseLabel);
        }
        if (mHeaderProgressBar != null) {
            mHeaderProgressBar.setProgress(mHeaderProgressBar.getMax());
        }
    }

    @Override
    public void onRefreshMinimized() {
        // Super handles ICS+ anyway...
        if (Build.VERSION.SDK_INT >= getMinimumApiLevel()) {
            superOnRefreshMinimized();
            return;
        }

        // Here we fade out most of the header, leaving just the progress bar
        View contentLayout = getHeaderView().findViewById(R.id.ptr_content);
        if (contentLayout != null) {
            contentLayout.startAnimation(AnimationUtils
                    .loadAnimation(contentLayout.getContext(), R.anim.fade_out));
            contentLayout.setVisibility(View.INVISIBLE);
        }
    }

    private void superOnRefreshMinimized() {
        // Here we fade out most of the header, leaving just the progress bar
        if (mContentLayout != null) {
            ObjectAnimator.ofFloat(mContentLayout, "alpha", 1f, 0f).start();
        }
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    @Override
    public boolean showHeaderView() {
        // Super handles ICS+ anyway...
        if (Build.VERSION.SDK_INT >= getMinimumApiLevel()) {
            return superShowHeaderView();
        }

        final View headerView = getHeaderView();
        final boolean changeVis = headerView != null && headerView.getVisibility() != View.VISIBLE;
        if (changeVis) {
            // Show Header
            if (mHeaderInAnimation != null) {
                // AnimationListener will call HeaderViewListener
                headerView.startAnimation(mHeaderInAnimation);
            }
            headerView.setVisibility(View.VISIBLE);
        }
        return changeVis;
    }

    private boolean superShowHeaderView() {
        final boolean changeVis = mHeaderView.getVisibility() != View.VISIBLE;

        if (changeVis) {
            mHeaderView.setVisibility(View.VISIBLE);
            AnimatorSet animSet = new AnimatorSet();
            ObjectAnimator transAnim = ObjectAnimator.ofFloat(mContentLayout, "translationY",
                    -mContentLayout.getHeight(), 0f);
            ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(mHeaderView, "alpha", 0f, 1f);
            animSet.playTogether(transAnim, alphaAnim);
            animSet.setDuration(mAnimationDuration);
            animSet.start();
        }

        return changeVis;
    }

    @Override
    public boolean hideHeaderView() {
        // Super handles ICS+ anyway...
        if (Build.VERSION.SDK_INT >= getMinimumApiLevel()) {
            return superHideHeaderView();
        }

        final View headerView = getHeaderView();
        final boolean changeVis = headerView != null && headerView.getVisibility() != View.GONE;
        if (changeVis) {
            // Hide Header
            if (mHeaderOutAnimation != null) {
                // AnimationListener will call HeaderTransformer and
                // HeaderViewListener
                headerView.startAnimation(mHeaderOutAnimation);
            } else {
                // As we're not animating, hide the header + call the header
                // transformer now
                headerView.setVisibility(View.GONE);
                onReset();
            }
        }
        return changeVis;
    }

    @SuppressLint("NewApi")
    private boolean superHideHeaderView() {
        final boolean changeVis = mHeaderView.getVisibility() != View.GONE;

        if (changeVis) {
            Animator animator;
            if (mContentLayout.getAlpha() >= 0.5f) {
                // If the content layout is showing, translate and fade out
                animator = new AnimatorSet();
                ObjectAnimator transAnim = ObjectAnimator.ofFloat(mContentLayout, "translationY",
                        0f, -mContentLayout.getHeight());
                ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(mHeaderView, "alpha", 1f, 0f);
                ((AnimatorSet) animator).playTogether(transAnim, alphaAnim);
            } else {
                // If the content layout isn't showing (minimized), just fade out
                animator = ObjectAnimator.ofFloat(mHeaderView, "alpha", 1f, 0f);
            }
            animator.setDuration(mAnimationDuration);
            animator.addListener(new HideAnimationCallback());
            animator.start();
        }

        return changeVis;
    }

    /**
     * Set color to apply to the progress bar.
     * <p/>
     * The best way to apply a color is to load the color from resources: {@code
     * setProgressBarColor(getResources().getColor(R.color.your_color_name))}.
     *
     * @param color The color to use.
     */
    public void setProgressBarColor(int color) {
        mProgressDrawableColor = color;
        applyProgressBarSettings();
    }

    /**
     * Set the rounded corner radius.
     *
     * @param radiusPx
     */
    public void setProgressBarCornerRadius(float radiusPx) {
        mProgressCornerRadius = Math.max(radiusPx, 0f);
        applyProgressBarSettings();
    }

    /**
     * Set the progress bar style. {@code style} must be one of {@link #PROGRESS_BAR_STYLE_OUTSIDE}
     * or {@link #PROGRESS_BAR_STYLE_INSIDE}.
     */
    public void setProgressBarStyle(int style) {
        if (mProgressBarStyle != style) {
            mProgressBarStyle = style;
            applyProgressBarStyle();
        }
    }

    /**
     * Set the progress bar height.
     */
    public void setProgressBarHeight(int height) {
        if (mProgressBarHeight != height) {
            mProgressBarHeight = height;
            applyProgressBarStyle();
        }
    }

    /**
     * Set Text to show to prompt the user is pull (or keep pulling).
     *
     * @param pullText - Text to display.
     */
    public void setPullText(CharSequence pullText) {
        mPullRefreshLabel = pullText;
        if (mHeaderTextView != null) {
            mHeaderTextView.setText(mPullRefreshLabel);
        }
    }

    /**
     * Set Text to show to tell the user that a refresh is currently in progress.
     *
     * @param refreshingText - Text to display.
     */
    public void setRefreshingText(CharSequence refreshingText) {
        mRefreshingLabel = refreshingText;
    }

    /**
     * Set Text to show to tell the user has scrolled enough to refresh.
     *
     * @param releaseText - Text to display.
     */
    public void setReleaseText(CharSequence releaseText) {
        mReleaseLabel = releaseText;
    }

    private void setupViewsFromStyles(Activity activity, View headerView) {
        final TypedArray styleAttrs = obtainStyledAttrsFromThemeAttr(activity,
                R.attr.ptrHeaderStyle, R.styleable.PullToRefreshHeader);

        // Retrieve the Action Bar size from the app theme or the Action Bar's style
        if (mContentLayout != null) {
            final int height = styleAttrs.getDimensionPixelSize(
                    R.styleable.PullToRefreshHeader_ptrHeaderHeight, getActionBarSize(activity));
            mContentLayout.getLayoutParams().height = height;
            mContentLayout.requestLayout();
        }

        // Retrieve the Action Bar background from the app theme or the Action Bar's style (see #93)
        Drawable bg = styleAttrs.hasValue(R.styleable.PullToRefreshHeader_ptrHeaderBackground)
                ? styleAttrs.getDrawable(R.styleable.PullToRefreshHeader_ptrHeaderBackground)
                : getActionBarBackground(activity);
        if (bg != null) {
            mHeaderTextView.setBackgroundDrawable(bg);

            // If we have an opaque background we can remove the background from the content layout
            if (mContentLayout != null && bg.getOpacity() == PixelFormat.OPAQUE) {
                mContentLayout.setBackgroundResource(0);
            }
        }

        // Retrieve the Action Bar Title Style from the app theme or the Action Bar's style
        Context abContext = headerView.getContext();
        final int titleTextStyle = styleAttrs
                .getResourceId(R.styleable.PullToRefreshHeader_ptrHeaderTitleTextAppearance,
                        getActionBarTitleStyle(abContext));
        if (titleTextStyle != 0) {
            mHeaderTextView.setTextAppearance(abContext, titleTextStyle);
        }

        // Retrieve the Progress Bar Color the style
        if (styleAttrs.hasValue(R.styleable.PullToRefreshHeader_ptrProgressBarColor)) {
            mProgressDrawableColor = styleAttrs.getColor(
                    R.styleable.PullToRefreshHeader_ptrProgressBarColor, mProgressDrawableColor);
        }

        if (styleAttrs.hasValue(R.styleable.PullToRefreshHeader_ptrProgressBarCornerRadius)) {
            mProgressCornerRadius = (float) styleAttrs.getDimensionPixelSize(
                    R.styleable.PullToRefreshHeader_ptrProgressBarCornerRadius, 0);
        }

        mProgressBarStyle = styleAttrs.getInt(
                R.styleable.PullToRefreshHeader_ptrProgressBarStyle, PROGRESS_BAR_STYLE_OUTSIDE);

        if (styleAttrs.hasValue(R.styleable.PullToRefreshHeader_ptrProgressBarHeight)) {
            mProgressBarHeight = styleAttrs.getDimensionPixelSize(
                    R.styleable.PullToRefreshHeader_ptrProgressBarHeight, mProgressBarHeight);
        }

        // Retrieve the text strings from the style (if they're set)
        if (styleAttrs.hasValue(R.styleable.PullToRefreshHeader_ptrPullText)) {
            mPullRefreshLabel = styleAttrs.getString(R.styleable.PullToRefreshHeader_ptrPullText);
        }
        if (styleAttrs.hasValue(R.styleable.PullToRefreshHeader_ptrRefreshingText)) {
            mRefreshingLabel = styleAttrs
                    .getString(R.styleable.PullToRefreshHeader_ptrRefreshingText);
        }
        if (styleAttrs.hasValue(R.styleable.PullToRefreshHeader_ptrReleaseText)) {
            mReleaseLabel = styleAttrs.getString(R.styleable.PullToRefreshHeader_ptrReleaseText);
        }

        styleAttrs.recycle();
    }

    private void applyProgressBarStyle() {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, mProgressBarHeight);

        switch (mProgressBarStyle) {
            case PROGRESS_BAR_STYLE_INSIDE:
                lp.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.ptr_content);
                break;
            case PROGRESS_BAR_STYLE_OUTSIDE:
                lp.addRule(RelativeLayout.BELOW, R.id.ptr_content);
                break;
        }

        mHeaderProgressBar.setLayoutParams(lp);
    }

    private void applyProgressBarSettings() {
        if (mHeaderProgressBar != null) {
            mHeaderProgressBar.setProgressBarColor(mProgressDrawableColor);
            mHeaderProgressBar.setProgressBarCornerRadius(mProgressCornerRadius);
        }
    }

    protected Drawable getActionBarBackground(Context context) {
        // Super handles ICS+ anyway...
        if (Build.VERSION.SDK_INT >= getMinimumApiLevel()) {
            return superGetActionBarBackground(context);
        }

        // Get action bar style values...
        TypedArray abStyle = obtainStyledAttrsFromThemeAttr(context, R.attr.actionBarStyle,
                R.styleable.SherlockActionBar);
        try {
            return abStyle.getDrawable(R.styleable.SherlockActionBar_background);
        } finally {
            abStyle.recycle();
        }
    }

    private Drawable superGetActionBarBackground(Context context) {
        int[] android_styleable_ActionBar = {android.R.attr.background};

        // Now get the action bar style values...
        TypedArray abStyle = obtainStyledAttrsFromThemeAttr(context, android.R.attr.actionBarStyle,
                android_styleable_ActionBar);
        try {
            // background is the first attr in the array above so it's index is 0.
            return abStyle.getDrawable(0);
        } finally {
            abStyle.recycle();
        }
    }

    protected int getActionBarSize(Context context) {
        // Super handles ICS+ anyway...
        if (Build.VERSION.SDK_INT >= getMinimumApiLevel()) {
            return superGetActionBarSize(context);
        }

        TypedArray values = context.obtainStyledAttributes(R.styleable.SherlockTheme);
        try {
            return values.getDimensionPixelSize(R.styleable.SherlockTheme_actionBarSize, 0);
        } finally {
            values.recycle();
        }
    }

    private int superGetActionBarSize(Context context) {
        int[] attrs = {android.R.attr.actionBarSize};
        TypedArray values = context.getTheme().obtainStyledAttributes(attrs);
        try {
            return values.getDimensionPixelSize(0, 0);
        } finally {
            values.recycle();
        }
    }

    protected int getActionBarTitleStyle(Context context) {
        // Super handles ICS+ anyway...
        if (Build.VERSION.SDK_INT >= getMinimumApiLevel()) {
            return superGetActionBarTitleStyle(context);
        }

        // Get action bar style values...
        TypedArray abStyle = obtainStyledAttrsFromThemeAttr(context, R.attr.actionBarStyle,
                R.styleable.SherlockActionBar);
        try {
            return abStyle.getResourceId(R.styleable.SherlockActionBar_titleTextStyle, 0);
        } finally {
            abStyle.recycle();
        }
    }

    private int superGetActionBarTitleStyle(Context context) {
        int[] android_styleable_ActionBar = {android.R.attr.titleTextStyle};

        // Now get the action bar style values...
        TypedArray abStyle = obtainStyledAttrsFromThemeAttr(context, android.R.attr.actionBarStyle,
                android_styleable_ActionBar);
        try {
            // titleTextStyle is the first attr in the array above so it's index is 0.
            return abStyle.getResourceId(0, 0);
        } finally {
            abStyle.recycle();
        }
    }

    protected int getMinimumApiLevel() {
        return Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    class HideAnimationCallback extends AnimatorListenerAdapter {
        @Override
        public void onAnimationEnd(Animator animation) {
            View headerView = getHeaderView();
            if (headerView != null) {
                headerView.setVisibility(View.GONE);
            }
            onReset();
        }
    }

    protected static TypedArray obtainStyledAttrsFromThemeAttr(Context context, int themeAttr,
                                                               int[] styleAttrs) {
        // Need to get resource id of style pointed to from the theme attr
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(themeAttr, outValue, true);
        final int styleResId = outValue.resourceId;

        // Now return the values (from styleAttrs) from the style
        return context.obtainStyledAttributes(styleResId, styleAttrs);
    }
}
