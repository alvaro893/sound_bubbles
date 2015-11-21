package metropolia.fi.suondbubbles.helper;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.os.Environment;
import android.util.Log;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

/**
 * Created by Alvaro on 20/11/2015.
 */
public class SoundFile {
    private final String DEBUG_TAG = getClass().getSimpleName();
    private final String FOLDER_NAME = "bubblesSounds";
    private File file;
    private InputStream urlSream;

    public SoundFile(InputStream urlSream) {
        this.urlSream = urlSream;
    }

    // It creates file in external memory (It does not work anymore)
    public File createFile(String filename) throws IOException {
        // create folder
            File folder = new File(Environment.getExternalStorageDirectory()+File.separator+FOLDER_NAME);
            if(!folder.exists()){
                folder.mkdir();
            }
        // create file (if file is already in memory, it won't be downloaded
        // unless the file is older than 24h
        file = new File(Environment.getExternalStorageDirectory()+File.separator+FOLDER_NAME, filename);
        long now = Calendar.getInstance().getTimeInMillis();
        if(file.exists() && now - file.lastModified() > 86400000){
            boolean isDeleted = file.delete();
            Log.d(DEBUG_TAG, "isDeleted:" + isDeleted);
            performDownload();
            return file;
        }
        if(file.exists()){
            return file;
        }
        file.createNewFile();
        return file;
    }


    // It creates file in internal memory and uses md5 hash to check if the file is different
    public File createFileInCache(Context ctx, String filename) throws IOException {
        File folder = ctx.getDir(FOLDER_NAME, Context.MODE_PRIVATE);
        file = new File(folder,filename);
        if(file.exists()){
            String md5file = calculateHashFile(file);
            String md5url = calculateHashFile(urlSream);
            if(!md5file.equals(md5url)){
                file.delete();
                Log.d(DEBUG_TAG, "file was deleted");
                file.createNewFile();
                performDownload();
            }
        }else{
            file.createNewFile();
            performDownload();
        }
        return file;
    }

    private void performDownload() throws IOException {
        // stream to the new file
        OutputStream outputStream = new FileOutputStream(file);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);

        // stream from url
        BufferedInputStream bufferedInputStream = new BufferedInputStream(urlSream);
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
            Log.d(DEBUG_TAG, "bytesread:" + bytesread);
        }
        // close streams
        bufferedOutputStream.close();
        bufferedInputStream.close();
    }

    public String calculateHashFile(File file) {
        String md5 = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            md5 = new String(Hex.encodeHex(DigestUtils.md5(fis)));
            fis.close();
        }catch (Exception e){
            Log.d(DEBUG_TAG, e.getMessage());
        }
        Log.d(DEBUG_TAG, md5);

        return md5;
    }

    public String calculateHashFile(InputStream is){
        String md5 = null;
        try{
            md5 = new String(Hex.encodeHex(DigestUtils.md5(is)));
            is.close();
        }catch (Exception e){
            Log.d(DEBUG_TAG, e.getMessage());
        }
        Log.d(DEBUG_TAG, md5);

        return md5;
    }


    /*
    public void calculateHashFileAlternate(File file){
        MessageDigest md = MessageDigest.getInstance("MD5");
        try (InputStream is = Files.newInputStream(Paths.get(file));
             DigestInputStream dis = new DigestInputStream(is, md))
        {
            // Read decorated stream (dis) to EOF as normal...
        }
        byte[] digest = md.digest();
    }
    */

}