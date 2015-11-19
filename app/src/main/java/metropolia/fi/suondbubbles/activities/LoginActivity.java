package metropolia.fi.suondbubbles.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import metropolia.fi.suondbubbles.R;
import metropolia.fi.suondbubbles.apiConnection.AsyncResponse;
import metropolia.fi.suondbubbles.apiConnection.CollectionID;
import metropolia.fi.suondbubbles.apiConnection.ServerConnection;
import metropolia.fi.suondbubbles.apiConnection.tasks.LoginTask;

public class LoginActivity extends AppCompatActivity implements AsyncResponse {

    private static final String PREFS_NAME = "remember_prefs";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_PASSWORD = "password";
    private static final String PREF_COLLECTION_ID = "collection_ID";
    private static final String CHECKED = "checked";
    private static final String DEBUG_TAG = "LoginActivity";

    private Button activity_login_btn;
    private EditText activity_login_et_user, activity_login_et_pass, activity_login_et_collection;
    private Intent intentMainSurfaceActivity;
    private String collection;
    private CheckBox remember_checkbox;
    private boolean checked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        checkHasLogged();
        activity_login_btn = (Button) findViewById(R.id.go_button);
        activity_login_et_user = (EditText) findViewById(R.id.username);
        activity_login_et_pass = (EditText) findViewById(R.id.password);
        activity_login_et_collection = (EditText) findViewById(R.id.collection_id);
        remember_checkbox = (CheckBox)findViewById(R.id.remember_checkBox);
        intentMainSurfaceActivity = new Intent(this, MainSurfaceActivity.class);

        loadPreferences();

        // button click listener
        activity_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = activity_login_et_user.getText().toString();
                String pass = activity_login_et_pass.getText().toString();
                collection = activity_login_et_collection.getText().toString();
                if (isFormValid()) {

                    if(remember_checkbox.isChecked()){
                        savePreferences(user, pass, collection, true);
                    }else {
                        savePreferences(null, null, null, false);
                    }

                    if(isNetworkAvailable()) {
                        login(user, pass);
                    }else {
                        Toast.makeText(getBaseContext(), R.string.no_internet_connection_activity_login, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void loadPreferences() {
        SharedPreferences pref = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        String username = pref.getString(PREF_USERNAME, null);
        String password = pref.getString(PREF_PASSWORD, null);
        String collection = pref.getString(PREF_COLLECTION_ID, null);
        boolean checked = pref.getBoolean(CHECKED, false);

        if(checked){
            activity_login_et_user.setText(username);
            activity_login_et_pass.setText(password);
            activity_login_et_collection.setText(collection);
            remember_checkbox.setChecked(true);
        }
    }

    public void savePreferences(String user, String pass, String collection, boolean remember_checkbox){
        getSharedPreferences(PREFS_NAME,MODE_PRIVATE)
                .edit()
                .putString(PREF_USERNAME, user)
                .putString(PREF_PASSWORD, pass)
                .putString(PREF_COLLECTION_ID,collection)
                .putBoolean(CHECKED, remember_checkbox)
                .commit();
    }


    public void login(String user, String pass){
        LoginTask loginTask = new LoginTask();
        loginTask.delegate = this;
        loginTask.execute(user, pass); // when the asynctask is finished the processFinish method is executed
      //  Toast.makeText(this, R.string.logging_activity_login, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void processFinish(Object result) {
        // save ServerConnection object
        SoundBubbles.serverConnection = (ServerConnection) result;


        if(SoundBubbles.userIsLogged()){
            CollectionID.setCollectionID(collection);
            startActivity(intentMainSurfaceActivity);
            Toast.makeText(this, R.string.logsucess_activity_login, Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(this, ((ServerConnection) result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isFormValid(){
        boolean isUserEmpty = this.activity_login_et_user.getText().toString().isEmpty();
        boolean isPassEmpty = this.activity_login_et_pass.getText().toString().isEmpty();
        boolean isCollectionEmpty = this.activity_login_et_collection.getText().toString().isEmpty();
        if(isUserEmpty){
            this.activity_login_et_user.setError(getString(R.string.emptyfiled_activity_login));
        }
        if(isPassEmpty){
            this.activity_login_et_pass.setError(getString(R.string.emptyfiled_activity_login));
        }
        if(isUserEmpty){
            this.activity_login_et_collection.setError(getString(R.string.emptyfiled_activity_login));
        }
        if(isPassEmpty || isUserEmpty || isCollectionEmpty)
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


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
