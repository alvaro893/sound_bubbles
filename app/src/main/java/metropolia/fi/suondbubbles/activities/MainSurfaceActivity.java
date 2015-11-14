package metropolia.fi.suondbubbles.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.Random;

import metropolia.fi.suondbubbles.Controllers.BubbleDragController;
import metropolia.fi.suondbubbles.R;
import metropolia.fi.suondbubbles.apiConnection.ServerFile;
import metropolia.fi.suondbubbles.layouts.FixedLayout;
import metropolia.fi.suondbubbles.views.Bubble;

public class MainSurfaceActivity extends AppCompatActivity {

    private String DEBUG_TAG = "MainSurfaceActivity";

    private ArrayList<FixedLayout> linesList;

    /** Views*/
    private FixedLayout fixedLayout_1;
    private FixedLayout fixedLayout_2;
    private FixedLayout fixedLayout_3;
    private FixedLayout fixedLayout_4;
    private FixedLayout fixedLayout_5;
    private FixedLayout fixedLayout_6;
    private FixedLayout receivedFixedLayout;
    private ScrollView scrollView;
    private ImageView removeView;
    private ImageButton addBtn;
    private Bubble bubble;

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

    /** data recieved from intent will be in these */
    private Bundle receivedBundle;
    private float receivedYCoordinates;
    private int receivedLayoutId;
    private ServerFile receivedServerFile;


    /**Constants for intents */
    private final String viewCoordinates = "viewCoordinates";
    private final String returnBundle = "returnBundle";
    private final String viewID = "viewID";
    private final String selectedFile = "selectedFile";


    private Random randomNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_surface);
        intentSearchActivity = new Intent(this, SearchActivity.class);

        /** scrolling down to bottom*/
        scrollToBottom();

        /** assigning touchListeners and dragListeners for viewGroups(FixedLayout aKa lines)*/
        initListeners();
        initLineList();


        randomNumber = new Random();
    }

    private void initLineList() {
        linesList = new ArrayList<FixedLayout>();
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


        fixedLayout_1 = (FixedLayout)findViewById(R.id.fixedLaytout_1);
        fixedLayout_2 = (FixedLayout)findViewById(R.id.fixedLaytout_2);
        fixedLayout_3 = (FixedLayout)findViewById(R.id.fixedLaytout_3);
        fixedLayout_4 = (FixedLayout)findViewById(R.id.fixedLaytout_4);
        fixedLayout_5 = (FixedLayout)findViewById(R.id.fixedLaytout_5);
        fixedLayout_6 = (FixedLayout)findViewById(R.id.fixedLaytout_6);
        removeView = (ImageView)findViewById(R.id.remove_view);

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

        fixedLayout_1.setOnDragListener(new BubbleDragController());
        fixedLayout_2.setOnDragListener(new BubbleDragController());
        fixedLayout_3.setOnDragListener(new BubbleDragController());
        fixedLayout_4.setOnDragListener(new BubbleDragController());
        fixedLayout_5.setOnDragListener(new BubbleDragController());
        fixedLayout_6.setOnDragListener(new BubbleDragController());
        removeView.setOnDragListener(new BubbleDragController());

    }


    /** called on add button click*/
    public void addNewBubble(View v){

        Log.d(DEBUG_TAG,"Y coordinate is " + scrollView.getScrollY());


        /** adding Y coordinate of visible screen to intent for activityOnResult*/
        intentSearchActivity.putExtra(viewCoordinates, (float)scrollView.getScrollY());

        /** adding random fixedLayout ID to intent for activityOnResult*/
        intentSearchActivity.putExtra(viewID, linesList.get(randomNumber.nextInt(linesList.size())).getId());

        /** starting SearchActivity for a result */
        startActivityForResult(intentSearchActivity, secondActivityRequest);
    }


    /** Class for controlling touches on FixedLayout(lines)*/
    private class FixedLayoutTouchController extends GestureDetector.SimpleOnGestureListener {

        private FixedLayout container;


        public FixedLayoutTouchController(FixedLayout container){
            this.container = container;
        }

        private String DEBUG_TAG = "FixedLayoutTouchController";

        @Override
        public boolean onDown(MotionEvent e) {
            /** value must be true, otherwise doubletap event won't accure */
            return true;
        }



        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d(DEBUG_TAG, "double tapped Y: " + e.getY());


            /** adding data relevent for bubble view creation in onActivityResult method */
            intentSearchActivity.putExtra(viewCoordinates, e.getY());
            intentSearchActivity.putExtra(viewID, container.getId());

            /** starting SearchActivity for a result */
            startActivityForResult(intentSearchActivity, secondActivityRequest);

            return true;
        }
    }

    /** method called after SearchActivity return*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == secondActivityRequest){
            if(resultCode == Activity.RESULT_OK){

                /** data assigment to local members*/
                receivedBundle = data.getBundleExtra(returnBundle);
                receivedYCoordinates = receivedBundle.getFloat(viewCoordinates);
                receivedLayoutId = receivedBundle.getInt(viewID);
                receivedServerFile = (ServerFile)receivedBundle.getSerializable(selectedFile);


                /** assignent of double tapped viewGroup recived from intent */
                receivedFixedLayout = (FixedLayout)findViewById(receivedLayoutId);

                /** bubble view creation, not yet visible */
                bubble = new Bubble(getBaseContext(), receivedServerFile);

                /** bubble size and location in viewgroup assigment*/
                layoutParams = new FixedLayout.LayoutParams(receivedFixedLayout.getWidth(),bubble.getHeight(),0,(int)receivedYCoordinates);

                /** bubble view assigned to viewgroup, bubble view is now visible*/
                receivedFixedLayout.addView(bubble, layoutParams);

            }
        }
    }
}
