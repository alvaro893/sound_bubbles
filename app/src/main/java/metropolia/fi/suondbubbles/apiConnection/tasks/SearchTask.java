package metropolia.fi.suondbubbles.apiConnection.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import metropolia.fi.suondbubbles.apiConnection.AsyncResponse;
import metropolia.fi.suondbubbles.apiConnection.ServerConnection;
import metropolia.fi.suondbubbles.apiConnection.ServerFile;

/**
 * Created by alvarob on 25.9.2015.
 */
public class SearchTask extends AsyncTask<Object, Void, ServerFile[]> {
    public AsyncResponse delegate = null;
    //private StringBuffer sb;

    protected ServerFile[] doInBackground(Object... params) {
        Log.d("params", ""+params.length);
        ServerConnection serverConnection = (ServerConnection) params[0];
        String strToSearch = (String) params[1];
        String searchResult = serverConnection.search(strToSearch);
        JSONArray jsonArray;
        ServerFile[] fileArray = null;
        try {
            jsonArray = new JSONArray(searchResult);
            fileArray = new ServerFile[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                fileArray[i] = new ServerFile(jsonArray.getJSONArray(i).getJSONObject(0));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSONException", e.getMessage());
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
