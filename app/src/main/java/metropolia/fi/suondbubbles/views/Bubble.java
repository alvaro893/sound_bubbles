package metropolia.fi.suondbubbles.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.io.IOException;
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
    private int fitMargin = 0;

    private int alpha = (int)(0.8 * 255);
    private TypedArray passive_colors, active_colors;
    private ServerFile serverFile;
    private String DEBUG_TAG = "Bubble class";
    private RectF rectCoordinates;
    private MediaPlayer mediaPlayer;

    private boolean detected = false;
    private boolean active = false;

    public Bubble(Context context, ServerFile serverFile) {
        super(context);
        init(serverFile);
        initHeight();
        initMediaplayer();

        createRoundedRectangle();
    }

    private void initHeight() {
        MediaPlayer duration = new MediaPlayer();
        try {
            duration.setDataSource(serverFile.getPathLocalFile());
            duration.prepare();
            setBubbleHeight(duration.getDuration());
            duration.release();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /** Initializes mediaplayer and set listerners **/
    private void initMediaplayer() {
        try {

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(serverFile.getPathLocalFile());

            //setBubbleHeight(mediaPlayer.getDuration());
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    mp.start();

                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.stop();
                }
            });
        }catch (Exception e){
            Log.e(DEBUG_TAG,"ERROR: " + e.getMessage());
        }

    }

    /** starts mediaplayer with async prepering*/
    public void startPlaying(){
        try {
            mediaPlayer.prepareAsync();

        }catch(Exception e){
            Log.e(DEBUG_TAG,"ERROR OCCURED: " + e.getMessage());
        }
    }

    /** stops mediaplayer*/
    public void stopPlaying(){
        try {
            mediaPlayer.stop();
        } catch (Exception e) {
            Log.e(DEBUG_TAG,"ERROR OCCURED: " + e.getMessage());
        }
    }

    /** return boolean value whether mediaplayer is playing currently **/
    public boolean bubbleIsPlaying(){
        return mediaPlayer.isPlaying();
    }

    /** returns boolean value whether bubble has collided with horizontal line */
    public boolean isDetected() {
        return detected;
    }

    public void setDetected(boolean detected) {
        this.detected = detected;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /** returns bubble bottom line Y coordinate (int) */
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

    public void setBubbleHeight(int bubbleHeight) {
        this.bubbleHeight = (int)PixelsConverter.convertDpToPixel(bubbleHeight * 0.05f,getContext());

    }

    private void init(ServerFile serverFile){
        this.serverFile = serverFile;
        this.mDetector = new GestureDetector(getContext(),new BubbleTouchController(getContext(),this));
    }



    private void createRoundedRectangle() {
        initStyle();
        rectCoordinates = new RectF(0,0,0, bubbleHeight);
    }

    /** method for initilizing bubble styles attributes */
    private void initStyle(){
        Random rnd = new Random();
        color_selection = rnd.nextInt(8);

        active_colors = getContext().getResources().obtainTypedArray(R.array.active_colors);
        passive_colors = getContext().getResources().obtainTypedArray(R.array.passive_colors);

        active_color = new Paint(Paint.ANTI_ALIAS_FLAG);
        active_color.setColor(active_colors.getColor(color_selection, 0));
        active_color.setAlpha(alpha);

        passive_color = new Paint(Paint.ANTI_ALIAS_FLAG);
        passive_color.setColor(passive_colors.getColor(color_selection, 0));
        passive_color.setAlpha(alpha);

        setColor(passive_color);

    }

    public int returnFittingYcoordinate(int containerBottomY, int parentYCoordinates){
        fitMargin = (int)PixelsConverter.convertDpToPixel(5,getContext());

        if (bubbleHeight + parentYCoordinates <= containerBottomY && parentYCoordinates >= 0) {
            finalfittingYcoordinate = parentYCoordinates;
            setBubbleBottomY(parentYCoordinates + bubbleHeight);
        }
        else if(parentYCoordinates < 0){
            finalfittingYcoordinate = fitMargin;
            setBubbleBottomY(finalfittingYcoordinate + bubbleHeight - fitMargin);

        }

        else {
            finalfittingYcoordinate = containerBottomY - bubbleHeight - fitMargin;
            setBubbleBottomY(finalfittingYcoordinate + bubbleHeight + fitMargin);

        }



        Log.d(DEBUG_TAG, "containerBottomY is " + containerBottomY);
        Log.d(DEBUG_TAG, "BubbleHeight is " + bubbleHeight);
        Log.d(DEBUG_TAG, "parentYCoordinates is " + parentYCoordinates);
        Log.d(DEBUG_TAG, "finalfittingYcoordinate is " + finalfittingYcoordinate);
        Log.d(DEBUG_TAG, "bubblebottomY is " + getBubbleBottomY());

        return finalfittingYcoordinate;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }

    /** draws rounded rectangle with 100dp corner radius*/
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        rectCoordinates.right = canvas.getWidth();
        canvas.drawRoundRect(rectCoordinates, PixelsConverter.convertDpToPixel(100,getContext()),PixelsConverter.convertDpToPixel(100,getContext()),color);
    }


    /** handles bubble bound size*/
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        /** set bubble width bound to be same as parent view width */
        int widthSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec),MeasureSpec.EXACTLY);

        /** set bubble height bound */
        int heightSpec = MeasureSpec.makeMeasureSpec(bubbleHeight,MeasureSpec.EXACTLY);

        setMeasuredDimension(widthSpec,heightSpec);
    }

}
