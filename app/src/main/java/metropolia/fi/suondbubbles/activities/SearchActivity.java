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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
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
    private final String DEBUG_TAG = "SearchActivity";


    private EditText activity_search_et_search;
    private GridView activity_search_grid;
    private TextView warning_text;
    private Button activity_search_btn_add;
    private ServerFile[] filesArray;
    private ArrayList<ServerFile> filesList;
    private MediaPlayer mediaPlayer;
    private Integer lastSelectedId;
    private String[] categories;
    private boolean categoryWasSelected;
    private View lastElementSelected;
    private Bundle bundle;
    private final String viewCoordinates = "viewCoordinates";
    private final String viewID = "viewID";
    private final String selectedFile = "selectedFile";
    private final String returnBundle = "returnBundle";
    private View previousView;
    private View currentView;
    private int currPosition = -1;
    private int previousPosition = -1;
    private ServerFile serverFile;
    private ServerFilesArrayAdapter adapter;
    private GifImageView gifImageView;
    private ViewGroup gridLayoutElement;
    private boolean downloadCompleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // don't use this activity if the user isn't logged yet
        if(!SoundBubbles.userIsLogged())
            SoundBubbles.openLoginActivity(this);

        // initialize views
        activity_search_et_search = (EditText)findViewById(R.id.search);
        activity_search_et_search.setOnEditorActionListener(setSearchActionListener());
        activity_search_grid = (GridView) findViewById(R.id.gridView);
        activity_search_btn_add = (Button) findViewById(R.id.add);
        warning_text = (TextView)findViewById(R.id.warning_text);
        gifImageView = new GifImageView(getBaseContext());
        gifImageView.setBackgroundResource(R.drawable.loading);

        previousView = null;
        currentView = null;

        // broad search in order to show something
        //performSearch(" ");
        setAddButton();

        //initilize media player
        initMediaPlayer();

        // categories
        showCategoriesList();
        categoryWasSelected = false;

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

        // remove button when music is gone
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.stop();
                adapter.backToNormal(currentView);

            }
        });
    }

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
        lastSelectedId = null;
        search = search.trim();
        SearchTask searchTask = new SearchTask();
        searchTask.delegate = this;
        Log.d("serverConnection", SoundBubbles.serverConnection.toString());
        searchTask.execute(SoundBubbles.serverConnection, search.trim());
        Toast. makeText (getBaseContext(), "searching", Toast.LENGTH_SHORT ).show();
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

    private void setAddButton(){

        this.activity_search_btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lastSelectedId == null) {
                    Toast.makeText(SearchActivity.this, "Select some sound first",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (downloadCompleted) {
                        Intent receivedIntent = getIntent();
                        float coordinates = receivedIntent.getFloatExtra(viewCoordinates, 0);
                        int receivedViewId = receivedIntent.getIntExtra(viewID, 0);


                        bundle = new Bundle();
                        bundle.putSerializable(selectedFile, filesList.get(lastSelectedId));
                        bundle.putFloat(viewCoordinates, coordinates);
                        bundle.putInt(viewID, receivedViewId);


                        Intent returnIntent = new Intent();
                        returnIntent.putExtra(returnBundle, bundle);
                        setResult(Activity.RESULT_OK, returnIntent);
                        mediaPlayer.stop();
                        finish();
                    } else {
                        Toast.makeText(getBaseContext(),"Sound is not yet available, please wait", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            SoundBubbles.logout();
            SoundBubbles.openLoginActivity(this);
        }

        return super.onOptionsItemSelected(item);
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
            File file = null;

            try{
                URL url = new URL(urlString);
                SoundFile soundFile = new SoundFile(url.openStream());
                file = soundFile.createFileInCache(SoundBubbles.getMainContext(), filename);
            }catch (Exception e){
                Log.d(DEBUG_TAG, e.getClass().toString() + ":" +e.getMessage());
                e.printStackTrace();
            }
            return file.getPath();
        }

        @Override
        protected void onPreExecute() {
            downloadCompleted = false;
            adapter.switchToGifImage(currentView);
        }

        @Override
        protected void onPostExecute(String path) {
            downloadCompleted = true;
            Log.d(DEBUG_TAG,"DOWNLOAD COMPLETED");
            serverFile.setPathLocalFile(path);
            startPlayingSound();
        }
    }
}
