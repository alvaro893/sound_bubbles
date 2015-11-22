package metropolia.fi.suondbubbles;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.VideoView;

import metropolia.fi.suondbubbles.activities.LoginActivity;
import metropolia.fi.suondbubbles.activities.SoundBubbles;

public class MainActivity extends AppCompatActivity {
    private final String DEBUG_TAG = "MainActivity";
    private Uri intro;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SoundBubbles.MainContext = this;


        intro = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.first_screen_480x800);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        switch(metrics.densityDpi){
            case DisplayMetrics.DENSITY_LOW:
                intro = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.intro);
                Log.d(DEBUG_TAG,"DENSITY_LOW");
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                intro = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.intro);
                Log.d(DEBUG_TAG, "DENSITY_MEDIUM");
                break;
            case DisplayMetrics.DENSITY_HIGH:
                intro = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.first_screen_480x800);
                Log.d(DEBUG_TAG, "DENSITY_HIGH");
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                intro = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.first_screen_480x800);
                Log.d(DEBUG_TAG, "DENSITY_XHIGH");
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                intro = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.first_screen_480x800);
                Log.d(DEBUG_TAG,"DENSITY_XXHIGH");
                break;
            case DisplayMetrics.DENSITY_XXXHIGH:
                intro = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.first_screen_480x800);
                Log.d(DEBUG_TAG, "DENSITY_XXXHIGH");
                break;
        }


        //startActivity(intent);





    }

    @Override
    protected void onResume() {
        super.onResume();

        intent = new Intent(this, LoginActivity.class);

        VideoView videoIntro = (VideoView)findViewById(R.id.videoIntro);
        videoIntro.setVideoURI(intro);
        videoIntro.requestFocus();
        videoIntro.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                startActivity(intent);
                finish();
            }
        });
        videoIntro.start();
    }
}
