package metropolia.fi.suondbubbles.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import metropolia.fi.suondbubbles.R;
import metropolia.fi.suondbubbles.controllers.BubbleDragController;
import metropolia.fi.suondbubbles.layouts.FixedLayout;

public class MainSurfaceActivity extends AppCompatActivity {

    private String DEBUG_TAG = "MainSurfaceActivity";
    private FixedLayout fixedLayout_1;
    private FixedLayout fixedLayout_2;
    private FixedLayout fixedLayout_3;
    private FixedLayout fixedLayout_4;
    private FixedLayout fixedLayout_5;
    private FixedLayout fixedLayout_6;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_surface);
        init();

    }

    private void init() {
        fixedLayout_1 = (FixedLayout)findViewById(R.id.fixedLaytout_1);
        fixedLayout_2 = (FixedLayout)findViewById(R.id.fixedLaytout_2);
        fixedLayout_3 = (FixedLayout)findViewById(R.id.fixedLaytout_3);
        fixedLayout_4 = (FixedLayout)findViewById(R.id.fixedLaytout_4);
        fixedLayout_5 = (FixedLayout)findViewById(R.id.fixedLaytout_5);
        fixedLayout_6 = (FixedLayout)findViewById(R.id.fixedLaytout_6);

        fixedLayout_1.setOnDragListener(new BubbleDragController());
        fixedLayout_2.setOnDragListener(new BubbleDragController());
        fixedLayout_3.setOnDragListener(new BubbleDragController());
        fixedLayout_4.setOnDragListener(new BubbleDragController());
        fixedLayout_5.setOnDragListener(new BubbleDragController());
        fixedLayout_6.setOnDragListener(new BubbleDragController());


    }


}
