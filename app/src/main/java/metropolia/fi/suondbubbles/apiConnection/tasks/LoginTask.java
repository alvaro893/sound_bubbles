package metropolia.fi.suondbubbles.apiConnection.tasks;

import android.os.AsyncTask;

import java.io.InputStream;

import metropolia.fi.suondbubbles.apiConnection.AsyncResponse;
import metropolia.fi.suondbubbles.apiConnection.NoApiKeyException;
import metropolia.fi.suondbubbles.apiConnection.ServerConnection;

/**
 * Created by Alvaro on 27/10/2015.
 */
public class LoginTask extends AsyncTask<String, Void, ServerConnection> {
    public AsyncResponse delegate = null;

    protected ServerConnection doInBackground(String ...params){

        ServerConnection serverConnection = new ServerConnection();
        serverConnection.switchToMuseumAPI();
        try {
            serverConnection.auth(params[0], params[1]);
        } catch (NoApiKeyException e) {
            e.printStackTrace();
        }finally {
            return serverConnection;
        }

    }

    protected void onPostExecute(ServerConnection result){
        try{
            delegate.processFinish(result);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
