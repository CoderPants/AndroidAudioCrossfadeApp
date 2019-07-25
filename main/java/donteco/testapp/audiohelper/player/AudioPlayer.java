package donteco.testapp.audiohelper.player;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;

import java.util.ArrayList;
import java.util.Iterator;

import donteco.testapp.audiohelper.ConstantsForApp;

public class AudioPlayer {

   private ArrayList<MediaPlayer> mediaPlayers;

   private Activity mediaActivity;
   private ArrayList<Audio> songList;

   private int curSongPlayingIndex;
   private long crossFadeDurationMS;
   private float crossFadeVolumeInterval;

   public AudioPlayer(Activity mediaActivity)
   {
      this.mediaActivity = mediaActivity;
      mediaPlayers = new ArrayList<>(ConstantsForApp.AUDIO_COUNT);
      crossFadeVolumeInterval = (float)ConstantsForApp.CROSSFADE_MAX_VOLUME / (float)ConstantsForApp.CROSSFADE_STEP_AMOUNT;
      curSongPlayingIndex = 0;
   }

   public void setSongList(ArrayList<Audio> songList) {
      this.songList = songList;
   }

   public ArrayList<MediaPlayer> getMediaPlayers() {
      return mediaPlayers;
   }

   public void setCurSongPlayingIndex(int curSongPlayingIndex) {
      this.curSongPlayingIndex = curSongPlayingIndex;
   }

   public int getCurSongPlayingIndex() {
      return curSongPlayingIndex;
   }

   public void setCrossFadeDurationMS(long crossFadeDuration) {
      this.crossFadeDurationMS = crossFadeDuration * 1000;
   }

   //Try to find equal
   public void play(Uri songUri)
   {
      if (mediaPlayers.size() < ConstantsForApp.AUDIO_COUNT)
      {
         MediaPlayer newMediaPlayer = MediaPlayer.create(mediaActivity, songUri);
         mediaPlayers.add(newMediaPlayer);
         newMediaPlayer.start();

         long crossFadeTimeIntervalMS =  crossFadeDurationMS / ConstantsForApp.CROSSFADE_STEP_AMOUNT;

         Crossfade crossfade = new Crossfade(this, newMediaPlayer,
                 crossFadeDurationMS, crossFadeTimeIntervalMS, crossFadeVolumeInterval);

         Thread startCrossFade = new Thread(crossfade);
         startCrossFade.start();

      }
   }

   public void pause()
   {
      for (MediaPlayer mediaPlayer : mediaPlayers) {
         if(mediaPlayer != null)
            mediaPlayer.pause();
      }
   }

   public void stopAllPlayers()
   {
      Iterator <MediaPlayer> mediaPlayerIterator = mediaPlayers.iterator();

      while (mediaPlayerIterator.hasNext())
      {
         mediaPlayerIterator.next().release();
         mediaPlayerIterator.remove();

      }
      curSongPlayingIndex = 0;
   }

   public Uri getUriOfNextSong() {
      return songList.get(curSongPlayingIndex).getUri();
   }

}
