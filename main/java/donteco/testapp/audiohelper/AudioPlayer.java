package donteco.testapp.audiohelper;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;

import java.util.ArrayList;

public class AudioPlayer {

   private ArrayList<MediaPlayer> mediaPlayers;

   private Activity mediaActivity;
   private ArrayList<Audio> songList;

   private int curSongPlayingIndex;
   private long crossFadeDurationMS;
   private long crossFadeTimeIntervalMS;
   private float crossFadeVolumeInterval;

   public AudioPlayer(Activity mediaActivity)
   {
      this.mediaActivity = mediaActivity;
      mediaPlayers = new ArrayList<>(ConstantsForApp.AUDIO_COUNT);
      crossFadeVolumeInterval = (float)ConstantsForApp.CROSSFADE_MAX_VOLUME / (float)ConstantsForApp.CROSSFADE_STEP_AMOUNT;
   }

   public void setSongList(ArrayList<Audio> songList) {
      this.songList = songList;
   }

   public ArrayList<MediaPlayer> getMediaPlayers() {
      return mediaPlayers;
   }

   public void setMediaPlayers(ArrayList<MediaPlayer> mediaPlayers) {
      this.mediaPlayers = mediaPlayers;
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
      if (mediaPlayers.size() <= ConstantsForApp.AUDIO_COUNT)
      {
         final MediaPlayer newMediaPlayer = MediaPlayer.create(mediaActivity, songUri);
         /*newMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
         {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
               stopLastPlayer(newMediaPlayer);
            }
         });*/

         mediaPlayers.add(newMediaPlayer);
         newMediaPlayer.start();

         crossFadeTimeIntervalMS =  crossFadeDurationMS / ConstantsForApp.CROSSFADE_STEP_AMOUNT;

         Crossfade crossfade = new Crossfade(this, newMediaPlayer,
                 crossFadeDurationMS, crossFadeTimeIntervalMS, crossFadeVolumeInterval);

         Thread startCrossFade = new Thread(crossfade);
         startCrossFade.start();

         //crossFade(newMediaPlayer);
      }
   }

   private void crossFade(final MediaPlayer curMediaPlayer)
   {
      Thread crossFadeThread = new Thread(new Runnable()
      {
         private long curAudioDurationMS = curMediaPlayer.getDuration();
         private long curAudioTimeMs = 0;
         private float curVolume = 0;

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
                           curSongPlayingIndex = ++curSongPlayingIndex == ConstantsForApp.AUDIO_COUNT? 0: curSongPlayingIndex;
                           play(getUriOfNextSong());
                        }
                        fadeOut();
                     }
                  }
                  catch (InterruptedException e)
                  {
                     System.out.println("Some thread interrupt \"crossFade\" thread!");
                     mediaPlayers.remove(curMediaPlayer);
                     curMediaPlayer.release();
                     return;
                  }
               }
               System.out.println("Size in stop: " + mediaPlayers.size() + " curSongPlaingIndex " + curSongPlayingIndex);
               curMediaPlayer.release();
               mediaPlayers.remove(curMediaPlayer);
               System.out.println("Size in stop: " + mediaPlayers.size() + " curSongPlaingIndex " + curSongPlayingIndex);
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
      });

      crossFadeThread.start();
   }

   public void pause() {

      for (MediaPlayer mediaPlayer : mediaPlayers) {
         if(mediaPlayer != null)
            mediaPlayer.pause();
      }
   }

   /*public void stop() {
      stopAllPlayers();
   }*/

   public void stopLastPlayer(MediaPlayer lastPlayer)
   {
      synchronized (mediaActivity)
      {
         System.out.println("Size in stop: " + mediaPlayers.size() + " curSongPlaingIndex " + curSongPlayingIndex);
         lastPlayer.release();
         mediaPlayers.remove(lastPlayer);
         System.out.println("Size in stop: " + mediaPlayers.size() + " curSongPlaingIndex " + curSongPlayingIndex);
      }
   }

   public void stopAllPlayers()
   {
      MediaPlayer mediaPlayer;

      for (int i = 0; i < mediaPlayers.size(); i++)
      {
         mediaPlayer = mediaPlayers.get(i);
         mediaPlayer.release();
         mediaPlayers.remove(mediaPlayer);
      }

      curSongPlayingIndex = 0;
   }

   public Uri getUriOfNextSong() {
      System.out.println("In uri cur song playing index " + curSongPlayingIndex + " cur mediaPlayers size " + mediaPlayers.size());
      return songList.get(curSongPlayingIndex).getUri();
   }

}
