package donteco.testapp.audiohelper;

import android.app.Activity;
import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import donteco.testapp.audiohelper.player.AudioPlayer;

public class PhoneCallHandler
{
    private Activity activity;
    private AudioPlayer audioPlayer;

    private boolean isCalling = false;

    public PhoneCallHandler(Activity activity, AudioPlayer audioPlayer)
    {
        this.activity = activity;
        this.audioPlayer = audioPlayer;
    }

    public void listenPhoneCalls()
    {
        PhoneStateListener phoneStateListener;
        TelephonyManager telephonyManager;

        telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);

        phoneStateListener = new PhoneStateListener()
        {
            @Override
            public void onCallStateChanged(int state, String phoneNumber)
            {
                switch (state)
                {
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (audioPlayer != null)
                        {
                            audioPlayer.pause();
                            isCalling = true;
                        }
                        break;

                    case TelephonyManager.CALL_STATE_IDLE:
                        if (audioPlayer != null)
                        {
                            if (isCalling) {
                                isCalling = false;
                                audioPlayer.play(audioPlayer.getUriOfNextSong());
                            }
                        }
                        break;
                }
            }
        };

        if (telephonyManager != null)
         telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }
}
