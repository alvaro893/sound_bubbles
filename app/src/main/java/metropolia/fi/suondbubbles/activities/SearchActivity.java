package metropolia.fi.suondbubbles.activities;

import android.media.MediaPlayer;
import android.net.Uri;
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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import metropolia.fi.suondbubbles.R;
import metropolia.fi.suondbubbles.adapters.ServerFilesArrayAdapter;
import metropolia.fi.suondbubbles.apiConnection.AsyncResponse;
import metropolia.fi.suondbubbles.apiConnection.SearchTask;
import metropolia.fi.suondbubbles.apiConnection.ServerFile;

public class SearchActivity extends AppCompatActivity implements AsyncResponse {
    private EditText activity_search_et_search;
    private GridView activity_search_grid;
    private Button activity_search_btn_cancel, activity_search_btn_add;
    private ServerFile[] filesArray;
    private ArrayList<ServerFile> filesList;
    private MediaPlayer mediaPlayer;
    private int lastSelected;

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
        activity_search_btn_cancel = (Button) findViewById(R.id.cancel);
        activity_search_btn_add = (Button) findViewById(R.id.add);

        // broad search in order to show something
        performSearch(" ");
        setCancelButton();

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

        URL urlTrack = file.getLink();
        //set up MediaPlayer
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(SearchActivity.this, Uri.parse(urlTrack.toString()));
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showList() {
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
                lastSelected = position;
                // change background
                currentGridView.setBackground(ContextCompat.getDrawable(
                        SearchActivity.this, R.drawable.grid_border_selected));
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
                        if(adapter.backToNormal(currentGridView)){
                            Toast. makeText (getBaseContext(), "stopped", Toast. LENGTH_LONG ).show();
                        }
                    }
                });
            }

        });
    }

    // processFinish is called after this method automatically
    protected void performSearch(String search) {
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
        showList();
    }

    private void setCancelButton(){
        this.activity_search_btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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


}
