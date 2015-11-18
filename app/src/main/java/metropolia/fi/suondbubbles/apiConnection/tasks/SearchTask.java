package metropolia.fi.suondbubbles.apiConnection.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import metropolia.fi.suondbubbles.apiConnection.AsyncResponse;
import metropolia.fi.suondbubbles.apiConnection.JSONtoServerFile;
import metropolia.fi.suondbubbles.apiConnection.ServerConnection;
import metropolia.fi.suondbubbles.apiConnection.ServerFile;

/**
 * Created by alvarob on 25.9.2015.
 */
public class SearchTask extends AsyncTask<Object, Void, ServerFile[]> {
    public AsyncResponse delegate = null;

    protected ServerFile[] doInBackground(Object... params) {
        // get parameters
        ServerConnection serverConnection = (ServerConnection) params[0];
        String strToSearch = (String) params[1];
        // perform the search
        String searchResult = serverConnection.search(strToSearch);
        JSONtoServerFile jsoNtoServerFile = new JSONtoServerFile();
        ServerFile[] fileArray = null;
        // make an array of custom objects from the search JSON array
        try {
            fileArray = jsoNtoServerFile.parseJSON(new JSONArray(searchResult));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return fileArray;

    }

    protected void onPostExecute(ServerFile[] result) {
        try {
            delegate.processFinish(result);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("processFinish", e.getMessage());
        }
    }

}
