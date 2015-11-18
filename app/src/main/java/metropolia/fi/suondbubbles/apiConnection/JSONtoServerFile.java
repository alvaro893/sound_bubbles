package metropolia.fi.suondbubbles.apiConnection;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by alvarob on 16.11.2015.
 */
public class JSONtoServerFile {
    public ServerFile[] parseJSON(JSONArray jsonArray){
        ServerFile[] fileArray = null;
        try {
            fileArray = new ServerFile[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONArray(i).getJSONObject(0);
                fileArray[i] = createServerFileFromJSON(json);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSONException", e.getMessage());
        }

        return fileArray;
    }
    public ServerFile createServerFileFromJSON(JSONObject json){
            // TODO: add missing fields to constructor ?
            ServerFile file = new ServerFile();

            Log.d("checkServerFile", json.toString());
            try {
                if (json.has("Title")) {
                    file.setTitle(json.getString("Title"));
                    if(file.getTitle().isEmpty())
                        file.setTitle("((No title))");
                } else {
                    file.setTitle("((No title))");
                }
                if (json.has("Original filename")) {
                    file.setFilename(json.getString("Original filename"));
                }else{
                    file.setFilename(null);
                }
                if (json.has("Sound Type")) {
                    file.setSoundType(json.getString("Sound Type"));
                }else{
                    file.setSoundType(null);
                }
                if (json.has("Category")) {
                    file.setCategory(json.getString("Category"));
                    if(file.getCategory().isEmpty())
                        file.setCategory("((missed category))");
                }else{
                    file.setCategory("((missed category))");
                }
                if (json.has("File extension")) {
                    file.setFileExtension(json.getString("File extension"));
                }else{
                    file.setFileExtension(null);
                }
                if (json.has("File size(KB))")) {
                    file.setFileSize(json.getDouble("File size(KB)"));
                }else{
                    file.setFileSize(0);
                }
                if (json.has("Collection name")) {
                    file.setCollectionName(json.getString("Collection name"));
                }else{
                    file.setCollectionName(null);
                }
                if (json.has("Length (sec)")) {
                    String length = json.getString("Length (sec)");
                    if(!length.isEmpty())
                        file.setLength(Integer.parseInt(length));
                    else
                        file.setLength(0);
                }else{
                    file.setLength(0);
                }
                if (json.has("Collection ID")) {
                    file.setCollectionID(json.getInt("Collection ID"));
                }else{
                    file.setCollectionID(0);
                }
                if (json.has("Download link")) {
                    file.setLink(json.getString("Download link"));
                }else{
                    file.setLink(null);
                }

            }catch (Exception e){
                Log.e(this.getClass().getName(), "error in parsing json: "+e.getMessage());
            }
        return file;
    }
}
