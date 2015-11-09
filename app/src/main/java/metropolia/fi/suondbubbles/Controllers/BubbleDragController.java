package metropolia.fi.suondbubbles.Controllers;

import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;

import metropolia.fi.suondbubbles.R;
import metropolia.fi.suondbubbles.layouts.FixedLayout;


public class BubbleDragController implements View.OnDragListener {

    private FixedLayout container;
    private FixedLayout.LayoutParams layoutParams;
    private String DEBUG_TAG = "dragController";

    @Override
    public boolean onDrag(View v, DragEvent event) {
        int action = event.getAction();
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                // do nothing
                break;

            case DragEvent.ACTION_DRAG_ENTERED:

                Log.d(DEBUG_TAG,"Entered bound:" + Integer.toString(v.getId()));
                break;

            case DragEvent.ACTION_DRAG_EXITED:
                break;

            case DragEvent.ACTION_DROP:
                // Dropped, reassign View to ViewGroup
                View view = (View) event.getLocalState();
                ViewGroup owner = (ViewGroup) view.getParent();


                if(v.getId() == R.id.remove_view){
                    view = null;
                    owner.removeView(view);

                } else{
                    owner.removeView(view);
                    container = (FixedLayout) v;
                    layoutParams = new FixedLayout.LayoutParams(v.getWidth(), v.getHeight(), 0, (int)(event.getY() - ((view.getHeight() / 2))));
                    container.addView(view, layoutParams);
                    view.setVisibility(View.VISIBLE);
                }
                break;

            case DragEvent.ACTION_DRAG_ENDED:
                break;

            default:
                break;
        }
        return true;
    }
}

