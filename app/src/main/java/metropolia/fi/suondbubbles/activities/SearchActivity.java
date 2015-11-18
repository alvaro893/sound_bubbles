package metropolia.fi.suondbubbles.activities;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

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
import metropolia.fi.suondbubbles.apiConnection.tasks.DownloadTask;
import metropolia.fi.suondbubbles.apiConnection.tasks.SearchTask;

public class SearchActivity extends AppCompatActivity implements AsyncResponse {
    private EditText activity_search_et_search;
    private GridView activity_search_grid;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // don't use this activity if the user isn't logged yet
        if(!SoundBubbles.userIsLogged())
            SoundBubbles.openLoginActivity(this);
        // initialize views
        activity_search_et_search = (EditText) findViewById(R.id.search);
        activity_search_et_search.setOnEditorActionListener(setSearchActionListener());
        activity_search_grid = (GridView) findViewById(R.id.gridView);
        activity_search_btn_add = (Button) findViewById(R.id.add);

        // broad search in order to show something
        //performSearch(" ");
        setAddButton();

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

    private void playFile(ServerFile file){

        //Download file if not exits in external memory
        download(file);

        //set up MediaPlayer
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(file.getPathLocalFile());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showServerFileList() {
        filesList = new ArrayList<>(Arrays.asList(filesArray));
        Log.d("filesArray", filesList.toString());
        final ServerFilesArrayAdapter adapter;
        adapter = new ServerFilesArrayAdapter(this, filesList);
        this.activity_search_grid.setAdapter(adapter);

        // set click for every item
        this.activity_search_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View currentGridView, int position, long id) {
                ServerFile serverFile = filesList.get(position);
                playFile(serverFile);
                selectLastElement(position, currentGridView);
                Log.d("adapter", adapter.getName().getText().toString());
                // set pause button
                adapter.switchToButtonPause(currentGridView);
                adapter.getBtn_pause().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mediaPlayer.isPlaying()){
                            mediaPlayer.stop();
                        }
                        adapter.backToNormal(currentGridView);
                    }
                });
                // remove button when music is gone
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        adapter.backToNormal(currentGridView);
//                        if (adapter.backToNormal(currentGridView)) {
//                            //Toast.makeText(getBaseContext(), "stopped", Toast.LENGTH_LONG).show();
//                        }
                    }
                });
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
        Toast. makeText (getBaseContext(), "searching", Toast. LENGTH_LONG ).show();
    }

    @Override
    public void processFinish(Object result) {
        filesArray = (ServerFile[]) result;
        showServerFileList();
    }

    private void download(ServerFile serverFile){
        DownloadTask downloadTask = new DownloadTask();

        // filename is not set, a timestamp will be used instead
        if(serverFile.getFilename() == null){
            String filename = Calendar.getInstance().getTimeInMillis() + "";
            downloadTask.execute(serverFile.getLink(), filename);
        }else{
            downloadTask.execute(serverFile.getLink(), serverFile.getFilename());
        }
        try {
            serverFile.setPathLocalFile(downloadTask.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
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

                    Intent receivedIntent = getIntent();
                    float coordinates = receivedIntent.getFloatExtra(viewCoordinates, 0);
                    int receivedViewId = receivedIntent.getIntExtra(viewID, 0);


                    bundle = new Bundle();
                    bundle.putSerializable(selectedFile, filesList.get(lastSelectedId));
                    bundle.putFloat(viewCoordinates,coordinates);
                    bundle.putInt(viewID,receivedViewId);


                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(returnBundle,bundle);
                    setResult(Activity.RESULT_OK, returnIntent);
                    mediaPlayer.stop();
                    finish();
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
}
