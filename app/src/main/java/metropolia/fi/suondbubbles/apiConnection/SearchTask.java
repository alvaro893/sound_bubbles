package metropolia.fi.suondbubbles.apiConnection;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import metropolia.fi.suondbubbles.apiConnection.AsyncResponse;

/**
 * Created by alvarob on 25.9.2015.
 */
public class SearchTask extends AsyncTask<String, Void, String> {
    public AsyncResponse delegate = null;
    private StringBuffer sb;

    protected String doInBackground(String... params) {
        ServerConnection serverConnection = new ServerConnection();
        String searchResult = serverConnection.search(params[0]);
        return searchResult;
    }

    protected void onPostExecute(String result) {
        delegate.processFinish(result);
    }

}
