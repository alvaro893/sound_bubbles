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

import metropolia.fi.suondbubbles.Controllers.GridTouchController;
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

    /** Constants*/
    private final String DEBUG_TAG = "SearchActivity";
    private final String SELECTED_FILE = "SELECTED_FILE";
    private final int MAXIMUM_AMOUNT = 5;

    /** Views */
    private EditText activity_search_et_search;
    private GridView gridView;
    private GifImageView gifImageView;
    private TextView categoryTextView;
    private Button selectSoundsButton, backToBubbles, cancel, addSounds;

    /** Adapters */
    private CategoriesAdapter categoriesAdapter;

    /** Arrays*/
    private ServerFile[] filesArray;
    private ArrayList<ServerFile> filesList;
    private String[] categories;
    private ServerFilesArrayAdapter adapter;
    private boolean[] isSelected;
    private ArrayList<View> selectedViews;
    private ArrayList<ServerFile> selectedViewsServerFileArray, downloadServerFileArray;

    /** Integers */
    private int currentSelectedSounds;
    private int downloadedSounds;

    /** Booleans*/
    private boolean outsideCategoryScreen;
    private boolean downloadCompleted;
    private boolean loading;
    private boolean invalidFile;
    private boolean modeAddSounds;
    private boolean modeSearchSounds;


    private GridTouchController gridTouchController;
    private SoundPlayer player;
    private ServerFile serverFile;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // don't use this activity if the user isn't logged yet
        if (!SoundBubbles.userIsLogged())
            SoundBubbles.openLoginActivity(this);

        init();

        // categories
        getCategories();
        showCategoriesList();

    }

    private void init() {
        outsideCategoryScreen = false;
        downloadCompleted = true;
        invalidFile = false;
        modeAddSounds = false;
        modeSearchSounds = false;
        loading = false;

        selectedViewsServerFileArray = new ArrayList<>();
        downloadServerFileArray = new ArrayList<>();
        selectedViews = new ArrayList<>();

        gridTouchController = new GridTouchController();

        currentSelectedSounds = 0;

        initViews();
        initSoundPlayer();
        setListeners();
    }

    private void initViews() {
        activity_search_et_search = (EditText) findViewById(R.id.search);
        gridView = (GridView) findViewById(R.id.gridView);
        backToBubbles = (Button) findViewById(R.id.back_to_bubbles);
        selectSoundsButton = (Button) findViewById(R.id.select_sounds);
        cancel = (Button) findViewById(R.id.search_activity_cancel);
        addSounds = (Button) findViewById(R.id.search_activity_add_sounds);
        categoryTextView = (TextView) findViewById(R.id.textView_categories);
        gifImageView = new GifImageView(getBaseContext());
        gifImageView.setBackgroundResource(R.drawable.loading);

    }

    private void initSoundPlayer() {

        player = new SoundPlayer();

        player.setSoundPlayerListener(new SoundPlayer.SoundPlayerListener() {
            @Override
            public void onStarting() {
                startAnimation(gridTouchController.getCurrentTouchedView(), player);
                adapter.switchToProgressBarWithPause(gridTouchController.getCurrentTouchedView());
            }

            @Override
            public void onPausing() {
                adapter.backToNormal(gridTouchController.getCurrentTouchedView());
            }

            @Override
            public void onStopping() {
                adapter.backToNormal(gridTouchController.getCurrentTouchedView());

            }
        });
    }

    private void setListeners() {
        activity_search_et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                SoundBubbles.hideKeyboard(SearchActivity.this, v);
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    modeSearchSounds = true;
                    outsideCategoryScreen = true;
                    player.stopIfPlaying();
                    categoryTextView.setText(R.string.search_result);
                    performSearch(v.getText().toString());
                    return true;
                }
                return false;
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View currentGridView, int position, long id) {
                if (!outsideCategoryScreen) {
                    performSearch(categories[position]);
                    categoryTextView.setText(categories[position]);
                    outsideCategoryScreen = true;
                    selectSoundsButton.setVisibility(View.VISIBLE);
                } else {
                    if (downloadCompleted) {
                        if (!modeAddSounds) {
                            Log.d(DEBUG_TAG, "View position is: " + position);
                            gridTouchController.setTouchedView(currentGridView, position);

                            serverFile = filesList.get(position);
                            Log.d(DEBUG_TAG, serverFile.getLink());

                            playFile(serverFile);


                        } else {
                            if (isSelected[position]) {
                                isSelected[position] = false;
                                currentSelectedSounds -= 1;
                                selectedViews.remove(currentGridView);
                                selectedViewsServerFileArray.remove(filesList.get(position));
                                currentGridView.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.grid_border));
                            } else {
                                if (currentSelectedSounds < MAXIMUM_AMOUNT) {
                                    isSelected[position] = true;
                                    currentSelectedSounds += 1;
                                    selectedViews.add(currentGridView);
                                    selectedViewsServerFileArray.add(filesList.get(position));

                                    // passive aqua color with 60% alpha
                                    currentGridView.setBackgroundColor(Color.argb(153, 171, 206, 203));
                                } else {
                                    Toast.makeText(getBaseContext(), "Maximum amount sounds selected", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }
                    } else {
                        Toast.makeText(getBaseContext(), "download in progress, please wait", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }

    private void showCategoriesList() {

        gridView.setAdapter(categoriesAdapter);


    }

    private void getCategories() {
        categories = new String[0];
        CategoryTask categoryTask = new CategoryTask();
        categoryTask.execute();
        try {
            categories = categoryTask.get();
            categoriesAdapter = new CategoriesAdapter(this, categories);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        Log.d(DEBUG_TAG, "categories: " + Arrays.deepToString(categories));
    }

    // processFinish is called after this method automatically
    protected void performSearch(String search) {
        gridTouchController.resetAll();
        search = search.trim();
        SearchTask searchTask = new SearchTask();
        searchTask.delegate = this;
        changeToModeNormal();
        Log.d(DEBUG_TAG, "serverConnection: " + SoundBubbles.serverConnection.toString());
        searchTask.execute(SoundBubbles.serverConnection, search.trim());
        Toast.makeText(getBaseContext(), "Searching", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void processFinish(Object result) {
        showServerFileList((ServerFile[]) result);
    }


    /** Starts circle progress animation*/
    private void startAnimation(View currentView, SoundPlayer player) {
        ProgressBar progressBar = (ProgressBar) currentView.findViewById(R.id.progressBar);
        progressBar.setMax(player.getDuration());
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, progressBar.getMax()); // see this max value coming back here, we animale towards that value
        animation.setDuration(player.getDuration()); //in milliseconds
        animation.setInterpolator(new LinearInterpolator());
        animation.start();
    }

    private void playFile(ServerFile file) {

        if (gridTouchController.isTouchedViewSame()) {
            Log.d(DEBUG_TAG,"Touched view is same");
            if(!loading) {
                if (player.isSoundPlayerPlaying())
                    player.stopIfPlaying();
                else
                    player.playIfNotPlaying();
            }
            else{
                Toast.makeText(getBaseContext(),"loading, please wait", Toast.LENGTH_SHORT).show();
            }
        }
        else {

            if (gridTouchController.getPreviousTouchedView() != null) {
                adapter.backToNormal(gridTouchController.getPreviousTouchedView());
            }
            if (player.isSoundPlayerPlaying()) {
                player.stopIfPlaying();
            }

            //Download file if not exits in external memory
            downloadAndPlay(file);

        }

        gridTouchController.adjustPreviousTouchedView();
    }

    private void changeToModeAdd() {
        modeAddSounds = true;
        backToBubbles.setVisibility(View.GONE);
        selectSoundsButton.setVisibility(View.GONE);
        cancel.setVisibility(View.VISIBLE);
        addSounds.setVisibility(View.VISIBLE);
    }

    private void changeToModeNormal() {
        modeAddSounds = false;
        backToBubbles.setVisibility(View.VISIBLE);
        selectSoundsButton.setVisibility(View.VISIBLE);
        cancel.setVisibility(View.GONE);
        addSounds.setVisibility(View.GONE);

        if (isSelected != null) {
            Arrays.fill(isSelected, Boolean.FALSE);
            resetSelectedViews();
            currentSelectedSounds = 0;
        }
    }

    public void cancelAdding(View v) {
        changeToModeNormal();
    }

    private void resetSelectedViews() {
        for (int i = 0; i < selectedViews.size(); i++) {
            selectedViews.get(i).setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.grid_border));
        }
        selectedViews.clear();
        selectedViewsServerFileArray.clear();
    }

    public void addSounds(View v) {
        player.stopIfPlaying();
        downloadedSounds = 0;
        downloadServerFileArray.clear();

        if (selectedViewsServerFileArray.size() == 0) {
            Toast.makeText(SearchActivity.this, "Select some sound first",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getBaseContext(), "Downloading", Toast.LENGTH_SHORT).show();
            downloadCompleted = false;
            Log.d(DEBUG_TAG, "number of selected: " + selectedViewsServerFileArray.size());
            for(int i = 0; i < selectedViewsServerFileArray.size(); i++){
                downloadServerFileArray.add(selectedViewsServerFileArray.get(i));
                download(downloadServerFileArray.get(i), i);
            }

            changeToModeNormal();
        }
    }

    private void finishActivity(){
        Intent intent = new Intent();
        intent.putExtra(SELECTED_FILE, downloadServerFileArray);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void showServerFileList(ServerFile[] result) {
        filesArray = result;
        filesList = new ArrayList<>(Arrays.asList(filesArray));


        Log.v(DEBUG_TAG, filesList.toString());
        isSelected = new boolean[filesList.size()];

        adapter = new ServerFilesArrayAdapter(this, filesList);
        this.gridView.setAdapter(adapter);


    }




    private void downloadAndPlay(ServerFile serverFile) {
        DownLoadAndPlayTask downloadTask = new DownLoadAndPlayTask();

        // filename is not set, a timestamp will be used instead
        if (serverFile.getFilename() == null) {
            String filename = Calendar.getInstance().getTimeInMillis() + "";
            downloadTask.execute(serverFile.getLink(), filename);
        } else {
            downloadTask.execute(serverFile.getLink(), serverFile.getFilename());
        }

    }

    private void download(ServerFile serverFile , int index) {
        DownLoadTask downloadTask = new DownLoadTask();


        // filename is not set, a timestamp will be used instead
        if (serverFile.getFilename() == null) {
            String filename = Calendar.getInstance().getTimeInMillis() + "";
            downloadTask.execute(serverFile.getLink(), filename);
        } else {
            downloadTask.execute(serverFile.getLink(), serverFile.getFilename(), Integer.toString(index));
        }

    }

    public void backToBubbles(View v) {
        player.stopIfPlaying();
        finish();
    }

    public void selectBubbles(View v) {
        if(downloadCompleted){
            player.stopIfPlaying();
            changeToModeAdd();
        }
        else{
            Toast.makeText(getBaseContext(),"Download is not yet completed, please wait", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.releaseSoundPlayer();
    }

    @Override
    public void onBackPressed() {
        player.stopIfPlaying();
        gridTouchController.resetAll();

        if (outsideCategoryScreen | modeSearchSounds) {
            if (modeSearchSounds && !modeAddSounds) {
                modeSearchSounds = false;
            }
            if (!modeAddSounds) {
                showCategoriesList();
                outsideCategoryScreen = false;
                categoryTextView.setText(R.string.categories);
                selectSoundsButton.setVisibility(View.GONE);
            } else {
                changeToModeNormal();
            }
        } else {
            super.onBackPressed();
        }
    }


    private class DownLoadAndPlayTask extends AsyncTask<String, Void, String> {
        private final String DEBUG_TAG = "DownLoadAndPlayTask";

        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0];
            String filename = params[1];
            File file;
            String path = null;

            try {
                URL url = new URL(urlString);
                SoundFile soundFile = new SoundFile(url.openStream());
                file = soundFile.createFileInCache(SoundBubbles.getMainContext(), filename);
                path = file.getPath();
            } catch (Exception e) {
                Log.d(DEBUG_TAG, e.getClass().toString() + ":" + e.getMessage());
                e.printStackTrace();
            }


            return path;
        }

        @Override
        protected void onPreExecute() {
            adapter.switchToGifImage(gridTouchController.getCurrentTouchedView());
            loading = true;
        }

        @Override
        protected void onPostExecute(String path) {
            Log.d(DEBUG_TAG, "DOWNLOAD COMPLETED");
            loading = false;
            if (path != null) {
                invalidFile = false;
                serverFile.setPathLocalFile(path);
                player.setSound(serverFile.getPathLocalFile());
                player.playIfNotPlaying();

            } else {
                invalidFile = true;
                adapter.backToNormal(gridTouchController.getCurrentTouchedView());
                Toast.makeText(getBaseContext(), "Corrupted sound", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class DownLoadTask extends AsyncTask<String, Void, String> {
        private final String DEBUG_TAG = "DownLoadTask";
        private String name;
        private String index;

        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0];
            String filename = params[1];
            name = filename;
            index = params[2];
            File file;
            String path = null;

            try {
                URL url = new URL(urlString);
                SoundFile soundFile = new SoundFile(url.openStream());
                file = soundFile.createFileInCache(SoundBubbles.getMainContext(), filename);
                path = file.getPath();
            } catch (Exception e) {
                Log.d(DEBUG_TAG, e.getClass().toString() + ":" + e.getMessage());
                e.printStackTrace();
            }


            return path;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(String path) {
            Log.d(DEBUG_TAG, "DOWNLOAD COMPLETED");
            if (path != null) {
                downloadedSounds += 1;
                downloadServerFileArray.get(Integer.parseInt(index)).setPathLocalFile(path);
                if(downloadedSounds == downloadServerFileArray.size()){
                    Log.d(DEBUG_TAG, "ALL SOUNDS COMPLETED");
                    Toast.makeText(getBaseContext(), "Download complete", Toast.LENGTH_SHORT).show();
                    finishActivity();
                    downloadCompleted = true;
                }

            } else {
                Toast.makeText(getBaseContext(), "Corrupted sound: " + name, Toast.LENGTH_SHORT).show();
                downloadCompleted = true;
            }
        }
    }
}
