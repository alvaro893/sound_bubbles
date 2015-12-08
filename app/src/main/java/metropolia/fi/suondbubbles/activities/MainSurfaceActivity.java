package metropolia.fi.suondbubbles.activities;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
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
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import metropolia.fi.suondbubbles.Controllers.BubbleDragController;
import metropolia.fi.suondbubbles.R;
import metropolia.fi.suondbubbles.apiConnection.ServerFile;
import metropolia.fi.suondbubbles.bubble.BubbleParentLayoutRandomizer;
import metropolia.fi.suondbubbles.bubble.BubblePosition;
import metropolia.fi.suondbubbles.dialogFragments.ConfirmDialogFragment;
import metropolia.fi.suondbubbles.dialogFragments.ConfirmExitDialogFragment;
import metropolia.fi.suondbubbles.dialogFragments.VolumeControlFragment;
import metropolia.fi.suondbubbles.layouts.FixedLayout;
import metropolia.fi.suondbubbles.views.Bubble;

public class MainSurfaceActivity extends AppCompatActivity{

    /** Constants */
    private final String SELECTED_FILE = "SELECTED_FILE";
    private final String DEBUG_TAG = "MainSurfaceActivity";


    /** Arrays */
    private ArrayList<FixedLayout> linesList;
    private ArrayList<Bubble> bubbleList;
    private BubbleParentLayoutRandomizer bubbleParentLayoutRandomizer;
    private int[] parentLayoutIndexes;

    /** Views*/
    private FixedLayout fixedLayout_1;
    private FixedLayout fixedLayout_2;
    private FixedLayout fixedLayout_3;
    private FixedLayout fixedLayout_4;
    private FixedLayout fixedLayout_5;
    private FixedLayout fixedLayout_6;
    private FixedLayout bubbleParentLayout;
    private ImageView playButton;
    private ScrollView scrollView;
    private ImageView removeView;
    private Bubble bubble;
    private View horizontalLine;
    private Bubble calcBubble;
    private TextView emptyTimeLineView;
    private LinearLayout parentView;

    /** Layout parameter */
    private FixedLayout.LayoutParams layoutParams;

    /** Detectors of gestures */
    private GestureDetector mDetector_1;
    private GestureDetector mDetector_2;
    private GestureDetector mDetector_3;
    private GestureDetector mDetector_4;
    private GestureDetector mDetector_5;
    private GestureDetector mDetector_6;


    private BubblePosition bubblePosition;
    private Intent intentSearchActivity;
    private int secondActivityRequest = 542;

    /** data received from intent will be in these */
    private ArrayList<ServerFile> receivedServerFiles;

    /**Animations **/
    private Animation alphaAnimation;
    private boolean animationON = false;
    private boolean horizontalLinePaused = false;

    /** Animators **/
    private ObjectAnimator horizontalLineAnimator;


    private int bubbleYcoordinate = 0;
    private int calcBubbleBottomY = 0;
    private int calcBubbleHeight = 0;

    private float horizontalLineY;


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

    @Override
    protected void onPause() {
        super.onPause();
        stop();
    }

    /** method called after SearchActivity return*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == secondActivityRequest){
            if(resultCode == Activity.RESULT_OK){

                receivedServerFiles = (ArrayList<ServerFile>)data.getSerializableExtra(SELECTED_FILE);
                parentLayoutIndexes = new int[receivedServerFiles.size()];
                parentLayoutIndexes = bubbleParentLayoutRandomizer.generateRandomIDs(receivedServerFiles.size(), bubblePosition.getDoubleTappedLayoutIndex());

                for(int i = 0; i < receivedServerFiles.size(); i++){
                    bubble = createBubble(receivedServerFiles.get(i), parentLayoutIndexes[i]);
                    bubbleList.add(bubble);
                    emptyTimeLineView.setVisibility(View.GONE);
                    bubble.startAnimation(alphaAnimation);
                }
            }
        }
    }

    private Bubble createBubble(ServerFile receivedServerFile, int index) {
        Bubble localBubble;
        localBubble = initBubble(receivedServerFile);
        bubbleParentLayout = getBubbleParentLayout(index);
        bubbleYcoordinate = getBubbleY(localBubble);
        layoutParams = new FixedLayout.LayoutParams(bubbleParentLayout.getWidth(),0,0,bubbleYcoordinate);

        /** bubble view assigned to viewgroup, bubble view is now visible*/
        bubbleParentLayout.addView(localBubble, layoutParams);
        return localBubble;
    }

    private Bubble initBubble(ServerFile serverfile) {
        /** bubble view creation, not yet visible */
        Bubble initBubble = new Bubble(this, serverfile);
        initBubble.setDoubletapOnBubbleDetector(new Bubble.DoubletapOnBubbleDetector() {
            @Override
            public void onDoubleTapOnBubbleDetected(Bubble initBubble) {
                openVolumeControlDialog(initBubble);
            }
        });

        return initBubble;
    }

    public FixedLayout getBubbleParentLayout(int index) {

        return (FixedLayout)findViewById(linesList.get(index).getId());
    }

    public int getBubbleY(Bubble localBubble) {
        if(bubblePosition.getyCoordinate() == 0)
            return localBubble.returnFittingYcoordinate(bubbleParentLayout.getBottom(), scrollView.getScrollY());
        else
            return localBubble.returnFittingYcoordinate(bubbleParentLayout.getBottom(), (int)bubblePosition.getyCoordinate());
    }


    private void init() {
        initListeners();
        initLineList();
        initAnimations();

        intentSearchActivity = new Intent(this, SearchActivity.class);
        parentLayoutIndexes = null;


        bubbleList = new ArrayList<>();
        bubblePosition = new BubblePosition();
        bubbleParentLayoutRandomizer = new BubbleParentLayoutRandomizer(linesList);

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
        emptyTimeLineView = (TextView)findViewById(R.id.emptyTimeline);
        parentView = (LinearLayout)findViewById(R.id.parentView);

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

        horizontalLineAnimator.addPauseListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationPause(Animator animation) {
                horizontalLinePaused = true;
                playButton.setSelected(false);
                pausePlaying();
            }
        });

        horizontalLineAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                playButton.setSelected(true);
                animationON = true;
                disableBubbleDragging();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                playButton.setSelected(false);
                animationON = false;
                enableBubbleDragging();
                resetAnimation();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.d(DEBUG_TAG, "AnimationON changed onAnimationCancel ");
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }

    private void resetAnimation() {
        horizontalLine.setY(parentView.getBottom() - horizontalLine.getHeight());
    }

    public void goBackToBeginning(View v){
        if(!horizontalLinePaused && animationON){
            stop();
            horizontalLineAnimator.start();
            Log.d(DEBUG_TAG, "!horizontalLinePaused && animationON");
        }else if(horizontalLinePaused) {
            Log.d(DEBUG_TAG, "horizontalLinePaused");
            stop();
        }

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
                if(bubbleList.isEmpty()){
                    emptyTimeLineView.setVisibility(View.VISIBLE);
                }
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
        bubblePosition.setyCoordinate(0f);
        bubblePosition.setDoubleTappedLayoutIndex(-1);

        /** starting SearchActivity for a result */
        startActivityForResult(intentSearchActivity, secondActivityRequest);
    }

    /** called on record button click **/
    public void startRecordActivity(View v){
        startActivity(new Intent(this, RecordActivity.class));

    }

    /** Method for removing all visual bubbles on fixedLayouts*/
    private void removeAllBubbles() {
        for(int index = 0; index < linesList.size(); index++){
            linesList.get(index).removeAllViews();
        }
        bubbleList.clear();
        emptyTimeLineView.setVisibility(View.VISIBLE);
    }

    /** method for stopping horizontal line animation and  playing, aswell resets active bubbles to passive*/
    private void stop(){
        if(animationON){
            Log.d(DEBUG_TAG, "Animation was on");
            horizontalLineAnimator.end();
            horizontalLinePaused = false;
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
        if(!animationON && !horizontalLinePaused){

            Toast.makeText(getBaseContext(), "Playing started", Toast.LENGTH_SHORT).show();
            horizontalLineAnimator.start();

        }
        else if(animationON && horizontalLinePaused){
            horizontalLinePaused = false;
            playButton.setSelected(true);
            horizontalLineAnimator.resume();
            disableBubbleDragging();
        }
        else{

            Toast.makeText(getBaseContext(),"Playing paused", Toast.LENGTH_SHORT).show();
            horizontalLineAnimator.pause();
            enableBubbleDragging();
        }
    }


    private void pausePlaying(){
        for (int i = 0; i < bubbleList.size(); i++){
            calcBubble = bubbleList.get(i);
            if(calcBubble.bubbleIsPlaying()){
                calcBubble.pausePlaying();
            }
        }
    }

    private void disableBubbleDragging(){
        for (int i = 0; i < bubbleList.size(); i++){
            calcBubble = bubbleList.get(i);
            calcBubble.disableDragging(true);
        }
    }

    private void enableBubbleDragging(){
        for (int i = 0; i < bubbleList.size(); i++){
            calcBubble = bubbleList.get(i);
            calcBubble.disableDragging(false);
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

            if(calcBubble.isPaused()){
                calcBubble.startPlaying();
            }
            else {
                if (!calcBubble.isDetected()) {
                    if (y <= calcBubbleBottomY && calcBubbleBottomY - calcBubbleHeight <= y) {
                        calcBubble.setDetected(true);
                        calcBubble.setColor(calcBubble.getActive_color());
                        calcBubble.invalidate();
                        Log.d(DEBUG_TAG, "bubble detected");
                        calcBubble.startPlaying();
                    }

                }
                else if(y > calcBubbleBottomY || calcBubbleBottomY - calcBubbleHeight  > y){
                        calcBubble.stopPlaying();
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
            Log.d(DEBUG_TAG, "Double tapped Y: " + e.getY());
            stop();
            Log.d(DEBUG_TAG, "Double Tapped line index is " + linesList.indexOf(container));

            bubblePosition.setDoubleTappedLayoutIndex(linesList.indexOf(container));
            bubblePosition.setyCoordinate(e.getY());

            /** starting SearchActivity for a result */
            startActivityForResult(intentSearchActivity, secondActivityRequest);

            return true;
        }
    }
}
