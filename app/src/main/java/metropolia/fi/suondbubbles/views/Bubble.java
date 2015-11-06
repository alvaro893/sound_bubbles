package metropolia.fi.suondbubbles.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import java.util.Random;

/**
 * Created by Vallo on 11/6/2015.
 */
public class Bubble extends View {

    private Paint color;

    //bubble height must be in int
    private int height;
    private String DEBUG_TAG = "Bubble class";
    private RectF rectCoordinates;


    public Bubble(Context context, int height) {
        super(context);
        this.height = height;
        createRoundedRectangle();
    }


    private void createRoundedRectangle() {
        initStyle();
        rectCoordinates = new RectF(0,0,0,height);
    }
    
    private void initStyle(){
        Random rnd = new Random();
        color = new Paint(Paint.ANTI_ALIAS_FLAG);
        color.setARGB(124, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        rectCoordinates.right = canvas.getWidth();
        canvas.drawRoundRect(rectCoordinates,100,100,color);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec),MeasureSpec.EXACTLY);
        int heightSpec = MeasureSpec.makeMeasureSpec(height,MeasureSpec.EXACTLY);

        setMeasuredDimension(widthSpec,heightSpec);
    }

}
