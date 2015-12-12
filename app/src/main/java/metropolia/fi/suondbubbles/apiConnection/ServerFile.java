package metropolia.fi.suondbubbles.apiConnection;

import java.io.Serializable;
import java.util.HashMap;

/**
 * - This is a JavaBean that contains all information related to every file from the server
 * - It contains all possible fields from server which are the following (although, not all are used):
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
    /** this match the name of the field in this java bean with the name in the remote database **/
    public static final HashMap<String,String> fieldMap = new HashMap<String,String>()
    {{      put("title",   "field8");
            put("category",   "field75");
            put("collectionID",   "collection");
            put("soundType",   "field76");
            put("filename",   "originalfilename");
            put("description",   "field73");
            put("length",   "field79");
            put("creator",   "field81");
            put("language",   "field82");
            put("publisher",   "field83");
            put("rights",   "field84");
            put("location",   "field78");
            put("source",   "field80");
            put("tags",   "field74");
        }};

    public ServerFile(){

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
