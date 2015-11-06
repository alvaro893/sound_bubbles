package metropolia.fi.suondbubbles.touchControllers;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup;

/**
 * Created by Vallo on 11/6/2015.
 */

/** Class for controlling touches on FixedLayout(lines)*/
public class FixedLayoutTouchController extends GestureDetector.SimpleOnGestureListener {

    private ViewGroup container;

    public FixedLayoutTouchController(ViewGroup viewGroup){
        container = viewGroup;
    }

    private String DEBUG_TAG = "FixedLayoutTouchController";

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Log.d(DEBUG_TAG, "double tapped");
        Log.d(DEBUG_TAG,container.toString());
        return true;
    }
}
