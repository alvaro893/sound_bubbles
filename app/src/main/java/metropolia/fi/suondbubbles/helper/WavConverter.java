package metropolia.fi.suondbubbles.helper;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import metropolia.fi.suondbubbles.apiConnection.CollectionID;
import metropolia.fi.suondbubbles.apiConnection.ServerFile;
import metropolia.fi.suondbubbles.apiConnection.tasks.UploadTask;

/**
 * Created by Alvaro on 22/11/2015.
 */
public class WavConverter {

    private final String DEBUG_TAG = this.getClass().getSimpleName();
    private Context ctx;
    private int timeInSecs;
    private ServerFile serverFile;
    private String category;
    private String title;

    public boolean isDone = false;
    private String response;

    public WavConverter(Context ctx, String title, String category) {
        this.ctx = ctx;
        this.title = title;
        this.category = category;
        serverFile = new ServerFile();

    }

    /**
     * Convert the give file into wav format
     * var  inFile path of the file to convert. Example: /sdcard/testrec.raw
     * var outFile path of the wav file to be created. Example: /sdcard/testrec.wav
     **/
    public String convertToWavAndUpload(String inFile, final String outFile) {

        File file = new File(outFile);
        if (file.exists()) {
            file.delete();
        }
        FFmpeg ffmpeg = FFmpeg.getInstance(ctx);
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                public void onStart() {
                }

                public void onFailure() {
                }

                public void onSuccess() {
                }

                public void onFinish() {
                }
            });
        } catch (FFmpegNotSupportedException e) {
            // Handle if FFmpeg is not supported by device
        }

        try {
            String command = String.format("-f s16le -ar 44.1k -ac 2 -i %s %s", inFile, outFile);
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {

                public void onStart() {
                }

                public void onProgress(String message) {
                    Log.d(DEBUG_TAG, message);
                }

                public void onFailure(String message) {
                    Log.d(DEBUG_TAG, "progress:" + message);
                }

                public void onSuccess(String message) {
                    // get the leng of the music file
                    String rgx = "time=......([0-9][0-9])";
                    Pattern pattern = Pattern.compile(rgx);
                    Matcher matcher = pattern.matcher(message);
                    String found = "not found";
                    if (matcher.find()) {
                        found = matcher.group(0); // should be something like "time=30:10:01"
                        timeInSecs = Integer.parseInt(found.substring(found.length() - 2));
                    }
                    Log.d(DEBUG_TAG, "sucess:" + message);


                    // make the file and call upload task
                    serverFile.setLength(timeInSecs);
                    serverFile.setCategory(category);
                    serverFile.setTitle(title);
                    serverFile.setPathLocalFile(outFile);
                    serverFile.setCollectionID(Integer.parseInt(CollectionID.getCollectionID()));
                    serverFile.setCreator("SoundBubbles");
                    serverFile.setDescription("Not supported yet");

                    UploadTask uploadTask = new UploadTask();
                    uploadTask.execute(serverFile);
                    try {
                        response = uploadTask.get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }

                public void onFinish() {
                    isDone = true;
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // Handle if FFmpeg is already running
        }
        if(isDone){
            return response;
        }else{
            return null;
        }
    }


}
