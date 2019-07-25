package donteco.testapp.audiohelper;

import android.app.Activity;
import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import donteco.testapp.audiohelper.player.AudioPlayer;

public class PhoneCallHandler {

    private Activity activity;
    private AudioPlayer audioPlayer;

    private boolean isCalling = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;

    public PhoneCallHandler(Activity activity, AudioPlayer audioPlayer)
    {
        this.activity = activity;
        this.audioPlayer = audioPlayer;
    }

    public void listenPhoneCalls()
    {
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
                        // Phone idle. Start playing.
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
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }
}
