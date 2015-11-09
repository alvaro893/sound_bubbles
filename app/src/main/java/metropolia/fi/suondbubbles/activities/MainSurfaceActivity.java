package metropolia.fi.suondbubbles.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;

import metropolia.fi.suondbubbles.Controllers.BubbleDragController;
import metropolia.fi.suondbubbles.R;
import metropolia.fi.suondbubbles.layouts.FixedLayout;

public class MainSurfaceActivity extends AppCompatActivity {

    private String DEBUG_TAG = "MainSurfaceActivity";
    private FixedLayout fixedLayout_1;
    private FixedLayout fixedLayout_2;
    private FixedLayout fixedLayout_3;
    private FixedLayout fixedLayout_4;
    private FixedLayout fixedLayout_5;
    private FixedLayout fixedLayout_6;
    private ScrollView scrollView;
    private ImageView removeView;
    private Intent intentSearchActivity;
    private ImageButton addBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_surface);
        intentSearchActivity = new Intent(this, SearchActivity.class);
        addBtn = (ImageButton) findViewById(R.id.btn_add);

        setButtonsBar();
        init();


        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    private void init() {
        scrollView = (ScrollView)findViewById(R.id.scrollView);

        fixedLayout_1 = (FixedLayout)findViewById(R.id.fixedLaytout_1);
        fixedLayout_2 = (FixedLayout)findViewById(R.id.fixedLaytout_2);
        fixedLayout_3 = (FixedLayout)findViewById(R.id.fixedLaytout_3);
        fixedLayout_4 = (FixedLayout)findViewById(R.id.fixedLaytout_4);
        fixedLayout_5 = (FixedLayout)findViewById(R.id.fixedLaytout_5);
        fixedLayout_6 = (FixedLayout)findViewById(R.id.fixedLaytout_6);
        removeView = (ImageView)findViewById(R.id.remove_view);

        fixedLayout_1.setOnDragListener(new BubbleDragController());
        fixedLayout_2.setOnDragListener(new BubbleDragController());
        fixedLayout_3.setOnDragListener(new BubbleDragController());
        fixedLayout_4.setOnDragListener(new BubbleDragController());
        fixedLayout_5.setOnDragListener(new BubbleDragController());
        fixedLayout_6.setOnDragListener(new BubbleDragController());
        removeView.setOnDragListener(new BubbleDragController());

    }

    private void setButtonsBar(){
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intentSearchActivity);
            }
        });
    }

}
