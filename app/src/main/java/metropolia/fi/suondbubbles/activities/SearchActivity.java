package metropolia.fi.suondbubbles.activities;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
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
import metropolia.fi.suondbubbles.media.SoundPlayer;
import pl.droidsonroids.gif.GifImageView;

public class SearchActivity extends AppCompatActivity implements AsyncResponse {

    /** Constants */
    private final String DEBUG_TAG = "SearchActivity";
    private final String VIEW_COORDINATES = "VIEW_COORDINATES";
    private final String VIEW_ID = "VIEW_ID";
    private final String SELECTED_FILE = "SELECTED_FILE";
    private final String RETURN_BUNDLE = "RETURN_BUNDLE";
    private final int MAXIMUM_AMOUNT = 5;

    /** Views*/
    private EditText activity_search_et_search;
    private GridView activity_search_grid;
    private View lastElementSelected;
    private View previousView;
    private View currentView;
    private GifImageView gifImageView;
    private TextView category;
    private Button selectSounds, backToBubbles, cancel, addSounds;

    /** Arrays */
    private ServerFile[] filesArray;
    private ArrayList<ServerFile> filesList;
    private String[] categories;
    private ServerFilesArrayAdapter adapter;
    private boolean[] isSelected;
    private ArrayList<View> selectedViews;
    private ArrayList<ServerFile> serverFileArray;

    /** Integers */
    private int lastSelectedId;
    private int currPosition;
    private int previousPosition;
    private int currentSelectedSounds;

    /** Booleans */
    private boolean categoryWasSelected;
    private boolean downloadCompleted;
    private boolean invalidFile;
    private boolean modeAddSounds;
    private boolean modeSearchSounds;


    private SoundPlayer player;
    private Bundle bundle;
    private ServerFile serverFile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // don't use this activity if the user isn't logged yet
        if(!SoundBubbles.userIsLogged())
            SoundBubbles.openLoginActivity(this);

        init();

        // initialize views
        initViews();

        //initilize media player
        initSoundPlayer();

        // categories
        showCategoriesList();

    }

    private void init() {
        categoryWasSelected = false;
        downloadCompleted = false;
        invalidFile = false;
        modeAddSounds = false;
        modeSearchSounds = false;

        serverFileArray = new ArrayList<>();

        lastSelectedId = 0;
        currPosition = -1;
        previousPosition = -1;
        currentSelectedSounds = 0;
    }

    private void initViews(){
        activity_search_et_search = (EditText)findViewById(R.id.search);
        activity_search_et_search = (EditText) findViewById(R.id.search);
        activity_search_et_search.setOnEditorActionListener(setSearchActionListener());
        activity_search_grid = (GridView) findViewById(R.id.gridView);
        backToBubbles = (Button)findViewById(R.id.back_to_bubbles);
        selectSounds = (Button)findViewById(R.id.select_sounds);
        cancel = (Button)findViewById(R.id.search_activity_cancel);
        addSounds = (Button)findViewById(R.id.search_activity_add_sounds);
        category = (TextView)findViewById(R.id.textView_categories);
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
                    modeSearchSounds = true;
                    category.setText(R.string.search_result);
                    performSearch(v.getText().toString());
                    player.stopIfPlaying();

                    return true;
                }
                return false;
            }
        };
    }

    private void initSoundPlayer(){

        player = new SoundPlayer();

        player.setSoundPlayerListener(new SoundPlayer.SoundPlayerListener() {
            @Override
            public void onStarting() {
                startAnimation(currentView, player);
                adapter.switchToProgressBarWithPause(currentView);
            }

            @Override
            public void onPausing() {
                adapter.backToNormal(currentView);
            }

            @Override
            public void onStopping() {
                adapter.backToNormal(currentView);

            }
        });
    }

    /** Starts playing animation*/
    private void startAnimation(View currentView, SoundPlayer player){
        ProgressBar progressBar = (ProgressBar)currentView.findViewById(R.id.progressBar);
        progressBar.setMax(player.getDuration());
        ObjectAnimator animation = ObjectAnimator.ofInt (progressBar, "progress", 0, progressBar.getMax()); // see this max value coming back here, we animale towards that value
        animation.setDuration(player.getDuration()); //in milliseconds
        animation.setInterpolator(new LinearInterpolator());
        animation.start();
    }

    private void playFile(ServerFile file){

        if(currPosition == previousPosition){
            player.stopIfPlaying();
        }
        else {

            if(previousView != null){
                adapter.backToNormal(previousView);
            }
            if(player.isSoundPlayerPlaying()){
                player.stopIfPlaying();
            }

            //Download file if not exits in external memory
            download(file);

        }

    }

    private void changeToModeAdd(){
        modeAddSounds = true;
        backToBubbles.setVisibility(View.GONE);
        selectSounds.setVisibility(View.GONE);
        cancel.setVisibility(View.VISIBLE);
        addSounds.setVisibility(View.VISIBLE);
    }

    private void changeToModeNormal(){
        modeAddSounds = false;
        backToBubbles.setVisibility(View.VISIBLE);
        selectSounds.setVisibility(View.VISIBLE);
        cancel.setVisibility(View.GONE);
        addSounds.setVisibility(View.GONE);

        if(isSelected != null) {
            Arrays.fill(isSelected, Boolean.FALSE);
            resetViews();
            currentSelectedSounds = 0;
        }
    }

    public void cancelAdding(View v){
        changeToModeNormal();
    }

    private void resetViews(){
        for(int i = 0; i < selectedViews.size(); i++){
            selectedViews.get(i).setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.grid_border));
        }
        selectedViews.clear();
    }

    public void addSounds(View v){
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
                player.stopIfPlaying();
                finish();

            } else if (invalidFile) {
                Toast.makeText(getBaseContext(), "Sound file is corrupted, select another one", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getBaseContext(), "Sound is not yet available, please wait", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showServerFileList() {
        filesList = new ArrayList<>(Arrays.asList(filesArray));
        selectedViews = new ArrayList<>();
        serverFileArray = new ArrayList<>();
        Log.d("filesArray", filesList.toString());
        isSelected = new boolean[filesList.size()];

        adapter = new ServerFilesArrayAdapter(this, filesList);
        this.activity_search_grid.setAdapter(adapter);

        // set click for every item
        this.activity_search_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View currentGridView, int position, long id) {

                if(!modeAddSounds) {
                    currentView = currentGridView;
                    currPosition = position;
                    Log.d(DEBUG_TAG, "clicked element");

                    serverFile = filesList.get(position);
                    Log.d(DEBUG_TAG, serverFile.getLink());

                    playFile(serverFile);

                    selectLastElement(position, currentGridView);

                    previousView = currentView;
                    previousPosition = currPosition;
                } else{
                    if(isSelected[position]) {
                        isSelected[position] = false;
                        currentSelectedSounds -= 1;
                        selectedViews.remove(currentGridView);
                        currentGridView.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.grid_border));
                    }else{
                        if(currentSelectedSounds < MAXIMUM_AMOUNT) {
                            isSelected[position] = true;
                            currentSelectedSounds += 1;
                            selectedViews.add(currentGridView);

                            // passive aqua color with 60% alpha
                            currentGridView.setBackgroundColor(Color.argb(153,171,206,203));
                        }else {
                            Toast.makeText(getBaseContext(),"Maximum amount sounds selected", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
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
                category.setText(categories[itemPosition]);
                categoryWasSelected = true;
                selectSounds.setVisibility(View.VISIBLE);
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
        DownLoadAndPlayTask downloadTask = new DownLoadAndPlayTask();

        // filename is not set, a timestamp will be used instead
        if(serverFile.getFilename() == null){
            String filename = Calendar.getInstance().getTimeInMillis() + "";
            downloadTask.execute(serverFile.getLink(), filename);
        }else{
            downloadTask.execute(serverFile.getLink(), serverFile.getFilename());
        }

    }

    public void backToBubbles(View v){
        player.stopIfPlaying();
        player.releaseSoundPlayer();
        finish();
    }

    public void selectBubbles(View v){
        player.stopIfPlaying();
        changeToModeAdd();
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
        player.stopIfPlaying();

        if(categoryWasSelected | modeSearchSounds){
            if(modeSearchSounds && !modeAddSounds) {
                modeSearchSounds = false;
            }
            if(!modeAddSounds) {
                showCategoriesList();
                categoryWasSelected = false;
                category.setText(R.string.categories);
                selectSounds.setVisibility(View.GONE);
            }else {
                changeToModeNormal();
            }
        }else{
            super.onBackPressed();
        }
    }

    // make visible the clicked element
    private void selectLastElement(int position, View currentGridView){

        lastSelectedId = position;
        lastElementSelected = currentGridView;

    }

    /** copied from DownloadTask */
    private class DownLoadAndPlayTask extends AsyncTask<String, Void, String> {
        private final String DEBUG_TAG = "DownLoadAndPlayTask";

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
                invalidFile = false;
                serverFile.setPathLocalFile(path);
                player.setSound(serverFile.getPathLocalFile());
                player.playIfNotPlaying();

            }else {
                invalidFile = true;
                adapter.backToNormal(currentView);
                Toast.makeText(getBaseContext(),"Corrupted sound", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
