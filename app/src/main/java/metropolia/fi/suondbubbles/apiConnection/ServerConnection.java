package metropolia.fi.suondbubbles.apiConnection;


import android.net.Uri;
import android.net.Uri.Builder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;


public class ServerConnection {
    public String Lastresponse;
    public boolean isLogged = false;
    private String apiKey = null;
    private String authority = "dev.mw.metropolia.fi";
    private String path = "dianag/AudioResourceSpace/plugins/";

    private Uri.Builder setUri(){
        Builder uri = new Uri.Builder()
                .scheme("http")
                .authority(this.authority)
                .path(this.path);
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
            // TODO: implement upload file in post
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

    }

    public void auth(String user, String pass) throws NoApiKeyException {
        Uri.Builder uri = setUri();
        uri.appendEncodedPath("api_auth/auth.php/");
        String builtUrl = uri.build().toString();
        // parameters to send
        HashMap<String,String> params = new HashMap<>();
        params.put("username", user.trim());
        params.put("password", pass.trim());

        JSONObject response;
        try {
            response = new JSONObject(doHttpPostRequest(builtUrl, params));
        }catch (Exception e){
            throw new NoApiKeyException();
        }


        try {
            this.apiKey = response.getString("api_key");
            Log.d("ServerConnection", apiKey);
            if(this.apiKey.length() > 35){
                this.isLogged = true;
                throw new NoApiKeyException();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String search(String search){

        Uri.Builder uri = setUri();
        uri.appendEncodedPath("api_audio_search/index.php/")
                .appendQueryParameter("key", apiKey)
                .appendQueryParameter("link", "true")
                .appendQueryParameter("collection", CollectionID.getCollectionID())
                .appendQueryParameter("search", search.trim());

        String builtUrl = uri.build().toString();
        return doHttpGetRequest(builtUrl);
    }
    public String upload(InputStream in){
        Log.d("testupdate", "inside upload method");
        ServerFile file = new ServerFile();
        Uri.Builder uri = setUri();
        uri.appendEncodedPath("api_upload/");
        String builtUrl = uri.build().toString();

        // parameters to send
        HashMap<String,String> params = new HashMap<>();
        params.put("key", apiKey);
        params.put("resourcetype", "4");
//        params.put("collection", Integer.toString(file.getCollectionID()));
//        params.put("field75", file.getCategory());
//        params.put("field76", file.getSoundType());
//        params.put("field73", file.getDescription());
//        params.put("field74", file.getTags());
//        params.put("field8", file.getTitle());
//        params.put("field77", Double.toString(file.getLat()));
//        params.put("field79", Double.toString(file.getLat()));
        // for testing
        params.put("collection", CollectionID.getCollectionID());
        params.put("field75", "machine");
        params.put("field76","effects" );
        params.put("field73","description" );
        params.put("field8","darth voice" );


        // return doHttpPostRequest()
        //return doHttpPostRequest(builtUrl, params);
        return doHttpPostUpload(builtUrl, in, params);
    }

    private String doHttpPostUpload(String builtUrl, InputStream in, HashMap params){
        //dummy file
        String attachmentName = "myfile";
        Log.d("testupdate", attachmentName);
        String attachmentFileName = "myfile.mp3";
        String crlf = "\r\n";
        String twoHyphens = "--";
        String boundary =  "*****";
        URL url = null;
        try {
            url = new URL(builtUrl);
        }catch (Exception e){
            e.printStackTrace();
        }
        //end dummy file

        //Set up request
        HttpURLConnection httpUrlConnection = null;
        try{
            httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setDoOutput(true);

            httpUrlConnection.setRequestMethod("POST");
            httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
            httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
            httpUrlConnection.setRequestProperty(
                    "Content-Type", "multipart/form-data;boundary=" + boundary);

            httpUrlConnection.setChunkedStreamingMode(0);
            OutputStream out = httpUrlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write(new JSONObject(params).toString());
            Log.d("payload", writer.toString());
            writer. close();
        }catch (Exception e){
            e.printStackTrace();
        }

        //Start content wrapper
        DataOutputStream request = null;
        try{
            request = new DataOutputStream(
                    httpUrlConnection.getOutputStream());

            request.writeBytes(twoHyphens + boundary + crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"" +
                    attachmentName + "\";filename=\"" +
                    attachmentFileName + "\"" + crlf);
            request.writeBytes(crlf);

            // do the upload itself
            request.write(inputStreamToByteArray(in));

            //End content wrapper
            request.writeBytes(crlf);
            request.writeBytes(twoHyphens + boundary +
                    twoHyphens + crlf);

            Log.d("testupdate", "start content wrapper");


        }catch (Exception e){
            e.printStackTrace();
        }finally {

            //Flush Output buffer and close streams
            try{
                request.flush();
                request.close();
                in.close();
                httpUrlConnection.disconnect();

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        // response from server
        return getResponse(httpUrlConnection);

    }

    private byte[] inputStreamToByteArray(InputStream inStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = inStream.read(buffer)) > 0) {
            baos.write(buffer, 0, bytesRead);
        }
        return baos.toByteArray();
    }

    private String getResponse(HttpURLConnection httpUrlConnection){
        String response = null;
        try{
            InputStream responseStream = new
                    BufferedInputStream(httpUrlConnection.getInputStream());

            BufferedReader responseStreamReader =
                    new BufferedReader(new InputStreamReader(responseStream));

            String line = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = responseStreamReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            responseStreamReader.close();

            response = stringBuilder.toString();
            responseStream.close();
        }catch (IOException e){
            e.printStackTrace();
        }

        return response;
    }

    public String[] getCategories(){
        Uri.Builder uri = setUri();
        uri.appendEncodedPath("api_upload/help_options.php");
        String category = "Category";
        String response = doHttpGetRequest(uri.build().toString());
        JSONArray jsonArray;
        JSONObject jsonObject = null;
        String categories = null;
        try {
            jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = (jsonArray.getJSONArray(i).getJSONObject(0));
                if(jsonObject.has(category))
                    break;
            }
            categories = jsonObject.getString(category);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSONException", e.getMessage());
        }
        return categories.split(",");
    }

    public void switchToMuseumAPI(){
        this.authority = "resourcespace.tekniikanmuseo.fi";
        this.path = "plugins";
    }

}
