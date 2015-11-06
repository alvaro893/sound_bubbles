package metropolia.fi.suondbubbles.controllers;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup;

import metropolia.fi.suondbubbles.layouts.FixedLayout;
import metropolia.fi.suondbubbles.views.Bubble;


/** Class for controlling touches on FixedLayout(lines)*/
public class FixedLayoutTouchController extends GestureDetector.SimpleOnGestureListener {


    private ViewGroup container;
    private Context context;
    private Bubble bubble;
    private FixedLayout.LayoutParams layoutParams;

    public FixedLayoutTouchController(Context context, ViewGroup viewGroup){
        this.container = viewGroup;
        this.context = context;
    }

    private String DEBUG_TAG = "FixedLayoutTouchController";

    @Override
    public boolean onDown(MotionEvent e) {

        return true;
    }


    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Log.d(DEBUG_TAG, "double tapped");

        /* TODO placeholder for starting SearchActivity with result*/


        bubble = new Bubble(context,400);
        layoutParams = new FixedLayout.LayoutParams(container.getWidth(),bubble.getHeight(),0, (int)e.getY());
        container.addView(bubble, layoutParams);


        return true;
    }
}
