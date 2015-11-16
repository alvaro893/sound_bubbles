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
public class DownloadTask extends AsyncTask<String, Void, Void> {
    final String DEBUG_TAG = "DownloadTask";
    @Override
    protected Void doInBackground(String... params) {
        // create file
        try{
            URL url = new URL(params[0]);
            // create folder
            File folder = new File(Environment.getExternalStorageDirectory()+File.separator+"sounds");
            if(!folder.exists()){
                folder.mkdir();
            }
            // create file
            File file = new File(Environment.getExternalStorageDirectory()+File.separator+"sounds", params[1]);

            if(file.exists()){
                boolean isDeleted = file.delete();
                Log.d(DEBUG_TAG, "isDeleted:"+isDeleted);
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
        return null;
    }

    protected void onPostExecute(){

    }
}
