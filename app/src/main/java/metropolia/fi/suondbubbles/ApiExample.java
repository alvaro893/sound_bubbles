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
                if(nameTrack.length() > 0){
                    searchTrack(nameTrack);
                }else{
                    Toast. makeText(ApiExample.this, "Search Box empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_api_example, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void processFinish(String result) {
        this.result.setText(result);
    }

    protected void searchTrack(String name) {
        name = name.replace(" ", "%20");
        String url = "http://dev.mw.metropolia.fi/dianag/AudioResourceSpace/plugins/api_audio_search/index.php/?key=M4B-lnwO3clT-MGJmnMM1NGOpJF4q4YNxaBoQzLTjMx9dit4w1QoUZxO3LuVJeQWO03fxaNfdX38tMN1oJ_2ViQq7h_2e1hKcv_h_jAhYXPJJnMayzS-Ih6FcgwvBVaB&link=true&collection=11&search=" + name;
        SearchTask searchTask = new SearchTask();
        searchTask.delegate = this;
        searchTask.execute(url);
        Toast. makeText (getBaseContext(), "searching" ,
                Toast. LENGTH_LONG )
                .show();
    }
}
