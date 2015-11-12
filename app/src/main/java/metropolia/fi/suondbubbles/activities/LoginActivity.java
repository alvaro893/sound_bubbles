package metropolia.fi.suondbubbles.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import metropolia.fi.suondbubbles.R;
import metropolia.fi.suondbubbles.apiConnection.AsyncResponse;
import metropolia.fi.suondbubbles.apiConnection.LoginTask;
import metropolia.fi.suondbubbles.apiConnection.ServerConnection;

public class LoginActivity extends AppCompatActivity implements AsyncResponse {
    private Button activity_login_btn;
    private EditText activity_login_et_user, activity_login_et_pass;
    private Intent intentMainSurfaceActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        checkHasLogged();
        activity_login_btn = (Button) findViewById(R.id.go_button);
        activity_login_et_user = (EditText) findViewById(R.id.username);
        activity_login_et_pass = (EditText) findViewById(R.id.password);
        intentMainSurfaceActivity = new Intent(this, MainSurfaceActivity.class);

        // button click listener
        activity_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = LoginActivity.this.activity_login_et_user.getText().toString();
                String pass = LoginActivity.this.activity_login_et_pass.getText().toString();
                if (isFormValid()) {
                    login(user, pass);
                }
            }
        });

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

    public void login(String user, String pass){
        LoginTask loginTask = new LoginTask();
        loginTask.delegate = this;
        loginTask.execute(user, pass); // when the asynctask is finished the processFinish method is executed
        Toast.makeText(this, R.string.logging_activity_login, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void processFinish(Object result) {
        // save ServerConnection object
        SoundBubbles.serverConnection = (ServerConnection) result;

        if(SoundBubbles.userIsLogged()){
            startActivity(intentMainSurfaceActivity);
            Toast.makeText(this, R.string.logsucess_activity_login, Toast.LENGTH_LONG).show();

        }else{
            Toast.makeText(this, R.string.nologin_activity_login, Toast.LENGTH_LONG).show();
        }
    }

    private boolean isFormValid(){
        boolean isUserEmpty = this.activity_login_et_user.getText().toString().isEmpty();
        boolean isPassEmpty = this.activity_login_et_pass.getText().toString().isEmpty();
        if(isUserEmpty){
            this.activity_login_et_user.setError(getString(R.string.emptyfiled_activity_login));
        }
        if(isPassEmpty){
            this.activity_login_et_pass.setError(getString(R.string.emptyfiled_activity_login));
        }
        if(isPassEmpty || isUserEmpty)
            return false;
        else
            return true;
    }

    private void checkHasLogged(){
        if(SoundBubbles.serverConnection == null){
            return;
        }
        if(!SoundBubbles.serverConnection.isLogged){
            startActivity(this.intentMainSurfaceActivity);
        }
    }
}
