package metropolia.fi.suondbubbles.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

import metropolia.fi.suondbubbles.Controllers.BubbleTouchController;
import metropolia.fi.suondbubbles.R;
import metropolia.fi.suondbubbles.apiConnection.ServerFile;
import metropolia.fi.suondbubbles.helper.PixelsConverter;


public class Bubble extends View {



    private Paint color, passive_color, active_color;

    private GestureDetector mDetector;



    private int bubbleHeight;
    private int color_selection;
    private int bubbleBottomY;
    private int finalfittingYcoordinate = 0;
    private TypedArray passive_colors, active_colors;
    private ServerFile serverFile;
    private String DEBUG_TAG = "Bubble class";
    private RectF rectCoordinates;

    public Bubble(Context context, ServerFile serverFile) {
        super(context);
        init(serverFile);
        createRoundedRectangle();
    }

    public int getBubbleBottomY() {
        return bubbleBottomY;
    }

    public void setBubbleBottomY(int bubbleBottomY) {
        this.bubbleBottomY = bubbleBottomY;
    }

    public int getBubbleHeight() {
        return bubbleHeight;
    }

    public Paint getPassive_color() {
        return passive_color;
    }

    public Paint getActive_color() {
        return active_color;
    }

    public void setColor(Paint color) {
        this.color = color;
    }

    private void init(ServerFile serverFile){
        this.serverFile = serverFile;
        this.bubbleHeight = (int)PixelsConverter.convertDpToPixel(serverFile.getLenght() * 50,getContext());
        this.mDetector = new GestureDetector(getContext(),new BubbleTouchController(getContext(),this));
    }

    private void createRoundedRectangle() {
        initStyle();
        rectCoordinates = new RectF(0,0,0, bubbleHeight);
    }
    
    private void initStyle(){
        Random rnd = new Random();
        color_selection = rnd.nextInt(8);

        active_colors = getContext().getResources().obtainTypedArray(R.array.active_colors);
        passive_colors = getContext().getResources().obtainTypedArray(R.array.passive_colors);

        active_color = new Paint(Paint.ANTI_ALIAS_FLAG);
        active_color.setColor(active_colors.getColor(color_selection,0));

        passive_color = new Paint(Paint.ANTI_ALIAS_FLAG);
        passive_color.setColor(passive_colors.getColor(color_selection, 0));

        setColor(passive_color);

    }

    public int returnFittingYcoordinate(int containerBottomY, int parentYCoordinates){
        if (bubbleHeight + parentYCoordinates <= containerBottomY)
            finalfittingYcoordinate = parentYCoordinates;
        else
            finalfittingYcoordinate = containerBottomY - bubbleHeight;

        setBubbleBottomY(finalfittingYcoordinate + bubbleHeight);

        Log.d(DEBUG_TAG, "containerBottomY is " + containerBottomY);
        Log.d(DEBUG_TAG,"BubbleHeight is " + bubbleHeight);
        Log.d(DEBUG_TAG, "parentYCoordinates is " + parentYCoordinates);
        Log.d(DEBUG_TAG, "finalfittingYcoordinate is " + finalfittingYcoordinate);
        Log.d(DEBUG_TAG, "bubblebottomY is " + getBubbleBottomY());

        return finalfittingYcoordinate;
    }




    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        rectCoordinates.right = canvas.getWidth();
        canvas.drawRoundRect(rectCoordinates, PixelsConverter.convertDpToPixel(100,getContext()),PixelsConverter.convertDpToPixel(100,getContext()),color);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec),MeasureSpec.EXACTLY);
        int heightSpec = MeasureSpec.makeMeasureSpec(bubbleHeight,MeasureSpec.EXACTLY);

        setMeasuredDimension(widthSpec,heightSpec);
    }

}
