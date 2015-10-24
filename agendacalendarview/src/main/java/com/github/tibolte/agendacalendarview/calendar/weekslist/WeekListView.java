package com.github.tibolte.agendacalendarview.calendar.weekslist;

import com.github.tibolte.agendacalendarview.utils.BusProvider;
import com.github.tibolte.agendacalendarview.utils.Events;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

public class WeekListView extends RecyclerView {
    private boolean mUserScrolling = false;
    private boolean mScrolling = false;

    // region Constructors

    public WeekListView(Context context) {
        super(context);
    }

    public WeekListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WeekListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // endregion

    // region Public methods

    /**
     * Enable snapping behaviour for this recyclerView
     *
     * @param enabled enable or disable the snapping behaviour
     */
    public void setSnapEnabled(boolean enabled) {
        if (enabled) {
            addOnScrollListener(mScrollListener);
        } else {
            removeOnScrollListener(mScrollListener);
        }
    }

    // endregion

    // region Private methods

    private OnScrollListener mScrollListener = new OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            final WeeksAdapter weeksAdapter = (WeeksAdapter) getAdapter();

            switch (newState) {
                case SCROLL_STATE_IDLE:
                    if (mUserScrolling) {
                        scrollToView(getCenterView());
                        postDelayed(() -> weeksAdapter.setDragging(false), 700); // Wait for recyclerView to settle
                    }

                    mUserScrolling = false;
                    mScrolling = false;
                    break;
                // If scroll is caused by a touch (scroll touch, not any touch)
                case SCROLL_STATE_DRAGGING:
                    BusProvider.getInstance().send(new Events.CalendarScrolledEvent());
                    // If scroll was initiated already, this is not a user scrolling, but probably a tap, else set userScrolling
                    if (!mScrolling) {
                        mUserScrolling = true;
                    }
                    weeksAdapter.setDragging(true);
                    break;
                case SCROLL_STATE_SETTLING:
                    // The user's finger is not touching the list anymore, no need
                    // for any alpha animation then
                    weeksAdapter.setAlphaSet(true);
                    mScrolling = true;
                    break;
            }
        }
    };

    private View getChildClosestToPosition(int y) {
        if (getChildCount() <= 0) {
            return null;
        }

        int itemHeight = getChildAt(0).getMeasuredHeight();

        int closestY = 9999;
        View closestChild = null;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);

            int childCenterY = ((int) child.getY() + (itemHeight / 2));
            int yDistance = childCenterY - y;

            // If child center is closer than previous closest, set it as closest
            if (Math.abs(yDistance) < Math.abs(closestY)) {
                closestY = yDistance;
                closestChild = child;
            }
        }

        return closestChild;
    }

    private View getCenterView() {
        return getChildClosestToPosition(getMeasuredHeight() / 2);
    }

    private void scrollToView(View child) {
        if (child == null) {
            return;
        }

        stopScroll();

        int scrollDistance = getScrollDistance(child);

        if (scrollDistance != 0) {
            smoothScrollBy(0, scrollDistance);
        }
    }

    private int getScrollDistance(View child) {
        int itemHeight = getChildAt(0).getMeasuredHeight();
        int centerY = getMeasuredHeight() / 2;

        int childCenterY = ((int) child.getY() + (itemHeight / 2));

        return childCenterY - centerY;
    }

    // endregion
}
