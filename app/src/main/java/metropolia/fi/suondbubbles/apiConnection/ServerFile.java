package metropolia.fi.suondbubbles.apiConnection;

import android.util.Log;

import org.json.JSONObject;

import java.io.Serializable;
import java.net.URL;

/**
 * - This is a JavaBean that contains all information related to every file from the server
 * - It contains all possible fields from server which are the following:
 *
 0:  {
 Metadata name: "Title"
 Metadata DB field: "field8"
 options: null
 }-
 1:  {
 Metadata name: "Creator"
 Metadata DB field: "field81"
 options: null
 }-
 2:  {
 Metadata name: "Language"
 Metadata DB field: "field82"
 options: null
 }-
 3:  {
 Metadata name: "Publisher"
 Metadata DB field: "field83"
 options: null
 }-
 4:  {
 Metadata name: "Rights"
 Metadata DB field: "field84"
 options: null
 }-
 5:  {
 Metadata name: "Location - longitude"
 Metadata DB field: "field78"
 options: null
 }-
 6:  {
 Metadata name: "Length (sec)"
 Metadata DB field: "field79"
 options: null
 }-
 7:  {
 Metadata name: "Source"
 Metadata DB field: "field80"
 options: null
 }-
 8:  {
 Metadata name: "Location - latitude"
 Metadata DB field: "field77"
 options: null
 }-
 9:  {
 Metadata name: "Original filename"
 Metadata DB field: "originalfilename"
 options: null
 }-
 10:  {
 Metadata name: "Description"
 Metadata DB field: "field73"
 options: null
 }-
 11:  {
 Metadata name: "Tags"
 Metadata DB field: "field74"
 options: null
 }-
 12:  {
 Metadata name: "Category"
 Metadata DB field: "field75"
 options: "nature,human,machine,story"
 }-
 13:  {
 Metadata name: "Sound Type"
 Metadata DB field: "field76"
 options: "soundscapes,ambience,effects"

 */
public class ServerFile implements Serializable {
    private String title, filename, category, soundType, fileExtension, collectionName, description,
            tags, creator, language, publisher, rights, location, source, link, pathLocalFile;
    private int length, collectionID;
    private double fileSize;

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
                    this.length = Integer.parseInt(lenght);
                else
                    this.length = 0;
            }else{
                this.length = 0;
            }
            if (json.has("Collection ID")) {
                this.collectionID = json.getInt("Collection ID");
            }else{
                this.collectionID = 0;
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

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPathLocalFile() {
        return pathLocalFile;
    }

    public void setPathLocalFile(String pathLocalFile) {
        this.pathLocalFile = pathLocalFile;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
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
}
