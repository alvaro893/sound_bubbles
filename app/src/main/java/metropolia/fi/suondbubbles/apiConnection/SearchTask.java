package metropolia.fi.suondbubbles.apiConnection;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by alvarob on 25.9.2015.
 */
public class SearchTask extends AsyncTask<String, Void, ServerFile[]> {
    public AsyncResponse delegate = null;
    private StringBuffer sb;

    protected ServerFile[] doInBackground(String... params) {
        ServerConnection serverConnection = new ServerConnection();
        String searchResult = serverConnection.search(params[0]);
        JSONArray jsonArray;
        ServerFile[] fileArray = null;
        try {
            jsonArray = new JSONArray(searchResult);
            fileArray = new ServerFile[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++){
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
        }catch (Exception e){
            e.printStackTrace();
            Log.d("processFinish", e.getMessage());
        }
    }

}
