package metropolia.fi.suondbubbles.controllers;

import android.content.ClipData;
import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;


public class BubbleTouchController extends  GestureDetector.SimpleOnGestureListener {

    private Context context;
    private View view;
    private String DEBUG_TAG = "BubbleTouchController";

    public BubbleTouchController(Context context, View view){
        this.context = context;
        this.view = view;

    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Log.d(DEBUG_TAG, "double tap on Bubble");
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.d(DEBUG_TAG, "Start dragging bubble");
        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

        ClipData data = ClipData.newPlainText("", "");
        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
        view.startDrag(data, shadowBuilder, view, 0);
        view.setVisibility(View.INVISIBLE);
    }

}
