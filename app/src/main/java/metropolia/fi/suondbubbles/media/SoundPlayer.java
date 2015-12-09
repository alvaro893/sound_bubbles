package metropolia.fi.suondbubbles.media;


import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

public class SoundPlayer {

    private MediaPlayer mediaPlayer;
    private final String DEBUG_TAG = "SoundPlayer";
    private boolean soundSourceSet, paused;
    private String soundPath;
    private SoundPlayerListener soundPlayerListener;
    private float volume;
    private int length;

    public interface SoundPlayerListener{
        void onStarting();
        void onPausing();
        void onStopping();
    }

    public void setSoundPlayerListener(SoundPlayerListener soundPlayerListener) {
        this.soundPlayerListener = soundPlayerListener;
    }

    public SoundPlayer(){
        init();
    }

    private void init() {
        soundSourceSet = false;
        paused = false;
        length = 0;
        volume = 0.5f;
        mediaPlayer = new MediaPlayer();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                soundPlayerListener.onStopping();
                mp.stop();
            }
        });
    }

    /** return true if soundPath exists, else returns false*/
    public boolean setSound(String soundPath){
        soundSourceSet = false;
        if(soundPath != null) {
            this.soundPath = soundPath;
            preparePlayer();
            return true;
        }
        return false;
    }

    private void preparePlayer(){
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(soundPath);
            soundSourceSet = true;
            setSoundVolume(volume);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playIfNotPlaying(){
        if(mediaPlayer != null && soundSourceSet){
            if(!mediaPlayer.isPlaying() && !paused){
                try {
                    mediaPlayer.prepare();
                    soundPlayerListener.onStarting();
                    mediaPlayer.start();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(paused){
                Log.d(DEBUG_TAG, "resuming playing");
                paused = false;
                mediaPlayer.start();
                soundPlayerListener.onStarting();
            }
            else {
                Log.d(DEBUG_TAG, "Already playing");
            }
        }
        else {
            Log.e(DEBUG_TAG, "No sound file assigned or mediaPlayer in not instanced");
        }
    }

    public void stopIfPlaying(){
        if(mediaPlayer != null){
            if(mediaPlayer.isPlaying()){
                soundPlayerListener.onStopping();
                mediaPlayer.stop();
            }
        }else {
            Log.e(DEBUG_TAG, "MediaPlayer is not initialized");
        }
    }


    public void pauseIfPlaying(){
        if(mediaPlayer != null){
            if(mediaPlayer.isPlaying()){
                soundPlayerListener.onPausing();
                mediaPlayer.pause();
                paused = true;
                length = mediaPlayer.getCurrentPosition();
                mediaPlayer.seekTo(length);
            }
        } else {
            Log.d(DEBUG_TAG, "No sound file assigned");
        }
    }

    public boolean isSoundPlayerPlaying(){
        return mediaPlayer.isPlaying();
    }

    public void releaseSoundPlayer(){
        if(mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }

    }
    public void setSoundVolume(float volume){
        this.volume = volume;
        mediaPlayer.setVolume(volume,volume);
    }

    public int getDuration(){
        return mediaPlayer.getDuration();
    }



}
