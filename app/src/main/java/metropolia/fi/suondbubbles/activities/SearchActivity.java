package metropolia.fi.suondbubbles.activities;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import metropolia.fi.suondbubbles.R;
import metropolia.fi.suondbubbles.adapters.RawFilesAdapter;
import metropolia.fi.suondbubbles.adapters.ServerFilesArrayAdapter;
import metropolia.fi.suondbubbles.apiConnection.AsyncResponse;
import metropolia.fi.suondbubbles.apiConnection.SearchTask;
import metropolia.fi.suondbubbles.apiConnection.ServerConnection;
import metropolia.fi.suondbubbles.apiConnection.ServerFile;

public class SearchActivity extends AppCompatActivity implements AsyncResponse {
    private EditText activity_search_et_search;
    private GridView activity_search_grid;
    //private Button search_btn, play_btn;
    //private TextView result;
    private ServerFile[] filesArray;
    private ArrayList<ServerFile> filesList;
    private ServerConnection serverConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            new Exception();
        } else {
            serverConnection = (ServerConnection) extras.getSerializable("ServerConnection");
        }
        //serverConnection = (ServerConnection) getIntent().getSerializableExtra("ServerConnection");
        activity_search_et_search = (EditText) findViewById(R.id.search);
        activity_search_et_search.setOnEditorActionListener(setSearchActionListener());
        activity_search_grid = (GridView) findViewById(R.id.gridView);
        // broad search in order to show something
        performSearch(" ");
        //result.setMovementMethod(new ScrollingMovementMethod());

//        search_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // hide keyboard
//                activity_search_et_search.clearFocus();
//                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(activity_search_et_search.getWindowToken(), 0);
//
//                String nameTrack = activity_search_et_search.getText().toString();
//                if (nameTrack.length() > 0) {
//                    performSearch(nameTrack);
//                } else {
//                    Toast.makeText(SearchActivity.this, "Search Box empty", Toast.LENGTH_SHORT).show();
//                }
//                setPlayButton(play_btn);
//            }
//        });
        

    }


    private TextView.OnEditorActionListener setSearchActionListener(){
        return new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch(v.getText().toString());
                    return true;
                }
                return false;
            }
        };
    }

    //TODO: convert this in media player for every row
    private void playFile(ServerFile file){

        URL urlTrack = file.getLink();
        //set up MediaPlayer
        MediaPlayer mp = new MediaPlayer();
        try {
            mp.setDataSource(SearchActivity.this, Uri.parse(urlTrack.toString()));
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showList() {
        filesList = new ArrayList<>(Arrays.asList(filesArray));
        Log.d("filesArray", filesList.toString());
        ServerFilesArrayAdapter adapter;
        adapter = new ServerFilesArrayAdapter(this, filesList);
        this.activity_search_grid.setAdapter(adapter);

        // set click for every item
        this.activity_search_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ServerFile serverFile = filesList.get(position);
                playFile(serverFile);
            }

        });
    }

    // processFinish is called after this method automatically
    protected void performSearch(String search) {
        search = search.trim();
        SearchTask searchTask = new SearchTask();
        searchTask.delegate = this;
        Log.d("serverConnection", serverConnection.toString());
        searchTask.execute(serverConnection, search.trim());
        Toast. makeText (getBaseContext(), "searching", Toast. LENGTH_LONG ).show();
    }

    @Override
    public void processFinish(Object result) {
        filesArray = (ServerFile[]) result;
        showList();


//        StringBuilder text = new StringBuilder();
//        text.append("--Found " + filesArray.length + " items--\n");
//
//        for(int i = 0; i < filesArray.length; i++){
//            text.append(i+1 + " " + filesArray[i].getTitle() + " - " + filesArray[i].getSoundType() + " - " + filesArray[i].getCategory() + '\n');
//        }
//        this.result.setText(text.toString());
        //ServerFile file = new createServerFileFromJSON(result);
    }


}
