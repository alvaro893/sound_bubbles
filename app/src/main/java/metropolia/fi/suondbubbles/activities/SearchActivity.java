package metropolia.fi.suondbubbles.activities;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import metropolia.fi.suondbubbles.R;
import metropolia.fi.suondbubbles.adapters.CategoriesAdapter;
import metropolia.fi.suondbubbles.adapters.ServerFilesArrayAdapter;
import metropolia.fi.suondbubbles.apiConnection.AsyncResponse;
import metropolia.fi.suondbubbles.apiConnection.ServerFile;
import metropolia.fi.suondbubbles.apiConnection.tasks.CategoryTask;
import metropolia.fi.suondbubbles.apiConnection.tasks.SearchTask;
import metropolia.fi.suondbubbles.helper.SoundFile;
import pl.droidsonroids.gif.GifImageView;

public class SearchActivity extends AppCompatActivity implements AsyncResponse {

    /** Constants */
    private final String DEBUG_TAG = "SearchActivity";
    private final String VIEW_COORDINATES = "VIEW_COORDINATES";
    private final String VIEW_ID = "VIEW_ID";
    private final String SELECTED_FILE = "SELECTED_FILE";
    private final String RETURN_BUNDLE = "RETURN_BUNDLE";

    /** Views*/
    private EditText activity_search_et_search;
    private GridView activity_search_grid;
    private View lastElementSelected;
    private View previousView;
    private View currentView;
    private GifImageView gifImageView;

    /** Arrays */
    private ServerFile[] filesArray;
    private ArrayList<ServerFile> filesList;
    private String[] categories;
    private ServerFilesArrayAdapter adapter;

    /** Integers */
    private int lastSelectedId = 0;
    private int currPosition = -1;
    private int previousPosition = -1;

    /** Booleans */
    private boolean categoryWasSelected = false;
    private boolean downloadCompleted = false;
    private boolean invalidFile = false;


    private Bundle bundle;
    private MediaPlayer mediaPlayer;

    private ServerFile serverFile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // don't use this activity if the user isn't logged yet
        if(!SoundBubbles.userIsLogged())
            SoundBubbles.openLoginActivity(this);

        // initialize views
        initViews();

        //initilize media player
        initMediaPlayer();

        // categories
        showCategoriesList();

    }

    private void initViews(){
        activity_search_et_search = (EditText)findViewById(R.id.search);
        activity_search_et_search = (EditText) findViewById(R.id.search);
        activity_search_et_search.setOnEditorActionListener(setSearchActionListener());
        activity_search_grid = (GridView) findViewById(R.id.gridView);
        gifImageView = new GifImageView(getBaseContext());
        gifImageView.setBackgroundResource(R.drawable.loading);
        previousView = null;
        currentView = null;

    }


    private TextView.OnEditorActionListener setSearchActionListener(){
        return new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                SoundBubbles.hideKeyboard(SearchActivity.this, v);
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch(v.getText().toString());
                    return true;
                }
                return false;
            }
        };
    }

    private void initMediaPlayer(){

        //set up MediaPlayer
        mediaPlayer = new MediaPlayer();

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                startAnimation(currentView, mp);
                adapter.switchToProgressBarWithPause(currentView);
                mp.start();
            }
        });

        // goes back to normal view when music is gone
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.stop();
                adapter.backToNormal(currentView);

            }
        });
    }

    /** Starts playing animation*/
    private void startAnimation(View currentView, MediaPlayer player){
        ProgressBar progressBar = (ProgressBar)currentView.findViewById(R.id.progressBar);
        progressBar.setMax(player.getDuration());
        ObjectAnimator animation = ObjectAnimator.ofInt (progressBar, "progress", 0, progressBar.getMax()); // see this max value coming back here, we animale towards that value
        animation.setDuration(player.getDuration()); //in milliseconds
        animation.setInterpolator(new LinearInterpolator());
        animation.start();
    }

    private void playFile(ServerFile file, View currectGridView){

        if(currPosition == previousPosition && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            adapter.backToNormal(currectGridView);

        }else {

            if(previousView != null){
                adapter.backToNormal(previousView);
            }
            if(mediaPlayer.isPlaying()){
                mediaPlayer.stop();
            }

            //Download file if not exits in external memory
            download(file);

        }

    }

    private void startPlayingSound(){
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(serverFile.getPathLocalFile());
            mediaPlayer.setVolume(0.5f,0.5f);
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showServerFileList() {
        filesList = new ArrayList<>(Arrays.asList(filesArray));
        Log.d("filesArray", filesList.toString());


        adapter = new ServerFilesArrayAdapter(this, filesList);
        this.activity_search_grid.setAdapter(adapter);

        // set click for every item
        this.activity_search_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View currentGridView, int position, long id) {
                currentView = currentGridView;
                currPosition = position;
                Log.d(DEBUG_TAG, "clicked element");

                serverFile = filesList.get(position);
                Log.d(DEBUG_TAG, serverFile.getLink());

                playFile(serverFile, currentGridView);

                selectLastElement(position, currentGridView);


                previousView = currentView;
                previousPosition = currPosition;
            }

        });
    }

    private void showCategoriesList(){
        getCategories();
        CategoriesAdapter adapter = new CategoriesAdapter(this, this.categories);
        this.activity_search_grid.setAdapter(adapter);

        this.activity_search_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemPosition, long l) {
                performSearch(categories[itemPosition]);
                categoryWasSelected = true;
            }
        });
    }

    // processFinish is called after this method automatically
    protected void performSearch(String search) {
        lastSelectedId = 0;
        search = search.trim();
        SearchTask searchTask = new SearchTask();
        searchTask.delegate = this;
        Log.d("serverConnection", SoundBubbles.serverConnection.toString());
        searchTask.execute(SoundBubbles.serverConnection, search.trim());
        Toast. makeText(getBaseContext(), "searching", Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void processFinish(Object result) {
        filesArray = (ServerFile[]) result;
        showServerFileList();
    }

    private void download(ServerFile serverFile){
        SoundDownloadTask downloadTask = new SoundDownloadTask();


        // filename is not set, a timestamp will be used instead
        if(serverFile.getFilename() == null){
            String filename = Calendar.getInstance().getTimeInMillis() + "";
            downloadTask.execute(serverFile.getLink(), filename);
        }else{
            downloadTask.execute(serverFile.getLink(), serverFile.getFilename());
        }

    }


    public void selectBubbles(View v){
        if (lastSelectedId == 0) {
            Toast.makeText(SearchActivity.this, "Select some sound first",
                    Toast.LENGTH_SHORT).show();
        } else {
            if (downloadCompleted && !invalidFile) {
                Intent receivedIntent = getIntent();
                float coordinates = receivedIntent.getFloatExtra(VIEW_COORDINATES, 0);
                int receivedViewId = receivedIntent.getIntExtra(VIEW_ID, 0);


                bundle = new Bundle();
                bundle.putSerializable(SELECTED_FILE, filesList.get(lastSelectedId));
                bundle.putFloat(VIEW_COORDINATES, coordinates);
                bundle.putInt(VIEW_ID, receivedViewId);


                Intent returnIntent = new Intent();
                returnIntent.putExtra(RETURN_BUNDLE, bundle);
                setResult(Activity.RESULT_OK, returnIntent);
                mediaPlayer.stop();
                finish();

            } else if (invalidFile) {
                Toast.makeText(getBaseContext(), "Sound file is corrupted, select another one", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getBaseContext(), "Sound is not yet available, please wait", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCategories(){
        categories = new String[0];
        CategoryTask categoryTask = new CategoryTask();
        categoryTask.execute();
        try {
            categories = categoryTask.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        Log.d("categories", Arrays.deepToString(categories));
    }

    @Override
    public void onBackPressed() {
        if(categoryWasSelected){
            showCategoriesList();
            categoryWasSelected = false;
        }else{
            super.onBackPressed();
        }
    }

    // make visible the clicked element
    private void selectLastElement(int position, View currentGridView){
        if(lastElementSelected != null){
            lastElementSelected.setBackground(ContextCompat.getDrawable(SearchActivity.this, R.drawable.grid_border));
        }
        lastSelectedId = position;
        lastElementSelected = currentGridView;
        currentGridView.setBackground(ContextCompat.getDrawable(
                SearchActivity.this, R.drawable.grid_border_selected));
    }

    /** copied from DownloadTask */
    private class SoundDownloadTask extends AsyncTask<String, Void, String> {
        private final String DEBUG_TAG = "SoundDownloadTask";

        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0];
            String filename = params[1];
            File file;
            String path = null;

            try{
                URL url = new URL(urlString);
                SoundFile soundFile = new SoundFile(url.openStream());
                file = soundFile.createFileInCache(SoundBubbles.getMainContext(), filename);
                path = file.getPath();
            }catch (Exception e){
                Log.d(DEBUG_TAG, e.getClass().toString() + ":" +e.getMessage());
                e.printStackTrace();
            }


            return path;
        }

        @Override
        protected void onPreExecute() {
            downloadCompleted = false;
            adapter.switchToGifImage(currentView);
        }

        @Override
        protected void onPostExecute(String path) {
            downloadCompleted = true;
            Log.d(DEBUG_TAG, "DOWNLOAD COMPLETED");
            if(path != null){
                serverFile.setPathLocalFile(path);
                startPlayingSound();
                if(invalidFile){
                    invalidFile = false;
                }
            }else {
                invalidFile = true;
                adapter.backToNormal(currentView);
                Toast.makeText(getBaseContext(),"Corrupted sound", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
