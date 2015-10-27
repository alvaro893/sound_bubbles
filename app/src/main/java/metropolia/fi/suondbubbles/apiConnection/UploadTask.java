package metropolia.fi.suondbubbles.apiConnection;

import android.os.AsyncTask;

import java.io.InputStream;

/**
 * Created by alvarob on 11.10.2015.
 */
public class UploadTask extends AsyncTask<Object, Void, String> {

    public AsyncResponse delegate = null;

    protected String doInBackground(Object ...params){
        //ServerFile file = (ServerFile) params[0];
        InputStream inputStream = (InputStream) params[0];

        ServerConnection serverConnection = new ServerConnection();

        return serverConnection.upload(inputStream);
    }

    protected void onPostExecute(String result){
        delegate.processFinish(result);
    }
}
