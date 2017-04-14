package org.sil.storyproducer.tools.media;

import android.media.MediaPlayer;

import java.io.IOException;

public class AudioPlayer {

    private MediaPlayer mPlayer;
    private boolean isPathSet, isPrepared;

    /**
     * Constructor for Audio Player, no params
     */
    public AudioPlayer() {
        mPlayer = new MediaPlayer();
        isPathSet = false;
        isPrepared = false;
    }

    /**
     * Only sets the path for the audio to
     * @param path String path for the audio
     */
    public void setPath(String path) {
        try {
            if (isPathSet) {
                mPlayer.reset();
                isPrepared = false;
            }
            mPlayer.setDataSource(path);
            isPathSet = true;
        } catch (IOException e) {
            //TODO maybe initializeToolbar with this exception
            e.printStackTrace();
        }
    }

    /**
     * Plays the audio with the given path
     */

    public void playAudio() {
        try {
            if (!isPrepared) {
                mPlayer.prepare();
                isPrepared = true;
            }
        } catch (IOException e) {
            //TODO maybe initializeToolbar with this exception
            e.printStackTrace();
        }
        mPlayer.start();
    }

    /**
     * Pauses the audio if it is currenlty being played
     */
    public void pauseAudio() {
        if(mPlayer.isPlaying()) {
            try {
                mPlayer.pause();
            } catch (IllegalStateException e) {
                //TODO maybe initializeToolbar with this exception
                e.printStackTrace();
            }
        }
    }

    /**
     * Resumes the audio from where it was last paused
     */
    public void resumeAudio() {
        int pauseSpot = mPlayer.getCurrentPosition();
        mPlayer.seekTo(pauseSpot);
        mPlayer.start();
    }

    /**
     * Stops the audio if it is currently being played
     */
    public void stopAudio() {
        if(isPrepared) {
            try {
                mPlayer.stop();
                isPrepared = false;
            } catch (IllegalStateException e) {
                //TODO maybe initializeToolbar with this exception
                e.printStackTrace();
            }
        }
    }

    /**
     * Releases the MediaPlayer object after completion
     */
    public void release() {
        mPlayer.release();
    }

    /**
     * This allows the user to do initializeToolbar once the audio has completed
     * via implementing MediaPlayer.OnCompleteListener.
     * @param OcL handler for OnCompletionListener
     */
    public void onPlayBackStop(MediaPlayer.OnCompletionListener OcL){
        mPlayer.setOnCompletionListener(OcL);
    }

    /**
     * returns the duration of the audio as an int
     * @return the duration of the audio as an int
     */
    public int getAudioDurationInSeconds() {
        return (int)(mPlayer.getDuration() * 0.001);
    }

    /**
     * returns the duration of the audio as an int in miliseconds
     * @return the duration of the audio as an int
     */
    public int getAudioDurationInMilliseconds() {
        return mPlayer.getDuration();
    }

    /**
     * Seeks to the parameter in milliseconds
     * @param msec milliseconds for where to seek to in the audio
     */
    public void seekTo(int msec) {
        mPlayer.seekTo(msec);
    }

    /**
     * sets the completion listener
     * @param listener handler for OnCompletionListener
     */
    public void audioCompletionListener(MediaPlayer.OnCompletionListener listener) {
        mPlayer.setOnCompletionListener(listener);
    }

    /**
     * sets the volume of the audio
     * @param volume the float for the volume from 0.0 to 1.0
     */
    public void setVolume(float volume) {
        mPlayer.setVolume(volume, volume);
    }

    /**
     * returns if the audio is being played or not
     * @return true or false based on if the audio is being played
     */
    public boolean isAudioPlaying() {
        return mPlayer.isPlaying();
    }
}
