package metropolia.fi.suondbubbles.Controllers;


import android.view.View;

public class GridTouchController {

    private View currentTouchedView;
    private View previousTouchedView;

    private int currentTouchedViewIndex;
    private int previousTouchedViewIndex;

    public GridTouchController(){
        init();
    }

    private void init() {
        currentTouchedView = null;
        previousTouchedView = null;
        currentTouchedViewIndex = -1;
        previousTouchedViewIndex = -1;
    }

    public void setTouchedView(View currentView, int currentViewIndex){
        this.currentTouchedView = currentView;
        this.currentTouchedViewIndex = currentViewIndex;

    }

    public boolean isTouchedViewSame(){
        return currentTouchedViewIndex == previousTouchedViewIndex;
    }

    public void adjustPreviousTouchedView(){
        previousTouchedView = currentTouchedView;
        previousTouchedViewIndex = currentTouchedViewIndex;
    }

    public View getCurrentTouchedView() {
        return currentTouchedView;
    }

    public int getCurrentTouchedViewIndex() {
        return currentTouchedViewIndex;
    }

    public View getPreviousTouchedView(){
        return previousTouchedView;
    }

    public void resetAll(){
        init();
    }
}
