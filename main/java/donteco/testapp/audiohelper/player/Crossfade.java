package donteco.testapp.audiohelper.player;

import android.media.MediaPlayer;

import java.util.ArrayList;

import donteco.testapp.audiohelper.ConstantsForApp;

public class Crossfade implements Runnable
{
    private AudioPlayer audioPlayer;

    private MediaPlayer curMediaPlayer;

    private long curAudioDurationMS;
    private long curAudioTimeMs;
    private float curVolume;

    private long crossFadeDurationMS;
    private long crossFadeTimeIntervalMS;
    private float crossFadeVolumeInterval;

    //private boolean isPaused;

    public Crossfade(AudioPlayer audioPlayer, MediaPlayer curMediaPlayer, long crossFadeDurationMS, long crossFadeTimeIntervalMS, float crossFadeVolumeInterval)
    {
        this.audioPlayer = audioPlayer;
        this.curMediaPlayer = curMediaPlayer;
        this.crossFadeDurationMS = crossFadeDurationMS;
        this.crossFadeTimeIntervalMS = crossFadeTimeIntervalMS;
        this.crossFadeVolumeInterval = crossFadeVolumeInterval;

        curAudioDurationMS = curMediaPlayer.getDuration();
        curAudioTimeMs = 0;
        curVolume = 0;
    }

    /*public void setPaused(boolean paused) {
        isPaused = paused;
    }*/

    //Main function
    //if mediaPlayer is trying to set smth on the released player, we catch IllegalStateException
    //than, main loop
    //first if - for fist crossFadeDurationMS
    //second if - for last crossFadeDurationMS
    //else - for interval after first crossFadeDurationMS and before last crossFadeDurationMS
    @Override
    public void run()
    {
        try
        {
            boolean needFadeOut = true;
            while (curMediaPlayer.isPlaying())
            {
                try
                {
                    /*if(isPaused)
                        this.wait();*/

                    if(curAudioTimeMs <= crossFadeDurationMS)
                        fadeIn();
                    else
                    {
                        if((curAudioDurationMS - curAudioTimeMs) <= crossFadeDurationMS)
                        {
                            if (needFadeOut)
                            {
                                needFadeOut = false;
                                playNextSong();
                            }
                            fadeOut();
                        }
                        else
                            timeCounting();
                    }
                }
                catch (InterruptedException e)
                {
                    System.out.println("Some thread interrupt \"crossFade\" thread!");
                    stopPlayer();
                    return;
                }
            }
            stopPlayer();
        }
        catch (IllegalStateException e)
        {
            System.out.println("Player has been released");
        }
    }

    private void fadeIn() throws InterruptedException
    {
        curVolume += crossFadeVolumeInterval;
        curAudioTimeMs += crossFadeTimeIntervalMS;
        curMediaPlayer.setVolume(curVolume, curVolume);
        Thread.sleep(crossFadeTimeIntervalMS);
    }

    private void timeCounting() throws InterruptedException
    {
        curAudioTimeMs += ConstantsForApp.STANDART_AUDIO_INTERVAL_MS;
        Thread.sleep(ConstantsForApp.STANDART_AUDIO_INTERVAL_MS);
    }

    private void fadeOut() throws InterruptedException
    {
        curVolume -= crossFadeVolumeInterval;
        curAudioTimeMs += crossFadeTimeIntervalMS;
        curMediaPlayer.setVolume(curVolume, curVolume);
        Thread.sleep(crossFadeTimeIntervalMS);
    }

    private void playNextSong()
    {
        int songIndex = audioPlayer.getCurSongPlayingIndex();
        songIndex = ++songIndex == ConstantsForApp.AUDIO_COUNT? 0 : songIndex;
        audioPlayer.setCurSongPlayingIndex(songIndex);
        audioPlayer.play(audioPlayer.getUriOfNextSong());
    }

    private void stopPlayer()
    {
        ArrayList<MediaPlayer> mediaPlayers = audioPlayer.getMediaPlayers();
        mediaPlayers.remove(curMediaPlayer);
        curMediaPlayer.release();
    }
}
