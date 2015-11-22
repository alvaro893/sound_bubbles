package metropolia.fi.suondbubbles.activities;

import android.app.DialogFragment;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import metropolia.fi.suondbubbles.R;
import metropolia.fi.suondbubbles.apiConnection.CollectionID;
import metropolia.fi.suondbubbles.apiConnection.ServerConnection;
import metropolia.fi.suondbubbles.apiConnection.ServerFile;
import metropolia.fi.suondbubbles.apiConnection.tasks.UploadTask;
import metropolia.fi.suondbubbles.dialogFragments.ConfirmDialogFragment;
import metropolia.fi.suondbubbles.dialogFragments.InputDialogFragment;
import metropolia.fi.suondbubbles.helper.SoundFile;
import metropolia.fi.suondbubbles.helper.WavConverter;

public class RecordActivity extends AppCompatActivity implements InputDialogFragment.InputDialogListener {

    private boolean recRunning, playRunning, countRuning;
    private Thread recordThread, playThread, countTread;
    private AudioTrack track;
    private SoundFile soundFile;
    private TextView time_tv;
    private final String DEBUG_TAG = this.getClass().getSimpleName();
    private final String FOLDER_NAME = "SoundBubbleRecords";
    private final String PATH_RAW_FILE = Environment.getExternalStorageDirectory() + File.separator + FOLDER_NAME + File.separator + "testrec.raw";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // creates folder if not exits
        SoundFile.createFolder(FOLDER_NAME);
        setContentView(R.layout.activity_record);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        time_tv = (TextView) findViewById(R.id.time_tv);
        recRunning=false;
        playRunning=false;
        countRuning=false;

        Log.d(DEBUG_TAG, PATH_RAW_FILE);
    }

    public void clickRecordButton(View v){
        if(!recRunning) {
            recordThread = new Thread() {
                public void run() {
                    recRunning = true;
                    startRecord();
                }
            };
            recordThread.start();

            countTread = new Thread(){
                public void run(){
                    countRuning = true;
                    startCounting(countTread);
                }
            };
            countTread.start();
        }else{
            recRunning = false;
            countRuning = false;
        }
    }

    

    public void clickPlayButton(View v){
        // stop recording when user attempts to play record
        if(recRunning){
            recRunning = false;
            countRuning = false;
        }

        if(!playRunning){
            playThread = new Thread() {
                public void run() {
                    playRunning = true;
                    playRecord();
                }
            };
            playThread.start();
        }else{
            playRunning = false;
        }
    }

    public void clickUploadButton(View v){

        InputDialogFragment inputDialogFragment = new InputDialogFragment();
        inputDialogFragment.show(getFragmentManager(), "inputDialogFragment");
    }

    private void startCounting(Thread t) {
        // time is in hundredth of a second
        Double count = 0.0;
        final int limit = 4000;
        
        while (count <= limit && countRuning){
            try {
                t.sleep(10);
                // Ui objects cannot be touch in other thread
                runOnUiThread(updateTime(count));
                count++;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        recRunning = false;
    }

    public Runnable updateTime(final Double count){
        return new Runnable() {
            @Override
            public void run() {
                int secs = ((Double) Math.floor(count / 100)).intValue();
                int hundredths = ((Double)(count % 100)).intValue();
                String time = String.format("%02d:%02d", secs, hundredths);
                time_tv.setText(time);
            }
        };
    }

    public void startRecord(){

        File file = new File(PATH_RAW_FILE);

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
        File file = new File(PATH_RAW_FILE);
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

    @Override
    public void onDialogYesClick(String title, String category, DialogFragment dialog) {
        String path = Environment.getExternalStorageDirectory() + File.separator + FOLDER_NAME + File.separator + title + ".wav";
        WavConverter wavConverter = new WavConverter(this);
        wavConverter.convWav(PATH_RAW_FILE, path);

        ServerFile serverFile = new ServerFile();
        serverFile.setCategory(category);
        serverFile.setTitle(title);
        serverFile.setPathLocalFile(path);
        serverFile.setFileExtension("wav");
        serverFile.setCollectionID(Integer.parseInt(CollectionID.getCollectionID()));

//        UploadTask uploadTask = new UploadTask();
//        uploadTask.execute(serverFile);

        dialog.dismiss();
    }

    @Override
    public void onDialogCancelClick(DialogFragment dialog) {
        dialog.dismiss();
    }
}
