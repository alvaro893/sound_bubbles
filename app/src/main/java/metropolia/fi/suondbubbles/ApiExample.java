package metropolia.fi.suondbubbles;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import metropolia.fi.suondbubbles.apiConnection.AsyncResponse;
import metropolia.fi.suondbubbles.apiConnection.SearchTask;
import metropolia.fi.suondbubbles.apiConnection.ServerConnection;
import metropolia.fi.suondbubbles.apiConnection.ServerFile;

public class ApiExample extends AppCompatActivity implements AsyncResponse {
    EditText search;
    Button search_btn;
    TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_example);
        search = (EditText) findViewById(R.id.search);
        search_btn = (Button) findViewById(R.id.search_btn);
        result = (TextView) findViewById(R.id.result);

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameTrack = search.getText().toString();
                if (nameTrack.length() > 0) {
                    searchTrack(nameTrack);
                } else {
                    Toast.makeText(ApiExample.this, "Search Box empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void processFinish(Object result) {
        ServerFile[] filesArray = (ServerFile[]) result;
        StringBuilder text = new StringBuilder();
        text.append("--Found " + filesArray.length + " items--\n");

        for(int i = 0; i < filesArray.length; i++){
            text.append(i+1 + " " + filesArray[i].getTitle() + " - " + filesArray[i].getSoundType() + " - " + filesArray[i].getCategory() + '\n');
        }
        this.result.setText(text.toString());
        //ServerFile file = new createServerFileFromJSON(result);
    }

    private ServerFile CreateServerFileFromJSON(String json){
        return null;
    }

    protected void searchTrack(String name) {
        name = name.trim();
        SearchTask searchTask = new SearchTask();
        searchTask.delegate = this;
        searchTask.execute(name);
        Toast. makeText (getBaseContext(), "searching", Toast. LENGTH_LONG ).show();
    }


}
