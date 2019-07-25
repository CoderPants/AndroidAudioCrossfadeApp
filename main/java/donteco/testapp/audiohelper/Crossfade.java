package donteco.testapp.audiohelper;

import android.media.MediaPlayer;

import java.util.ArrayList;

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

    @Override
    public void run()
    {
        try
        {
            boolean startingFadeOut = true;
            while (curMediaPlayer.isPlaying())
            {
                try
                {
                    if(curAudioTimeMs <= crossFadeDurationMS)
                        fadeIn();

                    if(curAudioTimeMs > crossFadeDurationMS
                            && (curAudioDurationMS - curAudioTimeMs) > crossFadeDurationMS)
                        timeCounting();

                    if((curAudioDurationMS - curAudioTimeMs) <= crossFadeDurationMS)
                    {
                        if (startingFadeOut)
                        {
                            startingFadeOut = false;
                            playNextSong();
                        }
                        fadeOut();
                    }
                }
                catch (InterruptedException e)
                {
                    System.out.println("Some thread interrupt \"crossFade\" thread!");
                    stopPlayer();
                    return;
                }
            }
            System.out.println("Size in stop: " + audioPlayer.getMediaPlayers().size() + " curSongPlaingIndex " + audioPlayer.getCurSongPlayingIndex());
            stopPlayer();
            System.out.println("Size in stop: " + audioPlayer.getMediaPlayers().size() + " curSongPlaingIndex " + audioPlayer.getCurSongPlayingIndex());
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
        //System.out.println( "Fade in" + "Cur duration " + curAudioDurationMS + " Cur Audio time ms " + curAudioTimeMs + " crossfade " + crossFadeDurationMS);
        Thread.sleep(crossFadeTimeIntervalMS);
    }

    private void timeCounting() throws InterruptedException
    {
        curAudioTimeMs += ConstantsForApp.STANDART_AUDIO_INTERVAL_MS;
        //System.out.println("Counting" + "Cur duration " + curAudioDurationMS + " Cur Audio time ms " + curAudioTimeMs + " crossfade " + crossFadeDurationMS);
        Thread.sleep(ConstantsForApp.STANDART_AUDIO_INTERVAL_MS);
    }

    private void fadeOut() throws InterruptedException
    {
        curVolume -= crossFadeVolumeInterval;
        curAudioTimeMs += crossFadeTimeIntervalMS;
        curMediaPlayer.setVolume(curVolume, curVolume);
        //System.out.println("Fade out" + "Cur duration " + curAudioDurationMS + " Cur Audio time ms " + curAudioTimeMs + " crossfade " + crossFadeDurationMS + " curVolume " + curVolume);
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
        audioPlayer.setMediaPlayers(mediaPlayers);
    }
}
