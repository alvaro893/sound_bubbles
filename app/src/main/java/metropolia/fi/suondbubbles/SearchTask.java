package metropolia.fi.suondbubbles;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

/**
 * Created by alvarob on 25.9.2015.
 */
public class SearchTask extends AsyncTask<String, Void, String> {
    public AsyncResponse delegate = null;
    private StringBuffer sb;

    protected String doInBackground(String... params) {
        try {
        URL url = new URL(params[0]);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            //readStream(in);

            BufferedReader bf = new BufferedReader(new InputStreamReader(in));
            sb = new StringBuffer("");
            String line = "";
            while ((line = bf.readLine()) != null) {
                sb.append(line);
            }
            bf.close();
            System. out .println(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }finally {

        }
        return sb.toString();
    }

    protected void onPostExecute(String result) {
        delegate.processFinish(result);
    }

}
