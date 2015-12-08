package metropolia.fi.suondbubbles.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.io.IOException;
import java.util.Random;

import metropolia.fi.suondbubbles.Controllers.BubbleTouchController;
import metropolia.fi.suondbubbles.R;
import metropolia.fi.suondbubbles.apiConnection.ServerFile;
import metropolia.fi.suondbubbles.helper.PixelsConverter;
import metropolia.fi.suondbubbles.media.SoundPlayer;


public class Bubble extends View {

    public interface DoubletapOnBubbleDetector{
        void onDoubleTapOnBubbleDetected(Bubble bubble);
    }

    private final String DEBUG_TAG = "Bubble class";
    private final int MINIUM_ALLOWED_SIZE = 3000;



    private Paint color, passive_color, active_color, textPaint;

    private GestureDetector mDetector;
    private int bubbleHeight;
    private int bubbleWidth;
    private int color_selection;
    private int bubbleBottomY;
    private int finalfittingYcoordinate = 0;
    private int fitMargin = 0;
    private DoubletapOnBubbleDetector doubletapOnBubbleDetector;

    private int alpha = (int)(0.8 * 255);
    private TypedArray passive_colors, active_colors;
    private ServerFile serverFile;

    private RectF rectCoordinates;
    private SoundPlayer soundPlayer;
    private int soundVolume;
    private float bubbleVolume;
    private BubbleTouchController bubbleTouchController;

    private Rect textBounds;

    private boolean detected = false;
    private boolean paused = false;


    public Bubble(Context context, ServerFile serverFile) {
        super(context);

        init(serverFile);
        initHeight();

        initSoundPlayer();

        createRoundedRectangle();
        initText();
    }


    private void initText() {
        textBounds = new Rect();

        int spValue = 12;

        int pixel= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spValue, getResources().getDisplayMetrics());

        textPaint = new Paint();
        textPaint.setTextSize(pixel);
        textPaint.setColor(Color.WHITE);
        textPaint.setStyle(Paint.Style.FILL);
    }


    public int getSoundVolume() {
        return soundVolume;
    }

    public void setSoundVolume(int soundVolume) {
        this.soundVolume = soundVolume;
        setBubbleVolume(soundVolume);
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

    private void setBubbleVolume(int soundVolume){

        bubbleVolume = (float)(soundVolume * 0.01);
        soundPlayer.setSoundVolume(bubbleVolume);
    }

    private void initSoundPlayer(){
        soundPlayer = new SoundPlayer();
        soundPlayer.setSound(serverFile.getPathLocalFile());
        setSoundVolume(50);
        soundPlayer.setSoundPlayerListener(new SoundPlayer.SoundPlayerListener() {
            @Override
            public void onStarting() {
                if (paused)
                    paused = false;

            }

            @Override
            public void onPausing() {
                paused = true;
            }

            @Override
            public void onStopping() {

            }
        });

    }


    public void startPlaying(){
        soundPlayer.playIfNotPlaying();
    }

    public void stopPlaying(){
        soundPlayer.stopIfPlaying();
    }
    public void pausePlaying(){
        soundPlayer.pauseIfPlaying();
    }
    public boolean bubbleIsPlaying(){
        return soundPlayer.isSoundPlayerPlaying();
    }

    public void setDoubletapOnBubbleDetector(DoubletapOnBubbleDetector doubletapOnBubbleDetector) {
        this.doubletapOnBubbleDetector = doubletapOnBubbleDetector;
    }

    /** returns boolean value whether bubble has collided with horizontal line */
    public boolean isDetected() {
        return detected;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setDetected(boolean detected) {
        this.detected = detected;
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
        Log.d(DEBUG_TAG,"HEIGHT IS : " + bubbleHeight);
        if(bubbleHeight < MINIUM_ALLOWED_SIZE){
            bubbleHeight = MINIUM_ALLOWED_SIZE;
        }
        this.bubbleHeight = (int)PixelsConverter.convertDpToPixel(bubbleHeight * 0.025f,getContext());

    }


    private void init(ServerFile serverFile){
        this.serverFile = serverFile;



        bubbleTouchController = new BubbleTouchController(getContext(),this);

        bubbleTouchController.setBubbleDoubleTapListener(new BubbleTouchController.BubbleDoubleTapListener() {
            @Override
            public void onBubbleDoubleTap(Bubble bubble) {
                doubletapOnBubbleDetector.onDoubleTapOnBubbleDetected(bubble);
            }
        });

        this.mDetector = new GestureDetector(getContext(),bubbleTouchController);
    }

    public void disableDragging(boolean value){
        bubbleTouchController.setDragDisabled(value);
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

    public int returnFittingYcoordinate(int containerBottomY, int bubbleTopY){
        fitMargin = (int)PixelsConverter.convertDpToPixel(5,getContext());

        if (bubbleHeight + bubbleTopY <= containerBottomY && bubbleTopY >= 0) {
            finalfittingYcoordinate = bubbleTopY;
            setBubbleBottomY(bubbleTopY + bubbleHeight);
        }
        else if(bubbleTopY < 0){
            finalfittingYcoordinate = 0;
            setBubbleBottomY(finalfittingYcoordinate + bubbleHeight);

        }

        else {
            finalfittingYcoordinate = containerBottomY - bubbleHeight - fitMargin;
            setBubbleBottomY(finalfittingYcoordinate + bubbleHeight + fitMargin);

        }



        Log.d(DEBUG_TAG, "containerBottomY is " + containerBottomY);
        Log.d(DEBUG_TAG, "BubbleHeight is " + bubbleHeight);
        Log.d(DEBUG_TAG, "bubbleTopY is " + bubbleTopY);
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
        Log.d(DEBUG_TAG, "Filename is " + serverFile.getFilename());
        rectCoordinates.right = canvas.getWidth();
        canvas.drawRoundRect(rectCoordinates, PixelsConverter.convertDpToPixel(100, getContext()), PixelsConverter.convertDpToPixel(100, getContext()), color);
        canvas.save();
        canvas.rotate(90, bubbleWidth / 2, bubbleHeight / 2);
        if(textPaint.measureText(serverFile.getTitle()) > canvas.getHeight()){
            calibrateTextSize(serverFile.getTitle(), canvas.getHeight() - PixelsConverter.convertDpToPixel(5,getContext()));
        }
        drawTextCentred(canvas, textPaint, serverFile.getTitle(), bubbleWidth / 2, bubbleHeight / 2);
        canvas.restore();

    }

    public void drawTextCentred(Canvas canvas, Paint paint, String text, float cx, float cy){
        paint.getTextBounds(text, 0, text.length(), textBounds);
        canvas.drawText(text, cx - textBounds.exactCenterX(), cy - textBounds.exactCenterY(), paint);
    }

    private int calibrateTextSize(String str, float maxWidth)
    {
        int size = 0;

        do {
            textPaint.setTextSize(++size);
        } while(textPaint.measureText(str) < maxWidth);

        return size;
    }


    /** handles bubble bound size*/
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        bubbleWidth = MeasureSpec.getSize(widthMeasureSpec);
        /** set bubble width bound to be same as parent view width */
        int widthSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec),MeasureSpec.EXACTLY);

        /** set bubble height bound */
        int heightSpec = MeasureSpec.makeMeasureSpec(bubbleHeight,MeasureSpec.EXACTLY);

        setMeasuredDimension(widthSpec, heightSpec);
    }

}
