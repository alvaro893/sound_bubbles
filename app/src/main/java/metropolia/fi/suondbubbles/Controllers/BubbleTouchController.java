package metropolia.fi.suondbubbles.Controllers;

import android.content.ClipData;
import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;

import metropolia.fi.suondbubbles.views.Bubble;


public class BubbleTouchController extends  GestureDetector.SimpleOnGestureListener {

    private Context context;
    private Bubble bubble;
    private String DEBUG_TAG = "BubbleTouchController";

    public BubbleTouchController(Context context, Bubble bubble){
        this.context = context;
        this.bubble = bubble;

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
        bubble.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

        ClipData data = ClipData.newPlainText("", "");

        bubble.startDrag(data, new View.DragShadowBuilder(bubble), bubble, 0);
    }


}
