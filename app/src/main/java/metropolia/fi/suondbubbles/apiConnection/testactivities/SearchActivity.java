package metropolia.fi.suondbubbles.apiConnection.testactivities;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;

import metropolia.fi.suondbubbles.R;
import metropolia.fi.suondbubbles.apiConnection.AsyncResponse;
import metropolia.fi.suondbubbles.apiConnection.SearchTask;
import metropolia.fi.suondbubbles.apiConnection.ServerFile;

public class SearchActivity extends AppCompatActivity implements AsyncResponse {
    EditText search;
    Button search_btn, play_btn;
    TextView result;
    ServerFile[] filesArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        search = (EditText) findViewById(R.id.search);
        search_btn = (Button) findViewById(R.id.search_btn);
        play_btn = (Button) findViewById(R.id.play_btn);
        result = (TextView) findViewById(R.id.result);
        result.setMovementMethod(new ScrollingMovementMethod());

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // hide keyboard
                search.clearFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(search.getWindowToken(), 0);

                String nameTrack = search.getText().toString();
                if (nameTrack.length() > 0) {
                    searchTrack(nameTrack);
                } else {
                    Toast.makeText(SearchActivity.this, "Search Box empty", Toast.LENGTH_SHORT).show();
                }
                setPlayButton(play_btn);
            }
        });
        

    }

    @Override
    public void processFinish(Object result) {
        filesArray = (ServerFile[]) result;
        StringBuilder text = new StringBuilder();
        text.append("--Found " + filesArray.length + " items--\n");

        for(int i = 0; i < filesArray.length; i++){
            text.append(i+1 + " " + filesArray[i].getTitle() + " - " + filesArray[i].getSoundType() + " - " + filesArray[i].getCategory() + '\n');
        }
        this.result.setText(text.toString());
        //ServerFile file = new createServerFileFromJSON(result);
    }

    private void setPlayButton(Button b){
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int n = (int) Math.round(Math.random() * filesArray.length - 1);
                ServerFile track = filesArray[n];
                URL urlTrack = track.getLink();
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
        });
        b.setVisibility(View.VISIBLE);
    }

    protected void searchTrack(String name) {
        name = name.trim();
        SearchTask searchTask = new SearchTask();
        searchTask.delegate = this;
        searchTask.execute(name);
        Toast. makeText (getBaseContext(), "searching", Toast. LENGTH_LONG ).show();
    }


}
