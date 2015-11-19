package metropolia.fi.suondbubbles.apiConnection;


public abstract  class CollectionID {
    private static String collectionID;

    public static String getCollectionID() {
        return collectionID;
    }

    public static void setCollectionID(String collection) {
       collectionID = collection;
    }
}
