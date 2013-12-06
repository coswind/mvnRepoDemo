package com.coswind.viewpagerindicator;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import uk.co.senab.actionbarpulltorefresh.library.widget.PullToRefreshProgressBar;

/**
 * Created by coswind on 13-12-5.
 */
public class PullRefreshProgressBar extends PullToRefreshProgressBar {
    private int[] mProgressBarColors;
    private int mProgressIndex;

    public PullRefreshProgressBar(Context c) {
        super(c, null);
    }

    public PullRefreshProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        mProgressBarColors = getResources().getIntArray(R.array.rainbow);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        //Set Duration Here.
        //mIndeterminateAnimator.setDuration(600);
    }

    @Override
    public void drawIndeterminate(Canvas canvas) {
        if (!mIndeterminateAnimator.isStarted()) {
            return;
        }

        final float animProgress = mIndeterminateAnimator.getAnimatedValue();
        final float barWidth = canvas.getWidth() / (float) mSegmentCount;

        for (int i = -1; i < mSegmentCount; i++) {
            final float l = (i + animProgress) * barWidth;
            final float r = l + barWidth - mIndeterminateBarSpacing;

            mPaint.setColor(mProgressBarColors[(i + 1 + mProgressIndex) % (mSegmentCount + 1)]);

            mDrawRect.set(l, 0f, r, canvas.getHeight());
            canvas.drawRect(mDrawRect, mPaint);
        }

        if (animProgress >= 1) {
            mProgressIndex = (mProgressIndex + mSegmentCount) % (mSegmentCount + 1);
        }
    }
}
