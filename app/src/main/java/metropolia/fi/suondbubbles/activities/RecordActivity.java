package metropolia.fi.suondbubbles.activities;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import metropolia.fi.suondbubbles.R;

public class RecordActivity extends AppCompatActivity {

    private boolean recRunning, playRunning;
    private Thread t1, t2;
    private AudioTrack track;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recRunning=false;
        playRunning=false;


    }

    public void clickRecordButton(View v){
        if(!recRunning) {
            t1 = new Thread() {
                public void run() {
                    recRunning = true;
                    startRecord();
                }
            };
            t1.start();
        }else{
            recRunning = false;
        }
    }

    public void clickPlayButton(View v){
        // stop recording when user attempts to play record
        if(recRunning){
            recRunning = false;
        }

        if(!playRunning){
            t2 = new Thread() {
                public void run() {
                    playRunning = true;
                    playRecord();
                }
            };
            t2.start();
        }else{
            playRunning = false;
        }
    }

    public void startRecord(){

        File file = new File(Environment.getExternalStorageDirectory(), "testrec.raw");

        try {
            file.createNewFile();

            OutputStream outputStream = new FileOutputStream(file);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);

            int minBufferSize = AudioRecord.getMinBufferSize(44100,
                    AudioFormat.CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT);

            byte[] audioData = new byte[minBufferSize];

            AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    44100,
                    AudioFormat.CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    minBufferSize);

            audioRecord.startRecording();

            while(recRunning){
                int numofBytes = audioRecord.read(audioData, 0, minBufferSize);
                for(int i = 0; i < numofBytes; i++){
                    dataOutputStream.write(audioData[i]);
                }
            }

            audioRecord.stop();
            dataOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playRecord(){
        File file = new File(Environment.getExternalStorageDirectory(), "testrec.raw");
        FileInputStream inputStream=null;

        int minBufferSize = AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);
        track = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT, minBufferSize, AudioTrack.MODE_STREAM);

        track.play();

        int i = 0;
        byte[] buffer = new byte[minBufferSize];
        try {
            inputStream = new FileInputStream( file );
            while ((i = inputStream.read(buffer, 0, minBufferSize)) != -1 && playRunning) {
                track.write(buffer, 0, i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        track.stop();
        track.release();

    }

}
