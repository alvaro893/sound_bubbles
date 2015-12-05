package metropolia.fi.suondbubbles.bubble;


public class BubblePosition {

    private float yCoordinate;
    private int parentLayoutID;

    public BubblePosition(){
        init();
    }

    private void init() {
        yCoordinate = 0;
        parentLayoutID = 0;
    }

    public float getyCoordinate() {
        return yCoordinate;
    }

    public void setyCoordinate(float yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    public int getParentLayoutID() {
        return parentLayoutID;
    }

    public void setParentLayoutID(int parentLayoutID) {
        this.parentLayoutID = parentLayoutID;
    }
}
