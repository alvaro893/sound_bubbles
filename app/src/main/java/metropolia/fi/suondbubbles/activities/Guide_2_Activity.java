package metropolia.fi.suondbubbles.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import metropolia.fi.suondbubbles.R;

public class Guide_2_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_2_);
    }

    public void onSkipClick(View view){
        Intent intent = new Intent(this, MainSurfaceActivity.class);
        startActivity(intent);
        finish();
    }

    public void onNextClick(View view){
        Intent intent = new Intent(this, Guide_3_Activity.class);
        startActivity(intent);
    }
}