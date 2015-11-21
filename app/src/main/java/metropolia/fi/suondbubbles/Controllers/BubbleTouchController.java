package metropolia.fi.suondbubbles.Controllers;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;

import metropolia.fi.suondbubbles.views.Bubble;
import metropolia.fi.suondbubbles.views.BubbleShadowBuilder;


public class BubbleTouchController extends  GestureDetector.SimpleOnGestureListener{

    public interface BubbleDoubleTapListener {
        void onBubbleDoubleTap(Bubble bubble);
    }

    private BubbleDoubleTapListener bubbleDoubleTapListener;

    public void setBubbleDoubleTapListener(BubbleDoubleTapListener bubbleDoubleTapListener) {
        this.bubbleDoubleTapListener = bubbleDoubleTapListener;
    }

    private Context context;
    private Bubble bubble;
    private String DEBUG_TAG = "BubbleTouchController";
    private Point touchPoint;
    private String coordinates;
    private int y_coordinate;

    public BubbleTouchController(Context context, Bubble bubble){
        this.context = context;
        this.bubble = bubble;
        touchPoint = new Point();

    }

    @Override
    public boolean onDown(MotionEvent e) {

        Log.d(DEBUG_TAG,"touch at getY: " + e.getY());
        Log.d(DEBUG_TAG,"touch at getRawY: " + e.getRawY());
        return true;
    }


    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Log.d(DEBUG_TAG, "double tap on Bubble");
        bubbleDoubleTapListener.onBubbleDoubleTap(bubble);

        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.d(DEBUG_TAG, "Start dragging bubble");
        bubble.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

        y_coordinate = (int)e.getY();
        coordinates = Integer.toString(y_coordinate);
        ClipData data = ClipData.newPlainText("", coordinates);


        touchPoint.set((int)e.getX(),(int)e.getY());

        bubble.startDrag(data, new BubbleShadowBuilder(bubble,touchPoint), bubble, 0);
    }



}
