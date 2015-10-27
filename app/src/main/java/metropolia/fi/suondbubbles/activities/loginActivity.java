package metropolia.fi.suondbubbles.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import metropolia.fi.suondbubbles.R;
import metropolia.fi.suondbubbles.apiConnection.AsyncResponse;

public class LoginActivity extends AppCompatActivity implements AsyncResponse {
    Button activity_login_btn;
    EditText activity_login_et_user, activity_login_et_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        activity_login_btn = (Button) findViewById(R.id.go_button);
        activity_login_et_user = (EditText) findViewById(R.id.username);
        activity_login_et_pass = (EditText) findViewById(R.id.password);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
    public void processFinish(Object result) {

    }
}
