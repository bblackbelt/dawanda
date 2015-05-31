package de.dawanda.dawandaclient.widgets;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.widget.AbsListView;

/**
 * Created by emanuele on 30.05.15.
 */
public class DaWandaSwipeRefreshLayout extends SwipeRefreshLayout {

    private AbsListView mTargetView;

    public DaWandaSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean canChildScrollUp() {
        if (getChildCount() == 0) {
            return true;
        }
        if (mTargetView == null) {
            if (!(getChildAt(0) instanceof AbsListView)) {
                return true;
            }
            mTargetView = (AbsListView) getChildAt(0);
        }
        if (mTargetView.getChildCount() > 0 && mTargetView.getFirstVisiblePosition() == 0) {
            return mTargetView.getChildAt(0).getTop() > 0;
        }
        return true;
    }
}
