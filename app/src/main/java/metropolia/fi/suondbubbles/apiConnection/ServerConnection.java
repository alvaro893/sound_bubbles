package metropolia.fi.suondbubbles.apiConnection;


import android.net.Uri;
import android.net.Uri.Builder;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by alvarob on 30.9.2015.
 */
public class ServerConnection {
    public String Lastresponse;
    public String status;
    private String apiKey = null;
    private String authority = "dev.mw.metropolia.fi";
    private String path = "dianag/AudioResourceSpace/plugins/";

    private Uri.Builder setUri(){
        Builder uri = new Uri.Builder()
                .scheme("http")
                .authority(this.authority)
                .path(this.path)
                .appendQueryParameter("key", apiKey);
        return uri;
    }

    private String doHttpGetRequest(String urlString){
        StringBuffer sb = new StringBuffer();

        try {
            Log.d("uriString", urlString);
            URL url = new URL(urlString);
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
            Log.d("response", sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            Lastresponse = sb.toString();
            return sb.toString();
        }
    }
    
    private String doHttpPostRequest(String urlString, HashMap<String,String> params){
        StringBuffer sb;
        String response = "some error happend";


        try {
            Log.d("uriString", urlString);
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true); // This set the request as a post request

            // payload to send
            urlConnection.setChunkedStreamingMode(0);
            OutputStream out = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write(new JSONObject(params).toString());
            Log.d("payload", writer.toString());
            writer.flush();
            writer.close();
            out.close();

            // response
            int responseCode=urlConnection.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK){
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader bf = new BufferedReader(new InputStreamReader(in));
                sb = new StringBuffer("");
                String line = "";
                while ((line = bf.readLine()) != null) {
                    sb.append(line);
                }
                bf.close();
                response = sb.toString();
            }else{
                response = "No response. code: " + responseCode;
            }
            Log.d("response", response);


        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            Lastresponse = response;
            return response;
        }
    }

    public ServerConnection(){
        try {
            auth("androidApp", "1uithread");
            status = "logged";
        } catch (NoApiKeyException e) {
            e.printStackTrace();
            status = "not logged";
        }
    }

    public String auth(String user, String pass) throws NoApiKeyException {
        Uri.Builder uri = setUri();
        uri.appendEncodedPath("api_auth/auth.php/");
        String builtUrl = uri.build().toString();
        // parameters
        HashMap<String,String> params = new HashMap<>();
        params.put("username", user);
        params.put("password", pass);

        JSONObject response;
        try {
            response = new JSONObject(doHttpPostRequest(builtUrl, params));
        }catch (Exception e){
            e.printStackTrace();
            throw new NoApiKeyException();
        }


        try {
            this.apiKey = response.getString("api_key");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(this.apiKey.equals(null)) throw new NoApiKeyException();
        return this.apiKey;
    }

    public String search(String search){

        Uri.Builder uri = setUri();
            uri.appendEncodedPath("api_audio_search/index.php/")
            .appendQueryParameter("link", "true")
            .appendQueryParameter("collection", "11")
            .appendQueryParameter("search", search.trim());

        String builtUrl = uri.build().toString();
        return doHttpGetRequest(builtUrl);
    }
    public void upload(){
        // TODO: implement upload
    }


}
