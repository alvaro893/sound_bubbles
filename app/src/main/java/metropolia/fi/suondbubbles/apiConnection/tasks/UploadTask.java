package metropolia.fi.suondbubbles.apiConnection.tasks;

import android.os.AsyncTask;

import java.io.InputStream;

import metropolia.fi.suondbubbles.activities.SoundBubbles;
import metropolia.fi.suondbubbles.apiConnection.AsyncResponse;
import metropolia.fi.suondbubbles.apiConnection.ServerConnection;
import metropolia.fi.suondbubbles.apiConnection.ServerFile;

/**
 * Created by alvarob on 11.10.2015.
 */
public class UploadTask extends AsyncTask<Object, Void, String> {


    protected String doInBackground(Object ...params){
        ServerFile file = (ServerFile) params[0];

        ServerConnection serverConnection = SoundBubbles.serverConnection;

        return serverConnection.upload(file);
    }

}
