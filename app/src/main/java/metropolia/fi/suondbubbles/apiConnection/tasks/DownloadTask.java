package metropolia.fi.suondbubbles.apiConnection.tasks;

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
        File file = null;
        try{
            URL url = new URL(params[0]);
            // create folder
            String FOLDER_NAME = "bubblesSounds";
            File folder = new File(Environment.getExternalStorageDirectory()+File.separator+FOLDER_NAME);
            if(!folder.exists()){
                folder.mkdir();
            }
            // create file (if file is already in memory, it won't be downloaded
            // unless the file is older than 24h
            file = new File(Environment.getExternalStorageDirectory()+File.separator+FOLDER_NAME, params[1]);
            long now = Calendar.getInstance().getTimeInMillis();
            if(file.exists() && now - file.lastModified() > 86400000){
                boolean isDeleted = file.delete();
                Log.d(DEBUG_TAG, "isDeleted:"+isDeleted);
                return file.getPath();
            }
            if(file.exists()){
                return file.getPath();
            }
                file.createNewFile();
            // stream to the new file
            OutputStream outputStream = new FileOutputStream(file);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);

            // stream from url
            BufferedInputStream bufferedInputStream = new BufferedInputStream(url.openStream());
            // size of the buffer
            int minBufferSize = AudioRecord.getMinBufferSize(44100,
                    AudioFormat.CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT);
            Log.d(DEBUG_TAG, "minbuff:"+minBufferSize);

            byte[] buffer = new byte[minBufferSize];

            // copy data to the file
            int bytesread;
            while((bytesread = bufferedInputStream.read(buffer)) != -1){
                bufferedOutputStream.write(buffer, 0, bytesread);
                //Log.d(DEBUG_TAG, "bytesread:"+bytesread);
            }
            // close streams
            bufferedOutputStream.close();
            bufferedInputStream.close();

        }catch (Exception e){
            Log.d(DEBUG_TAG, e.getClass().toString() + ":" +e.getMessage());
            e.printStackTrace();
        }
        return file.getPath();
    }

}