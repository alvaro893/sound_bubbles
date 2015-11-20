package metropolia.fi.suondbubbles.apiConnection.tasks;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Calendar;

import metropolia.fi.suondbubbles.activities.SoundBubbles;
import metropolia.fi.suondbubbles.helper.SoundFile;

/**
 * Created by alvarob on 14.11.2015
 *
 * Description
 * Download file from the given url and writes it into
 * 'sounds' folder with the given name.

 * Parameters
 * params[0]: url String
 * params[1]: name of the file to be created
 *
 */
public class DownloadTask extends AsyncTask<String, Void, String> {
    final String DEBUG_TAG = "DownloadTask";
    @Override
    protected String doInBackground(String... params) {
        String urlString = params[0];
        String filename = params[1];
        File file = null;
        try{
            URL url = new URL(urlString);
            SoundFile soundFile = new SoundFile(url.openStream());
            file = soundFile.createFileInCache(SoundBubbles.getMainContext(), filename);
        }catch (Exception e){
            Log.d(DEBUG_TAG, e.getClass().toString() + ":" +e.getMessage());
            e.printStackTrace();
        }
        return file.getPath();
    }



}
