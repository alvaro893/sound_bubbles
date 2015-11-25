package metropolia.fi.suondbubbles.helper;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Alvaro on 22/11/2015.
 */
public class WavConverter {

    private final String DEBUG_TAG = this.getClass().getSimpleName();
    private Context ctx;
    private int timeInSecs;

    public WavConverter(Context ctx) {
        this.ctx = ctx;
    }

    /** Convert the give file into wav format
     * @param  inFile path of the file to convert. Example: /sdcard/testrec.raw
     * @param outFile path of the wav file to be created. Example: /sdcard/testrec.wav
     * **/
    public void convWav(String inFile, String outFile){
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
                    Log.d(DEBUG_TAG, "progress:"+message);
                }

                public void onSuccess(String message) {
                        String rgx = "time=......([0-9][0-9])";
                        Pattern pattern = Pattern.compile(rgx);
                        Matcher matcher = pattern.matcher(message);
                        String found = "not found";
                        if (matcher.find()) {
                            found = matcher.group(0); // should be something like "time=30:10:01"
                            timeInSecs = Integer.parseInt(found.substring(found.length() - 2));
                        }
                    Log.d(DEBUG_TAG, "sucess:" + message);
                    if(found != null)
                        Log.d(DEBUG_TAG, "found:"+found);
                }

                public void onFinish() {
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // Handle if FFmpeg is already running
        }
    }

    public int getTimeInSecs() {
        return timeInSecs;
    }
}
