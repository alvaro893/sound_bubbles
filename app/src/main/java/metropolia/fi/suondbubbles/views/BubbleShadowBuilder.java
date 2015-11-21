package metropolia.fi.suondbubbles.views;


import android.graphics.Point;
import android.util.Log;
import android.view.View;

public class BubbleShadowBuilder extends View.DragShadowBuilder{

    private final String DEBUG_TAG = "BubbleShadowBuilder";
    private View view;
    private Point point;

    public BubbleShadowBuilder(View view, Point point){
        super(view);
        this.view = view;
        this.point = point;
    }

    @Override
    public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
        if (view != null) {
            shadowSize.set(view.getWidth(), view.getHeight());
            shadowTouchPoint.set(shadowSize.x / 2, point.y);
        } else {
            Log.e(DEBUG_TAG, "Asked for drag thumb metrics but no view");
        }
    }
}
