package metropolia.fi.suondbubbles.apiConnection;


import android.net.Uri;

import java.net.URL;

/**
 * Created by alvarob on 30.9.2015.
 */
public class ServerConnection implements AsyncResponse {
    private String response;
    private String apiKey = null;
    final String url = "dev.mw.metropolia.fi/dianag/AudioResourceSpace/";



    public ServerConnection(){
        auth(null, null);
    }

    public void auth(String user, String pass) {
        // TODO: implement authentification
        String subUrl = "plugins/api_auth/auth.php";
        this.apiKey = "M4B-lnwO3clT-MGJmnMM1NGOpJF4q4YNxaBoQzLTjMx9dit4w1QoUZxO3LuVJeQWO03fxaNfdX38tMN1oJ_2ViQq7h_2e1hKcv_h_jAhYXPJJnMayzS-Ih6FcgwvBVaB";
        //if(this.apiKey.equals(null)) throw new NoApiKeyException();
    }

    public String search(String search){
        String subUrl = ("plugins/api_audio_search/index.php");
        Uri.Builder uri = new Uri.Builder();
        uri.scheme("http")
            .authority(this.url)
            .appendPath(subUrl)
                .appendQueryParameter("key", apiKey)
                .appendQueryParameter("link", "true")
                .appendQueryParameter("collection", "11")
                .appendQueryParameter("search", search);
        //search = search.replace(" ", "%20");
        //String url = "http://dev.mw.metropolia.fi/dianag/AudioResourceSpace/plugins/api_audio_search/index.php/?key=" + apiKey + "&link=true&collection=11&search=" + search;
        SearchTask searchTask = new SearchTask();
        searchTask.delegate = this;
        searchTask.execute(uri.toString());
        return null;

    }
    public void upload(){
        // TODO: implement upload
    }

    public void processFinish(String result){
        response = result;
    }

}
