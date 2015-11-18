package metropolia.fi.suondbubbles;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.VideoView;

import metropolia.fi.suondbubbles.activities.LoginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Intent intent = new Intent(this, LoginActivity.class);
        //startActivity(intent);
        Uri intro = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.intro);
        VideoView videoIntro = (VideoView)findViewById(R.id.videoIntro);
        videoIntro.setVideoURI(intro);
        videoIntro.requestFocus();
        videoIntro.start();
        videoIntro.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                startActivity(intent);
            }
        });

    }
}
