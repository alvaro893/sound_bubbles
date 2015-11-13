package metropolia.fi.suondbubbles.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

import metropolia.fi.suondbubbles.Controllers.BubbleTouchController;
import metropolia.fi.suondbubbles.R;
import metropolia.fi.suondbubbles.helper.PixelsConverter;


public class Bubble extends View {



    private Paint color, passive_color, active_color;

    private GestureDetector mDetector;
    private int height;     //bubble height must be in int
    private int color_selection;
    private TypedArray passive_colors, active_colors;


    private String DEBUG_TAG = "Bubble class";
    private RectF rectCoordinates;


    public Bubble(Context context, int height) {
        super(context);
        init(height);
        createRoundedRectangle();
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

    private void init(int height){
        this.height = height;
        this.mDetector = new GestureDetector(getContext(),new BubbleTouchController(getContext(),this));
    }

    private void createRoundedRectangle() {
        initStyle();
        rectCoordinates = new RectF(0,0,0,height);
    }
    
    private void initStyle(){
        Random rnd = new Random();
        color_selection = rnd.nextInt(8);

        active_colors = getContext().getResources().obtainTypedArray(R.array.active_colors);
        passive_colors = getContext().getResources().obtainTypedArray(R.array.passive_colors);

        active_color = new Paint(Paint.ANTI_ALIAS_FLAG);
        active_color.setColor(active_colors.getColor(color_selection,0));

        passive_color = new Paint(Paint.ANTI_ALIAS_FLAG);
        passive_color.setColor(passive_colors.getColor(color_selection,0));

        setColor(passive_color);

    }




    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        rectCoordinates.right = canvas.getWidth();
        canvas.drawRoundRect(rectCoordinates, PixelsConverter.convertPixelsToDp(60,getContext()),PixelsConverter.convertPixelsToDp(60,getContext()),color);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec),MeasureSpec.EXACTLY);
        int heightSpec = MeasureSpec.makeMeasureSpec(height,MeasureSpec.EXACTLY);

        setMeasuredDimension(widthSpec,heightSpec);
    }

}
