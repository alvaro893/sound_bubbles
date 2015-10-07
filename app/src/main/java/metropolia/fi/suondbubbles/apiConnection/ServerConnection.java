package metropolia.fi.suondbubbles.apiConnection;


import android.net.Uri;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.net.Uri.Builder;
import android.util.Log;

/**
 * Created by alvarob on 30.9.2015.
 */
public class ServerConnection {
    public String Lastresponse;
    private String apiKey = null;
    private String authority = "dev.mw.metropolia.fi";
    private String path = "dianag/AudioResourceSpace/plugins/api_audio_search/index.php";



    public ServerConnection(){
        auth(null, null);
    }

    public String auth(String user, String pass) {
        // TODO: implement authentification
        String subUrl = "plugins/api_auth/auth.php";
        this.apiKey = "M4B-lnwO3clT-MGJmnMM1NGOpJF4q4YNxaBoQzLTjMx9dit4w1QoUZxO3LuVJeQWO03fxaNfdX38tMN1oJ_2ViQq7h_2e1hKcv_h_jAhYXPJJnMayzS-Ih6FcgwvBVaB";
        //if(this.apiKey.equals(null)) throw new NoApiKeyException();
        return this.apiKey;
    }

    public String search(String search){
        Builder uri = new Uri.Builder()
            .scheme("http")
            .authority(this.authority)
            .path(this.path)
            .appendQueryParameter("key", apiKey)
            .appendQueryParameter("link", "true")
            .appendQueryParameter("collection", "11")
            .appendQueryParameter("search", search);

        String builtUrl = uri.build().toString();


        //search = search.replace(" ", "%20");
        //String url = "http://dev.mw.metropolia.fi/dianag/AudioResourceSpace/plugins/api_audio_search/index.php/?key=" + apiKey + "&link=true&collection=11&search=" + search;
//

        StringBuffer sb = new StringBuffer();

        try {
            Log.d("uriString", builtUrl);
            URL url = new URL(builtUrl);
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
            Lastresponse = sb.toString();
            return sb.toString();
        }
    }
    public void upload(){
        // TODO: implement upload
    }


}
