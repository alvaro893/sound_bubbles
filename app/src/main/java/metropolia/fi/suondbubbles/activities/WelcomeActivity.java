package metropolia.fi.suondbubbles.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;

import metropolia.fi.suondbubbles.R;


public class WelcomeActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "do_not_show_pref";
    private static final String CHECKED = "checked";
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        checkBox = (CheckBox)findViewById(R.id.dont_show_checkbox);
        loadPreferences();
    }

    private void loadPreferences() {
        SharedPreferences pref = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        boolean checked = pref.getBoolean(CHECKED, false);
        if(checked){
            Intent intent = new Intent(this, MainSurfaceActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void checkCheckBoxStatus(){
        if(checkBox.isChecked()){
            savepreferences();
        }
    }

    private void savepreferences() {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putBoolean(CHECKED, checkBox.isChecked())
                .commit();
    }


    public void onSkipClick(View view){
        Intent intent = new Intent(this, MainSurfaceActivity.class);
        checkCheckBoxStatus();
        startActivity(intent);
        finish();
    }

    public void onNextClick(View view){
        Intent intent = new Intent(this, GuideActivity.class);
        checkCheckBoxStatus();
        startActivity(intent);
    }
}
