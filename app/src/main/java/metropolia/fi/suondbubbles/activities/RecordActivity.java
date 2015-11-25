package metropolia.fi.suondbubbles.activities;

import android.app.DialogFragment;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
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
import java.util.concurrent.ExecutionException;

import metropolia.fi.suondbubbles.R;
import metropolia.fi.suondbubbles.apiConnection.AsyncResponse;
import metropolia.fi.suondbubbles.apiConnection.CollectionID;
import metropolia.fi.suondbubbles.apiConnection.ServerFile;
import metropolia.fi.suondbubbles.apiConnection.tasks.UploadTask;
import metropolia.fi.suondbubbles.dialogFragments.InputDialogFragment;
import metropolia.fi.suondbubbles.helper.SoundFile;
import metropolia.fi.suondbubbles.helper.WavConverter;
/** Warning: this activity use multiple threads,
 *  so it is not possible to touch the UI components (aka buttons and so on) from those.  **/
public class RecordActivity extends AppCompatActivity implements InputDialogFragment.InputDialogListener, AsyncResponse {

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

    /** Start the thread to record
     * @param v View that represents the button that was clicked **/
    public void clickRecordButton(View v){
        if(!recRunning) {
            recordThread = new Thread() {
                public void run() {
                    recRunning = true;
                    startRecord();
                }
            };
            recordThread.start();
            addPauseImageToButton(v);
            addPlayImageToButton(findViewById(R.id.play_fab));

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
            removePauseImageToButton(v);
        }
    }

    /** Start the thread to play the record sound
     * @param v View that represents the button that was clicked **/
    public void clickPlayButton(View v){
        // stop recording when user attempts to play record
        if(recRunning){
            recRunning = false;
            countRuning = false;
            removePauseImageToButton(findViewById(R.id.record_fab));
        }

        if(!playRunning){
            playThread = new Thread() {
                public void run() {
                    playRunning = true;
                    playRecord();
                }
            };
            addPauseImageToButton(v);
            playThread.start();
        }else{
            playRunning = false;
            addPlayImageToButton(v);
        }
    }

    /** Starts the process of saving and uploading the record
     * @param v View that represents the button that was clicked **/
    public void clickUploadButton(View v){

        InputDialogFragment inputDialogFragment = new InputDialogFragment();
        inputDialogFragment.show(getFragmentManager(), "inputDialogFragment");
    }
    /** Start the thread that counts the time alongside the recording
     * @param t the thread that makes the count **/
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

    private void addPauseImageToButton(View v){
        ((FloatingActionButton)v).setImageResource(R.drawable.pause);
    }

    private void removePauseImageToButton(View v){
        ((FloatingActionButton)v).setImageResource(0);
    }

    private void addPlayImageToButton(View v){
        ((FloatingActionButton)v).setImageResource(android.R.drawable.ic_media_play);
    }
    /** this method lets access an ui component (the time counter) to update it**/
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
    /** called from the record thread **/
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

    /** called from the play thread **/
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

    /** this is a implementation of the dialog interface when save button is clicked **/
    @Override
    public void onDialogYesClick(String title, String category, DialogFragment dialog) {
        String path = Environment.getExternalStorageDirectory() + File.separator + FOLDER_NAME + File.separator + title + ".wav";
        WavConverter wavConverter = new WavConverter(this);
        wavConverter.convWav(PATH_RAW_FILE, path);

        ServerFile serverFile = new ServerFile();
        serverFile.setCategory(category);
        serverFile.setTitle(title);
        serverFile.setPathLocalFile(path);
        serverFile.setCollectionID(Integer.parseInt(CollectionID.getCollectionID()));
        serverFile.setLength(wavConverter.getTimeInSecs());
        serverFile.setCreator("SoundBubbles");
        serverFile.setDescription("Not supported yet");

        UploadTask uploadTask = new UploadTask();
        uploadTask.execute(serverFile);


        dialog.dismiss();
    }

    /** this is a implementation of the dialog interface when cancel button is clicked **/
    @Override
    public void onDialogCancelClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    /** this will be executed when the AsyncTask has been finish
     * @param result the response from UploadAsync task. It should include the response from the server  **/
    @Override
    public void processFinish(Object result) {
        Log.d(DEBUG_TAG, "result:"+result);
        Toast.makeText(this, result.toString(), Toast.LENGTH_LONG).show();

    }
}
