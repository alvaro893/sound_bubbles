package metropolia.fi.suondbubbles.bubble;


public class BubblePosition {

    private float yCoordinate;
    private int doubleTappedLayoutIndex;

    public BubblePosition(){
        init();
    }

    private void init() {
        yCoordinate = 0;
        doubleTappedLayoutIndex = -1;
    }

    public float getyCoordinate() {
        return yCoordinate;
    }

    public void setyCoordinate(float yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    public int getDoubleTappedLayoutIndex() {
        return doubleTappedLayoutIndex;
    }

    public void setDoubleTappedLayoutIndex(int doubleTappedLayoutIndex) {
        this.doubleTappedLayoutIndex = doubleTappedLayoutIndex;
    }
}
