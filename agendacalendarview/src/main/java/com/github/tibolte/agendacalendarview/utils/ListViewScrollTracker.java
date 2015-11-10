package com.github.tibolte.agendacalendarview.utils;

import com.github.tibolte.agendacalendarview.agenda.AgendaListView;

import android.support.v4.util.SparseArrayCompat;
import android.view.View;

/**
 * Helper class calculating the scrolling distance in the AgendaListView.
 */
public class ListViewScrollTracker {
    private AgendaListView mListView;
    private SparseArrayCompat<Integer> mPositions;
    private SparseArrayCompat<Integer> mListViewItemHeights = new SparseArrayCompat<>();
    private int mFirstVisiblePosition;
    private int mReferencePosition = -1; // Position of the current date in the Agenda listView

    // region Constructor and Accessor(s)

    public ListViewScrollTracker(AgendaListView listView) {
        mListView = listView;
    }

    public int getReferencePosition() {
        return mReferencePosition;
    }

    // endregion

    // region Public methods

    /**
     * Call from an AbsListView.OnScrollListener to calculate the incremental offset (change in
     * scroll offset
     * since the last calculation).
     *
     * @param firstVisiblePosition First visible item position in the list.
     * @param visibleItemCount     Number of visible items in the list.
     * @return The incremental offset, or 0 if it wasn't possible to calculate the offset.
     */
    public int calculateIncrementalOffset(int firstVisiblePosition, int visibleItemCount) {
        // Remember previous positions, if any
        SparseArrayCompat<Integer> previousPositions = mPositions;

        // Store new positions
        mPositions = new SparseArrayCompat<Integer>();
        for (int i = 0; i < visibleItemCount; i++) {
            mPositions.put(firstVisiblePosition + i, mListView.getListChildAt(i).getTop());
        }

        if (previousPositions != null) {
            // Find position which exists in both mPositions and previousPositions, then return the difference
            // of the new and old Y values.
            for (int i = 0; i < previousPositions.size(); i++) {
                int previousPosition = previousPositions.keyAt(i);
                int previousTop = previousPositions.get(previousPosition);
                Integer newTop = mPositions.get(previousPosition);
                if (newTop != null) {
                    return newTop - previousTop;
                }
            }
        }

        return 0; // No view's position was in both previousPositions and mPositions
    }

    /**
     * Call from an AbsListView.OnScrollListener to calculate the scrollY (Here
     * we definite as the distance in pixels compared to the position representing the current
     * date).
     *
     * @param firstVisiblePosition First visible item position in the list.
     * @param visibleItemCount     Number of visible items in the list.
     * @return Distance in pixels compared to current day position (negative if firstVisiblePosition less than mReferencePosition)
     */
    public int calculateScrollY(int firstVisiblePosition, int visibleItemCount) {
        mFirstVisiblePosition = firstVisiblePosition;
        if (mReferencePosition < 0) {
            mReferencePosition = mFirstVisiblePosition;
        }

        if (visibleItemCount > 0) {
            View c = mListView.getListChildAt(0); // this is the first visible row
            int scrollY = -c.getTop();
            mListViewItemHeights.put(firstVisiblePosition, c.getMeasuredHeight());

            if (mFirstVisiblePosition >= mReferencePosition) {
                for (int i = mReferencePosition; i < firstVisiblePosition; ++i) {
                    if (mListViewItemHeights.get(i) == null) {
                        mListViewItemHeights.put(i, c.getMeasuredHeight());
                    }
                    scrollY += mListViewItemHeights.get(i); // add all heights of the views that are gone
                }
                return scrollY;
            } else {
                for (int i = mReferencePosition - 1; i >= firstVisiblePosition; --i) {
                    if (mListViewItemHeights.get(i) == null) {
                        mListViewItemHeights.put(i, c.getMeasuredHeight());
                    }
                    scrollY -= mListViewItemHeights.get(i);
                }
                return scrollY;
            }

        }
        return 0;
    }

    public void clear() {
        mPositions = null;
    }

    // endregion
}
