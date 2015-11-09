package metropolia.fi.suondbubbles.layouts;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import metropolia.fi.suondbubbles.Controllers.FixedLayoutTouchController;


public class FixedLayout extends ViewGroup {

    GestureDetector mDetector;
    FixedLayoutTouchController controller;


    private String DEBUG_TAG = "FixedLayout";

    public FixedLayout(Context context) {
        super(context);
        init();
    }

    public FixedLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();

    }

    private void init(){
        controller = new FixedLayoutTouchController(getContext(),(ViewGroup)getRootView());
        mDetector = new GestureDetector(getContext(), controller);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int width = 0;
        int height = 0;

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                FixedLayout.LayoutParams lp = (FixedLayout.LayoutParams) child.getLayoutParams();
                int right = lp.x + child.getMeasuredWidth();
                int bottom = lp.y + child.getMeasuredHeight();
                width = Math.max(width, right);
                height = Math.max(height, bottom);
            }
        }

        height = Math.max(height, getSuggestedMinimumHeight());
        width = Math.max(width, getSuggestedMinimumWidth());
        width = resolveSize(width, widthMeasureSpec);
        height = resolveSize(height, heightMeasureSpec);
        setMeasuredDimension(width, height);

    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0, 0);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                FixedLayout.LayoutParams lp = (FixedLayout.LayoutParams) child.getLayoutParams();
                child.layout(lp.x, lp.y, lp.x + child.getMeasuredWidth(), lp.y + child.getMeasuredHeight());
            }
        }
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof FixedLayout.LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {

        public int x;
        public int y;

        public LayoutParams(int width, int height, int x, int y) {
            super(width, height);
            this.x = x;
            this.y = y;
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

    }


}
