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
   private int crossFadeDurationMS;
   private float crossFadeTimeIntervalMS;
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


   public void setCurSongPlayingIndex(int curSongPlayingIndex) {
      this.curSongPlayingIndex = curSongPlayingIndex;
   }

   public void setCrossFadeDurationMS(int crossFadeDuration) {
      this.crossFadeDurationMS = crossFadeDuration * 1000;
   }

   public void play(Uri songUri)
   {
      if (mediaPlayers.size() < ConstantsForApp.AUDIO_COUNT)
      {
         MediaPlayer newMediaPlayer = MediaPlayer.create(mediaActivity, songUri);
         newMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
         {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
               stopLastPlayer();
            }
         });

         mediaPlayers.add(newMediaPlayer);
         newMediaPlayer.start();
         System.out.println("Size in start:" + mediaPlayers.size());
         crossFade();
      }
   }

   private void crossFade()
   {
      final MediaPlayer curMediaPlayer = mediaPlayers.get(curSongPlayingIndex);
      System.out.println("Current index in crossfade " + curSongPlayingIndex);
      curSongPlayingIndex = ++curSongPlayingIndex == ConstantsForApp.AUDIO_COUNT? 0: curSongPlayingIndex;
      crossFadeTimeIntervalMS = (float) crossFadeDurationMS / (float)ConstantsForApp.CROSSFADE_STEP_AMOUNT;
      final long standartAudioIntervalMS = 1000;

      new Thread(new Runnable()
      {
         private float curAudioTimeMS = 0;
         private float curVolume = 0;
         private boolean startFadeOut = true;
         private int songDurationMS = curMediaPlayer.getDuration();

         @Override
         public void run()
         {
            float difference;

            while (curAudioTimeMS <= songDurationMS && curMediaPlayer.isPlaying())
            {
               //System.out.println("Cur volume: " + curVolume + " CurTime: " + curAudioTimeMS + " crossfade interval " + crossFadeTimeIntervalMS + " crossfadevolume interval" + crossFadeVolumeInterval);
               //Fade in
               if(curAudioTimeMS < crossFadeDurationMS)
               {
                  curVolume += crossFadeVolumeInterval;
                  curAudioTimeMS += crossFadeTimeIntervalMS;
                  curMediaPlayer.setVolume(curVolume, curVolume);

                  try {
                     Thread.sleep((long)(crossFadeTimeIntervalMS));
                  } catch (InterruptedException e) {
                     e.printStackTrace();
                  }
               }
               else
               {
                  difference = songDurationMS - curAudioTimeMS;
                  //Fade out
                  if (difference <= crossFadeDurationMS && difference >= 0)
                  {
                     System.out.println( "Difference " + (songDurationMS - curAudioTimeMS)+ "curmediaplayer" + curMediaPlayer + " crossfadeduration " + crossFadeDurationMS + " curvolume " + curVolume);
                     if(startFadeOut)
                     {
                        play(getUriOfNextSong());
                        startFadeOut = false;
                     }
                     curVolume -= crossFadeVolumeInterval;
                     curAudioTimeMS += crossFadeTimeIntervalMS;

                     curMediaPlayer.setVolume(curVolume, curVolume);
                     try {
                        Thread.sleep((long) (crossFadeTimeIntervalMS));
                     } catch (InterruptedException e) {
                        e.printStackTrace();
                     }
                  }
                  else
                  {
                     curAudioTimeMS += standartAudioIntervalMS;

                     try {
                        Thread.sleep(standartAudioIntervalMS);
                     } catch (InterruptedException e) {
                        e.printStackTrace();
                     }
                  }
               }
            }

         }
      }).start();
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

   public void stopLastPlayer()
   {
      //Due to the crossfade effect we got 2 players playing in the same time
      int lastMediaPlayerIndex = curSongPlayingIndex;
      MediaPlayer lastMediaPlayer = mediaPlayers.get(lastMediaPlayerIndex);

      System.out.println("Size in stop: " + mediaPlayers.size() + " curSongPlaingIndex " + curSongPlayingIndex);

      mediaPlayers.remove(lastMediaPlayer);
      lastMediaPlayer.release();

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
      return songList.get(curSongPlayingIndex).getUri();
   }

}
