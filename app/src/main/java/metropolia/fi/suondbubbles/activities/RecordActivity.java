package metropolia.fi.suondbubbles.activities;

import android.app.DialogFragment;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

import metropolia.fi.suondbubbles.R;
import metropolia.fi.suondbubbles.adapters.RecordingsAdapter;
import metropolia.fi.suondbubbles.dialogFragments.InputDialogFragment;
import metropolia.fi.suondbubbles.helper.Record;
import metropolia.fi.suondbubbles.helper.SoundFile;
import metropolia.fi.suondbubbles.helper.WavConverter;

/**
 * Warning: this activity use multiple threads,
 * so it is not possible to touch the UI components (aka buttons and so on) from those.
 **/
public class RecordActivity extends AppCompatActivity implements InputDialogFragment.InputDialogListener {

    private boolean recRunning, playRunning, countRuning;
    private Thread recordThread, playThread, countTread;
    private AudioTrack track;
    private TextView time_tv;
    private ListView recordings_lv;
    private RecordingsAdapter recordingsAdapter;
    private DrawerLayout drawerLayout;
    private ArrayList<Record> recordsArray;
    private final String DEBUG_TAG = this.getClass().getSimpleName();
    private final String FOLDER_NAME = "SoundBubbleRecords";
    private final String PATH_TO_FOLDER = Environment.getExternalStorageDirectory() + File.separator + FOLDER_NAME;
    private String pathRawFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        // creates folder if not exits
        SoundFile.createFolder(FOLDER_NAME);

        time_tv = (TextView) findViewById(R.id.time_tv);
        recRunning = false;
        playRunning = false;
        countRuning = false;

        recordings_lv = (ListView) findViewById(R.id.recordings_list_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_activity_record);
        initRecordingListView();

        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                initRecordingListView();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                initRecordingListView();
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    /** populates the slide menu if there are recordings **/
    public void initRecordingListView(){
        recordsArray = new ArrayList<>();
        boolean noFiles = false;

        // Get all files in the folder
        File recordingsFolder = new File(Environment.getExternalStorageDirectory() + File.separator + FOLDER_NAME);

        for (final File fileEntry : recordingsFolder.listFiles()) {
            Record record = new Record();
            record.setPath(fileEntry.getPath());
            record.setName(fileEntry.getName());
            recordsArray.add(record);
        }
        Log.d(DEBUG_TAG, "recordings found:" + recordsArray.size());

        recordingsAdapter = new RecordingsAdapter(this, recordsArray, drawerLayout);
        recordings_lv.setAdapter(recordingsAdapter);

        recordings_lv.setOnItemClickListener(onClickRecordingList());
    }

    private void setCurrentFilePath(String path){
        pathRawFile = path;
    }

    public AdapterView.OnItemClickListener onClickRecordingList(){
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(DEBUG_TAG, "pathfile:" + recordsArray.get(i).getPath());
                setCurrentFilePath(recordsArray.get(i).getPath());
                drawerLayout.closeDrawers();
            }
        };
    }

    /**
     * Start the thread to record
     *
     * @param v View that represents the button that was clicked
     **/
    public void clickRecordButton(View v) {
        if (!recRunning) {
            recordThread = new Thread() {
                public void run() {
                    recRunning = true;
                    setNewFilePath();
                    startRecord();
                }
            };
            recordThread.start();
            addPauseImageToButton(v);
            addPlayImageToButton(findViewById(R.id.play_fab));

            countTread = new Thread() {
                public void run() {
                    countRuning = true;
                    startCounting(countTread);
                }
            };
            countTread.start();
        } else {
            recRunning = false;
            countRuning = false;
            removePauseImageToButton(v);
        }
    }

    private void setNewFilePath(){
        Date date = new Date();
        long time = date.getTime();
        String path = PATH_TO_FOLDER + File.separator + "rec" + String.valueOf(time) + ".raw";
        pathRawFile = path;
    }

    /**
     * Start the thread to play the record sound
     *
     * @param v View that represents the button that was clicked
     **/
    public void clickPlayButton(View v) {
        if(pathRawFile == null || !new File(pathRawFile).exists()){
            Toast.makeText(this, "select a file or record something", Toast.LENGTH_SHORT).show();
            return;
        }
        // stop recording when user attempts to play record
        if (recRunning) {
            recRunning = false;
            countRuning = false;
            removePauseImageToButton(findViewById(R.id.record_button));
        }

        if (!playRunning) {
            playThread = new Thread() {
                public void run() {
                    playRunning = true;
                    playRecord();
                }
            };
            addPauseImageToButton(v);
            playThread.start();
        } else {
            playRunning = false;
            addPlayImageToButton(v);
        }
    }

    /**
     * Starts the process of saving and uploading the record
     *
     * @param v View that represents the button that was clicked
     **/
    public void clickUploadButton(View v) {

        InputDialogFragment inputDialogFragment = new InputDialogFragment();
        inputDialogFragment.show(getFragmentManager(), "inputDialogFragment");
    }

    /**
     * Start the thread that counts the time alongside the recording
     *
     * @param t the thread that makes the count
     **/
    private void startCounting(Thread t) {
        // time is in hundredth of a second
        Double count = 0.0;
        final int limit = 4000;

        while (count <= limit && countRuning) {
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

    private void addPauseImageToButton(View v) {
        ((AppCompatImageButton) v).setImageResource(android.R.drawable.ic_media_pause);
        //((Button) v).setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_media_pause,0,0,0);
    }

    private void removePauseImageToButton(View v) {
        ((AppCompatImageButton) v).setImageResource(android.R.color.transparent);//((Button) v).setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
    }

    private void addPlayImageToButton(View v) {
        ((AppCompatImageButton) v).setImageResource(android.R.drawable.ic_media_play);//setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_media_play,0,0,0);
    }

    /**
     * this method lets access an ui component (the time counter) to update it
     **/
    public Runnable updateTime(final Double count) {
        return new Runnable() {
            @Override
            public void run() {
                int secs = ((Double) Math.floor(count / 100)).intValue();
                int hundredths = ((Double) (count % 100)).intValue();
                String time = String.format("%02d:%02d", secs, hundredths);
                time_tv.setText(time);
            }
        };
    }

    /**
     * called from the record thread
     **/
    public void startRecord() {

        File file = new File(pathRawFile);

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

            while (recRunning) {
                int numofBytes = audioRecord.read(audioData, 0, minBufferSize);
                for (int i = 0; i < numofBytes; i++) {
                    dataOutputStream.write(audioData[i]);
                }
            }

            audioRecord.stop();
            dataOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * called from the play thread
     **/
    public void playRecord() {
        File file = new File(pathRawFile);
        FileInputStream inputStream = null;

        int minBufferSize = AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);
        track = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT, minBufferSize, AudioTrack.MODE_STREAM);

        track.play();

        int i = 0;
        byte[] buffer = new byte[minBufferSize];
        try {
            inputStream = new FileInputStream(file);
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

    /**
     * this is a implementation of the dialog interface when save button is clicked
     **/
    @Override
    public void onDialogYesClick(String title, String category, DialogFragment dialog) {
        dialog.dismiss();
        final String PATH_WAV_FILE = Environment.getExternalStorageDirectory() + File.separator + FOLDER_NAME + File.separator + title + ".wav";
        WavConverter wavConverter = new WavConverter(RecordActivity.this, title, category);
        String response = wavConverter.convertToWavAndUpload(pathRawFile, PATH_WAV_FILE);
        Log.d(DEBUG_TAG, response);
        Toast.makeText(this, "done", Toast.LENGTH_LONG).show();
    }

    /**
     * this is a implementation of the dialog interface when cancel button is clicked
     **/
    @Override
    public void onDialogCancelClick(DialogFragment dialog) {
        dialog.dismiss();
    }

}
