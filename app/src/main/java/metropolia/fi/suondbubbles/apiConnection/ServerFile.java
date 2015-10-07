package metropolia.fi.suondbubbles.apiConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

/**
 * This class should recive the JSON
 */
// TODO implent this class
public class ServerFile {
    private String Title, filename, category, soundType, FileExtension, CollectionName;
    private int lenght, collectionID;
    private float FileSize;
    private URL link;
    
    ServerFile(JSONObject json){
        readAttributes(json);
    }

    private void readAttributes(JSONObject json) {
        // TODO: ServerFile class
    }

}
