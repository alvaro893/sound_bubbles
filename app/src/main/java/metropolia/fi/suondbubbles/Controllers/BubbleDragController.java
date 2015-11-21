package metropolia.fi.suondbubbles.Controllers;

import android.content.ClipData;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import metropolia.fi.suondbubbles.R;
import metropolia.fi.suondbubbles.layouts.FixedLayout;
import metropolia.fi.suondbubbles.views.Bubble;


public class BubbleDragController implements View.OnDragListener {

    public interface BubbleRemoveListener{
        void bubbleRemoved(Bubble bubble);
    }

    private FixedLayout container;
    private FixedLayout.LayoutParams layoutParams;
    private String DEBUG_TAG = "dragController";
    private Bubble bubbleView;
    private ImageView removeView;
    private int droptopY = 0;
    private BubbleRemoveListener bubbleRemoveListener;
    private ClipData data;
    private String coord;
    private int yCoord;


    public void setBubbleRemoveListener(BubbleRemoveListener bubbleRemoveListener) {
        this.bubbleRemoveListener = bubbleRemoveListener;
    }

    public void setRemoveView(ImageView removeView){
        this.removeView = removeView;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        int action = event.getAction();

        switch (event.getAction()) {


            case DragEvent.ACTION_DRAG_STARTED:
                Log.d(DEBUG_TAG,"ACTION_DRAG_STARTED");
                bubbleView = (Bubble)event.getLocalState();
                bubbleView.setVisibility(View.INVISIBLE);
                removeView.setAlpha(1f);
                break;

            case DragEvent.ACTION_DRAG_ENTERED:
                switch (v.getId()){
                    case R.id.remove_view:
                        removeView.setSelected(true);
                        Log.d(DEBUG_TAG,"Entered remove_view");
                        break;
                    case R.id.fixedLaytout_1:
                        Log.d(DEBUG_TAG,"Entered fixedLaytout_1");
                        break;
                    case R.id.fixedLaytout_2:
                        Log.d(DEBUG_TAG,"Entered fixedLaytout_2");
                        break;
                    case R.id.fixedLaytout_3:
                        Log.d(DEBUG_TAG,"Entered fixedLaytout_3");
                        break;
                    case R.id.fixedLaytout_4:
                        Log.d(DEBUG_TAG,"Entered fixedLaytout_4");
                        break;

                    case R.id.fixedLaytout_5:
                        Log.d(DEBUG_TAG,"Entered fixedLaytout_5");
                        break;

                    case R.id.fixedLaytout_6:
                        Log.d(DEBUG_TAG,"Entered fixedLaytout_6");
                        break;

                    default:
                        break;
                }
                break;


            case DragEvent.ACTION_DRAG_EXITED:
                switch (v.getId()) {
                    case R.id.remove_view:
                        removeView.setSelected(false);
                        break;
                }
                break;

            case DragEvent.ACTION_DROP:
                Log.d(DEBUG_TAG,"drop event accured");
                // Dropped, reassign View to ViewGroup
                bubbleView = (Bubble) event.getLocalState();
                ViewGroup owner = (ViewGroup) bubbleView.getParent();


                if(v.getId() == R.id.remove_view){
                    bubbleView.stopPlaying();
                    owner.removeView(bubbleView);
                    bubbleView.setDetected(false);
                    bubbleRemoveListener.bubbleRemoved(bubbleView);
                    removeView.setSelected(false);

                } else{
                    owner.removeView(bubbleView);
                    container = (FixedLayout) v;
                    data = event.getClipData();
                    Log.d(DEBUG_TAG,"data.getItemCount() is " + data.getItemCount());

                    coord = data.getItemAt(0).getText().toString();
                    Log.d(DEBUG_TAG, " coord is " + coord);
                     yCoord = Integer.parseInt(coord);



                    droptopY = (int)(event.getY() -  yCoord);
                    layoutParams = new FixedLayout.LayoutParams(v.getWidth(), v.getHeight(), 0, bubbleView.returnFittingYcoordinate(container.getBottom(), droptopY));
                    container.addView(bubbleView, layoutParams);
                    bubbleView.setVisibility(View.VISIBLE);
                }
                break;

            case DragEvent.ACTION_DRAG_ENDED:


                bubbleView = (Bubble) event.getLocalState();


                if(!bubbleView.isShown()){
                    bubbleView.setVisibility(View.VISIBLE);

                }

                removeView.setAlpha(0f);

                break;


            default:
                break;
        }
        return true;
    }
}

