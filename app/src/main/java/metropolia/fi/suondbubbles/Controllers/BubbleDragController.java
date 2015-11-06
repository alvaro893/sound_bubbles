package metropolia.fi.suondbubbles.controllers;

import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;

import metropolia.fi.suondbubbles.layouts.FixedLayout;


public class BubbleDragController implements View.OnDragListener {

    private FixedLayout container;
    private FixedLayout.LayoutParams layoutParams;

    @Override
    public boolean onDrag(View v, DragEvent event) {
        int action = event.getAction();
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                // do nothing
                break;

            case DragEvent.ACTION_DRAG_ENTERED:
                break;

            case DragEvent.ACTION_DRAG_EXITED:
                break;

            case DragEvent.ACTION_DROP:
                // Dropped, reassign View to ViewGroup
                View view = (View) event.getLocalState();
                ViewGroup owner = (ViewGroup) view.getParent();
                owner.removeView(view);
                container = (FixedLayout)v;
                layoutParams = new FixedLayout.LayoutParams(v.getWidth(),v.getHeight(), 0, (int)(event.getY() - ((view.getHeight() / 2))));
                container.addView(view, layoutParams);
                view.setVisibility(View.VISIBLE);
                break;

            case DragEvent.ACTION_DRAG_ENDED:
                break;

            default:
                break;
        }
        return true;
    }
}

