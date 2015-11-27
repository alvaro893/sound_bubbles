package metropolia.fi.suondbubbles.apiConnection;

import android.util.Log;

import com.googlecode.openbeans.BeanInfo;
import com.googlecode.openbeans.Introspector;
import com.googlecode.openbeans.PropertyDescriptor;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Alvaro on 24/11/2015.
 * Mandaroty parameters (copy from server documentation)
 key=[string]          			auth key
 userfile=[@file]        		set the file path
 filename=[string]				specify a new filename for the uploaded file
 resourcetype=4					default is 4
 collection=[integer]			collection to upload to (required - please see http://url/plugins/api_upload/help_collections.php
 'metadata DB field'=[string]	please see http://url/plugins/api_upload/help_metadata.php and http://url/plugins/api_upload/help_options.php
 */
public class ServerFiletoParameters {
    private final String DEBUG_TAG = getClass().getSimpleName();
    // parameters to send
    private HashMap<String,String> params = new HashMap<>();

    /** This get the properties of the ServerFile Bean dinamically **/
    public HashMap createJSON(ServerFile serverFile, String apiKey){
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(ServerFile.class);
            for (PropertyDescriptor propertyDesc : beanInfo.getPropertyDescriptors()) {
                // get atribute name from ServerFileBean and its value
                String propertyName = propertyDesc.getName();
                Object value = propertyDesc.getReadMethod().invoke(serverFile);
                // get database field if it exits
                String dbFiledName = ServerFile.fieldMap.get(propertyName);
                if(dbFiledName != null && value != null){
                    Log.d(DEBUG_TAG, "propertyName:" + propertyName + "," + "value:" + value.toString() + "," + "fieldDB:" + dbFiledName);
                    params.put(dbFiledName, value.toString());
                }
            }
            // other parameters
            params.put("key", apiKey);
            //params.put("filename", serverFile.getFilename());
            params.put("resourcetype", "4");
            //params.put("collection", CollectionID.getCollectionID());

            Log.d(DEBUG_TAG, "params length:" + String.valueOf(params.size()));
        }catch (Exception e){
            e.printStackTrace();
        }
            return params;
    }

}
