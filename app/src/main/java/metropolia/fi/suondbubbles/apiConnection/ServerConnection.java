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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Class that handles the access to the server using Http requests. The mail actions are
 * auth -> authentication: it gets the api key
 * search -> get a list of files
 * upload -> upload recording file from the phone
 * getCategories -> get categories dynamically from the server**/
public class ServerConnection {
    public String Lastresponse;
    private String message;
    public boolean isLogged = false;
    private String apiKey = null;
    private String authority = "dev.mw.metropolia.fi";
    private String path = "dianag/AudioResourceSpace/plugins/";
    private String DEBUG_TAG = getClass().getSimpleName();

    public ServerConnection(){

    }

    public String getMessage() {
        return message;
    }


    /** Get the Api key for an correct given user/password pair. The key is stored in @apiKey field
     * @param user
     * @param  pass
     * */
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
            message = apiKey;
            Log.d("ServerConnection", apiKey);
            if(this.apiKey.length() > 35){
                this.isLogged = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /** Sends a search string to the server using the collection that the user selected in the
     *  login screen.
     *  @param search the search
     *  @return The result of the search in a String JSON*/
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

    /** Performs an "multipart/form-data" POST request  sending a ServerFile object as the body of this request, including
     * both the metedata and the binary file itself. It uses ServerFileToParameters class for this
     * purpose.
     * @param serverFile The file to upload with its metadata*/
    public String upload(ServerFile serverFile) {
        String crlf = "\r\n";
        String twoHyphens = "--";
        String boundary =  "*****";

        Uri.Builder uri = setUri();
        uri.appendEncodedPath("api_upload/");

        // file to submit
        File file = new File(serverFile.getPathLocalFile());
        String attachmentFileName = file.getName();

        // create parameters from ServerFile
        ServerFiletoParameters serverFiletoParameters = new ServerFiletoParameters();
        HashMap params = serverFiletoParameters.createJSON(serverFile, apiKey);
        HttpURLConnection httpUrlConnection = null;


        try {
            URL url = new URL(uri.toString());
            httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setDoOutput(true);

            httpUrlConnection.setRequestMethod("POST");
            httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
            httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
            httpUrlConnection.setRequestProperty(
                    "Content-Type", "multipart/form-data;boundary=" + boundary);
            DataOutputStream request = new DataOutputStream(
                    httpUrlConnection.getOutputStream());

            // iterate through parameters
            Iterator it = params.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                String field = (String) pair.getKey();
                String value = (String) pair.getValue();
                it.remove(); // avoids a ConcurrentModificationException
                request.writeBytes(twoHyphens + boundary + crlf);
                request.writeBytes("Content-Disposition: form-data; name=\"" +
                        field + crlf);
                request.writeBytes(crlf);
                // parameter value
                Log.d(DEBUG_TAG, "value:" + value + "key:" + field);
                request.writeBytes(value);
                // end wrapper
                request.writeBytes(crlf);
                request.writeBytes(twoHyphens + boundary +
                        twoHyphens + crlf);
            }
            Log.d(DEBUG_TAG, "requestSoFar:"+request.toString());

            // Add the file
            request.writeBytes(twoHyphens + boundary + crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"" +
                    "userfile" + "\";filename=\"" +
                    attachmentFileName + "\"" + crlf);
            request.writeBytes(crlf);
            // convert file to bytes
            request.write(inputStreamToByteArray(new FileInputStream(file)));
            // end wrapper
            request.writeBytes(crlf);
            request.writeBytes(twoHyphens + boundary +
                    twoHyphens + crlf);

            // flush ouput buffer
            request.flush();
            request.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return getResponse(httpUrlConnection);
    }
    /** Return and array with the catagories used in this API */
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

    private Uri.Builder setUri(){
        Builder uri = new Uri.Builder()
                .scheme("http")
                .authority(this.authority)
                .path(this.path);
        return uri;
    }

    /** Makes an GET request
     * @param urlString the destiny and the parameters of the GET request
     * @return it returns a String JSON response*/
    private String doHttpGetRequest(String urlString){
        StringBuffer sb = new StringBuffer();

        try {
            Log.d("uriString", urlString);
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

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
    /** Makes an POST request using a JSON body. It can't be used to upload a file, use method
     * upload() instead.
     * @param urlString the url of the post request destiny
     * @param params the parameters of the post request payload (will be converted to JSON)*/
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
            String jsonPayload = new JSONObject(params).toString();
            Log.d(DEBUG_TAG, "json-payload:"+jsonPayload);
            writer.write(jsonPayload);
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

    /** converts a file into an array of bytes
     * @param inStream the stream of the file **/
    private byte[] inputStreamToByteArray(InputStream inStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = inStream.read(buffer)) > 0) {
            baos.write(buffer, 0, bytesRead);
        }
        inStream.close();
        return baos.toByteArray();
    }

    /** gets the reponse from a request used by HttpURLConnection **/
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
        Log.d(DEBUG_TAG, "response:"+response);
        return response;
    }

    /** switches to use the museo url instead the metropolia test one without changing any line code **/
    public void switchToMuseumAPI(){
        this.authority = "resourcespace.tekniikanmuseo.fi";
        this.path = "plugins";
    }

}
