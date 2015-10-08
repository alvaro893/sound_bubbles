package metropolia.fi.suondbubbles.apiConnection;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

/**
 * This class should recive the JSON
 */
public class ServerFile {
    private String title, filename, category, soundType, fileExtension, collectionName;
    private int lenght, collectionID;
    private double fileSize;
    private URL link;

    public ServerFile(JSONObject json)
    {
        Log.d("checkServerFile", json.toString());
        try {
            if (json.has("Title")) {
                this.title = json.getString("Title");
            } else {
                this.title = null;
            }
            if (json.has("Original filename")) {
                this.filename = json.getString("Original filename");
            }else{
                this.filename = null;
            }
            if (json.has("Sound Type")) {
                this.soundType = json.getString("Sound Type");
            }else{
                this.soundType = null;
            }
            if (json.has("Category")) {
                this.category = json.getString("Category");
            }else{
                this.category = null;
            }
            if (json.has("File extension")) {
                this.fileExtension = json.getString("File extension");
            }else{
                this.fileExtension = null;
            }
            if (json.has("File size(KB))")) {
                this.fileSize = json.getDouble("File size(KB)");
            }else{
                this.fileSize = 0;
            }
            if (json.has("Collection name")) {
                this.collectionName = json.getString("Collection name");
            }else{
                this.collectionName = null;
            }
            if (json.has("Length (sec)")) {
                String lenght = json.getString("Length (sec)");
                if(!lenght.isEmpty())
                    this.lenght = Integer.parseInt(lenght);
                else
                    this.lenght = 0;
            }else{
                this.lenght = 0;
            }
            if (json.has("Collection ID")) {
                this.collectionID = json.getInt("Collection ID");
            }else{
                this.collectionID = 0;
            }
            if (json.has("Download link")) {
                this.link = new URL(json.getString("Download link"));
            }else{
                this.link = null;
            }

        }catch (Exception e){
            Log.e("ServerFile", "error in constructor: "+e.getMessage());
        }
    }


    public URL getLink() {
        return link;
    }

    public String getTitle() {
        return title;
    }

    public String getFilename() {
        return filename;
    }

    public String getCategory() {
        return category;
    }

    public String getSoundType() {
        return soundType;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public int getLenght() {
        return lenght;
    }

    public int getCollectionID() {
        return collectionID;
    }

    public double getFileSize() {
        return fileSize;
    }
}
