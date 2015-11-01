package metropolia.fi.suondbubbles.apiConnection;

import android.util.Log;

import org.json.JSONObject;

import java.net.URL;

/**
 * This class should recive the JSON
 */
public class ServerFile {
    private String title, filename, category, soundType, fileExtension, collectionName, description, tags;
    private int lenght, collectionID;
    private double fileSize, lat, lon;
    private URL link;

    public ServerFile(){

    }

    public ServerFile(JSONObject json){
        // TODO: add missing fields to constructor ?
        Log.d("checkServerFile", json.toString());
        try {
            if (json.has("Title")) {
                this.title = json.getString("Title");
                if(this.title.isEmpty())
                    this.title = "((No title))";
            } else {
                this.title = "((No title))";
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
                if(this.category.isEmpty())
                    this.category = "((missed category))";
            }else{
                this.category = "((missed category))";
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

    @Override
    public String toString() {
        return this.getTitle();
    }

    @Override
    public boolean equals(Object o) {
        if(this.filename.equals( ((ServerFile) o).getFilename()) ){
            return true;
        }else{
            return false;
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSoundType() {
        return soundType;
    }

    public void setSoundType(String soundType) {
        this.soundType = soundType;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public int getLenght() {
        return lenght;
    }

    public void setLenght(int lenght) {
        this.lenght = lenght;
    }

    public int getCollectionID() {
        return collectionID;
    }

    public void setCollectionID(int collectionID) {
        this.collectionID = collectionID;
    }

    public double getFileSize() {
        return fileSize;
    }

    public void setFileSize(double fileSize) {
        this.fileSize = fileSize;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public URL getLink() {
        return link;
    }

    public void setLink(URL link) {
        this.link = link;
    }
}
