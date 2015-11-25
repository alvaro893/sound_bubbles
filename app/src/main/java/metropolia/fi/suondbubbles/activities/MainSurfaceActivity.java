package metropolia.fi.suondbubbles.activities;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import metropolia.fi.suondbubbles.Controllers.BubbleDragController;
import metropolia.fi.suondbubbles.R;
import metropolia.fi.suondbubbles.apiConnection.ServerFile;
import metropolia.fi.suondbubbles.dialogFragments.ConfirmDialogFragment;
import metropolia.fi.suondbubbles.dialogFragments.ConfirmExitDialogFragment;
import metropolia.fi.suondbubbles.dialogFragments.VolumeControlFragment;
import metropolia.fi.suondbubbles.layouts.FixedLayout;
import metropolia.fi.suondbubbles.views.Bubble;

public class MainSurfaceActivity extends AppCompatActivity{

    private String DEBUG_TAG = "MainSurfaceActivity";

    private ArrayList<FixedLayout> linesList;
    private ArrayList<Bubble> bubbleList;

    /** Views*/
    private FixedLayout fixedLayout_1;
    private FixedLayout fixedLayout_2;
    private FixedLayout fixedLayout_3;
    private FixedLayout fixedLayout_4;
    private FixedLayout fixedLayout_5;
    private FixedLayout fixedLayout_6;
    private FixedLayout receivedFixedLayout;
    private ImageView playButton;
    private ScrollView scrollView;
    private ImageView removeView;
    private Bubble bubble;
    private View horizontalLine;
    private Bubble calcBubble;

    /** Layout parameter */
    private FixedLayout.LayoutParams layoutParams;

    /** Detectors of gestures */
    private GestureDetector mDetector_1;
    private GestureDetector mDetector_2;
    private GestureDetector mDetector_3;
    private GestureDetector mDetector_4;
    private GestureDetector mDetector_5;
    private GestureDetector mDetector_6;


    private Intent intentSearchActivity;
    private int secondActivityRequest = 542;

    /** data received from intent will be in these */
    private Bundle receivedBundle;
    private float receivedYCoordinates;
    private int receivedLayoutId;
    private ServerFile receivedServerFile;


    /**Constants for intents */
    private final String viewCoordinates = "viewCoordinates";
    private final String returnBundle = "returnBundle";
    private final String viewID = "viewID";
    private final String selectedFile = "selectedFile";


    /**Animations **/
    private Animation alphaAnimation;
    private boolean animationON = false;

    /** Animators **/
    private ObjectAnimator horizontalLineAnimator;


    private Random randomNumber;
    private int bubbleYcoordinate = 0;
    private int calcBubbleBottomY = 0;
    private int calcBubbleHeight = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_surface);

        scrollToBottom();

        assignViews();
        init();
    }

    @Override
    public void onBackPressed() {
        ConfirmExitDialogFragment dialogFragment = new ConfirmExitDialogFragment();
        dialogFragment.setConfirmExitDialogListener(new ConfirmExitDialogFragment.ConfirmExitDialogListener() {
            @Override
            public void onDialogYesClick(DialogFragment dialog) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                dialog.dismiss();
                startActivity(intent);
                finish();
            }

            @Override
            public void onDialogCancelClick(DialogFragment dialog) {
                dialog.dismiss();
            }
        });
        dialogFragment.show(getFragmentManager(), "ConfirmExitDialogFagment");
    }

    /** method called after SearchActivity return*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == secondActivityRequest){
            if(resultCode == Activity.RESULT_OK){

                /** data assignment to local members*/
                receivedBundle = data.getBundleExtra(returnBundle);
                receivedYCoordinates = receivedBundle.getFloat(viewCoordinates);
                receivedLayoutId = receivedBundle.getInt(viewID);
                receivedServerFile = (ServerFile)receivedBundle.getSerializable(selectedFile);


                /** assignment of double tapped viewGroup received from intent */
                receivedFixedLayout = (FixedLayout)findViewById(receivedLayoutId);

                /** bubble view creation, not yet visible */
                bubble = new Bubble(this, receivedServerFile);
                bubble.setDoubletapOnBubbleDetector(new Bubble.DoubletapOnBubbleDetector() {
                    @Override
                    public void onDoubleTapOnBubbleDetected(Bubble bubble) {
                        openVolumeControlDialog(bubble);
                    }
                });





                if(receivedYCoordinates == 0){
                    bubbleYcoordinate = bubble.returnFittingYcoordinate(receivedFixedLayout.getBottom(), scrollView.getScrollY());
                }else{
                    bubbleYcoordinate = bubble.returnFittingYcoordinate(receivedFixedLayout.getBottom(), (int) receivedYCoordinates);
                }

                layoutParams = new FixedLayout.LayoutParams(receivedFixedLayout.getWidth(),0,0,bubbleYcoordinate);

                /** bubble view assigned to viewgroup, bubble view is now visible*/
                receivedFixedLayout.addView(bubble, layoutParams);
                bubbleList.add(bubble);
                Log.d(DEBUG_TAG, "bubble bottom:" + bubble.getBubbleBottomY());


                bubble.startAnimation(alphaAnimation);

            }
        }
    }

    
    private void init() {
        initListeners();
        initLineList();
        initAnimations();

        intentSearchActivity = new Intent(this, SearchActivity.class);
        randomNumber = new Random();
        bubbleList = new ArrayList<>();
    }

    private void assignViews() {
        playButton = (ImageView)findViewById(R.id.btn_play);
        fixedLayout_1 = (FixedLayout)findViewById(R.id.fixedLaytout_1);
        fixedLayout_2 = (FixedLayout)findViewById(R.id.fixedLaytout_2);
        fixedLayout_3 = (FixedLayout)findViewById(R.id.fixedLaytout_3);
        fixedLayout_4 = (FixedLayout)findViewById(R.id.fixedLaytout_4);
        fixedLayout_5 = (FixedLayout)findViewById(R.id.fixedLaytout_5);
        fixedLayout_6 = (FixedLayout)findViewById(R.id.fixedLaytout_6);
        removeView = (ImageView)findViewById(R.id.remove_view);

    }

    private void initAnimations() {
        alphaAnimation = AnimationUtils.loadAnimation(this, R.anim.alpha);

        horizontalLine = findViewById(R.id.horizontal_line);
        horizontalLineAnimator = (ObjectAnimator)AnimatorInflater.loadAnimator(this, R.animator.translate_y);
        horizontalLineAnimator.setTarget(horizontalLine);
        horizontalLineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                playDetectedBubble(Float.valueOf(animation.getAnimatedValue("y").toString()));
            }
        });
        horizontalLineAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                playButton.setSelected(true);
                horizontalLine.setVisibility(View.VISIBLE);
                animationON = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                playButton.setSelected(false);
                horizontalLine.setVisibility(View.INVISIBLE);
                animationON = false;

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }

    /**method that add all fixedLayouts to list */
    private void initLineList() {

        linesList = new ArrayList<>();
        linesList.add(fixedLayout_1);
        linesList.add(fixedLayout_2);
        linesList.add(fixedLayout_3);
        linesList.add(fixedLayout_4);
        linesList.add(fixedLayout_5);
        linesList.add(fixedLayout_6);
    }

    /** scrolls scrollView down to bottom in separate thread */
    private void scrollToBottom(){

        scrollView = (ScrollView)findViewById(R.id.scrollView);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    /** method for assigning touchListeners and dragListeners for viewGroups(FixedLayout)*/
    private void initListeners() {

        BubbleDragController bubbleDragController = new BubbleDragController();

        bubbleDragController.setRemoveView(removeView);
        bubbleDragController.setBubbleRemoveListener(new BubbleDragController.BubbleRemoveListener() {
            @Override
            public void bubbleRemoved(Bubble bubble) {
                bubbleList.remove(bubble);
            }
        });

        mDetector_1 = new GestureDetector(getApplicationContext(), new FixedLayoutTouchController(fixedLayout_1));
        mDetector_2 = new GestureDetector(getApplicationContext(), new FixedLayoutTouchController(fixedLayout_2));
        mDetector_3 = new GestureDetector(getApplicationContext(), new FixedLayoutTouchController(fixedLayout_3));
        mDetector_4 = new GestureDetector(getApplicationContext(), new FixedLayoutTouchController(fixedLayout_4));
        mDetector_5 = new GestureDetector(getApplicationContext(), new FixedLayoutTouchController(fixedLayout_5));
        mDetector_6 = new GestureDetector(getApplicationContext(), new FixedLayoutTouchController(fixedLayout_6));



        fixedLayout_1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
              return mDetector_1.onTouchEvent(event);
            }
        });

        fixedLayout_2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mDetector_2.onTouchEvent(event);
            }
        });

        fixedLayout_3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mDetector_3.onTouchEvent(event);
            }
        });

        fixedLayout_4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mDetector_4.onTouchEvent(event);
            }
        });

        fixedLayout_5.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mDetector_5.onTouchEvent(event);
            }
        });

        fixedLayout_6.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mDetector_6.onTouchEvent(event);
            }
        });

        fixedLayout_1.setOnDragListener(bubbleDragController);
        fixedLayout_2.setOnDragListener(bubbleDragController);
        fixedLayout_3.setOnDragListener(bubbleDragController);
        fixedLayout_4.setOnDragListener(bubbleDragController);
        fixedLayout_5.setOnDragListener(bubbleDragController);
        fixedLayout_6.setOnDragListener(bubbleDragController);
        removeView.setOnDragListener(bubbleDragController);
    }

    /** called on add button click*/
    public void addNewBubble(View v){
        stop();

        Log.d(DEBUG_TAG, "Y coordinate is " + scrollView.getScrollY());


        /** adding Y coordinate of visible screen to intent for activityOnResult*/
        intentSearchActivity.putExtra(viewCoordinates, 0.0f);

        /** adding random fixedLayout ID to intent for activityOnResult*/
        intentSearchActivity.putExtra(viewID, linesList.get(randomNumber.nextInt(linesList.size())).getId());

        /** starting SearchActivity for a result */
        startActivityForResult(intentSearchActivity, secondActivityRequest);
    }

    /** called on record button click **/
    public void startRecordActivity(View v){
        startActivity(new Intent(this, RecordActivity.class));
        stop();
    }

    /** Method for removing all visual bubbles on fixedLayouts*/
    private void removeAllBubbles() {
        for(int index = 0; index < linesList.size(); index++){
            linesList.get(index).removeAllViews();
        }
        bubbleList.clear();
    }

    /** method for stopping horizontal line animation and  playing, aswell resets active bubbles to passive*/
    private void stop(){
        if(animationON){
            Log.d(DEBUG_TAG, "Animation was on");
            horizontalLineAnimator.end();
            stopAllBubblePlaying();
            resetBubblesDetected();
        }else
            Log.d(DEBUG_TAG,"Animation was off");
    }

    /** called on new button click*/
    public void startNewSession(View v){
        ConfirmDialogFragment dialogFragment = new ConfirmDialogFragment();
        dialogFragment.setConfirmDialogListener(new ConfirmDialogFragment.ConfirmDialogListener() {
            @Override
            public void onDialogYesClick(DialogFragment dialog) {
                dialog.dismiss();
                stop();
                removeAllBubbles();
            }

            @Override
            public void onDialogCancelClick(DialogFragment dialog) {
                dialog.dismiss();
            }
        });
        dialogFragment.show(getFragmentManager(), "ConfirmDialogFagment");

    }

    /**called on play button click*/
    public void startPlay(View v){
        if(!animationON){

            Toast.makeText(getBaseContext(), "Playing started", Toast.LENGTH_SHORT).show();
            horizontalLineAnimator.start();
        }
        else{

            Toast.makeText(getBaseContext(),"Playing stopped", Toast.LENGTH_SHORT).show();
            stop();
        }
    }

    /** cancels all playing */
    private void stopAllBubblePlaying() {
        for (int i = 0; i < bubbleList.size(); i++){
            calcBubble = bubbleList.get(i);
            if(calcBubble.bubbleIsPlaying()){
                calcBubble.stopPlaying();
            }
        }
    }

    /** changes all active bubbles color back to passive color */
    private void resetBubblesDetected() {
        for(int i = 0 ; i < bubbleList.size(); i++){
            calcBubble = bubbleList.get(i);
            if(calcBubble.isDetected()){
                calcBubble.setDetected(false);
                calcBubble.setColor(calcBubble.getPassive_color());
                calcBubble.invalidate();
            }
        }
    }

    /** Method for playing detected bubble */
    private void playDetectedBubble(float y){
        for(int i = 0; i < bubbleList.size(); i++){
            calcBubble = bubbleList.get(i);
            calcBubbleHeight = calcBubble.getBubbleHeight();
            calcBubbleBottomY = calcBubble.getBubbleBottomY();

            if(!calcBubble.isDetected()){
                if(y <= calcBubbleBottomY && calcBubbleBottomY - calcBubbleHeight <= y){
                    calcBubble.setDetected(true);
                    calcBubble.setColor(calcBubble.getActive_color());
                    calcBubble.invalidate();
                    Log.d(DEBUG_TAG, "bubble detected");
                    calcBubble.startPlaying();

                }
            }else{
                if(calcBubbleBottomY - calcBubbleHeight >= y){
                    calcBubble.setColor(calcBubble.getPassive_color());
                    calcBubble.invalidate();
                    calcBubble.setDetected(false);
                }
            }
        }

    }

    /** DialogFragment for adjusting volume */
    private void openVolumeControlDialog(final Bubble bubble){
        VolumeControlFragment dialogFragment = new VolumeControlFragment();
        dialogFragment.setmListener(new VolumeControlFragment.VolumeListener() {
            @Override
            public void onVolumeChange(int volume) {
                Log.d(DEBUG_TAG,"Sound volume is:" + volume);
                bubble.setSoundVolume(volume);
            }

            @Override
            public void onOkClick(DialogFragment dialog) {
                dialog.dismiss();
            }
        });
        dialogFragment.setVolume(bubble.getSoundVolume());
        dialogFragment.show(getFragmentManager(), "VolumeControlFragment");

    }


    /** Class for controlling touches on FixedLayout(lines)*/
    private class FixedLayoutTouchController extends GestureDetector.SimpleOnGestureListener {

        private final String DEBUG_TAG = "FixedLayTouchController";
        private FixedLayout container;


        public FixedLayoutTouchController(FixedLayout container){
            this.container = container;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            /** value must be true, otherwise doubletap event won't accure */
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d(DEBUG_TAG, "double tapped Y: " + e.getY());
            stop();


            /** adding data relevant for bubble view creation in onActivityResult method */
            intentSearchActivity.putExtra(viewCoordinates, e.getY());
            intentSearchActivity.putExtra(viewID, container.getId());

            /** starting SearchActivity for a result */
            startActivityForResult(intentSearchActivity, secondActivityRequest);

            return true;
        }
    }
}
