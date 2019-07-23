package donteco.testapp.audiohelper;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.WindowManager;

public class ConstantsForApp {

    public static final int AUDIO_COUNT = 2;
    public static final int STARTING_POSITION = 0;

    public static final int CROSSFADE_STEP_AMOUNT = 100;
    public static final int CROSSFADE_MAX_VOLUME = 1;

    public static final int MY_PERMISSION_REQUEST = 1;
    public static final int PERMISSION_GRANTED = PackageManager.PERMISSION_GRANTED;
    public static final String STORAGE_PERMISSION_REQUEST = Manifest.permission.READ_EXTERNAL_STORAGE;

    public static final String ACTION_PICK = Intent.ACTION_PICK;
    public static final Uri  EXTERNAL_CONTENT_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

    public static final int FULL_SCREEN = WindowManager.LayoutParams.FLAG_FULLSCREEN;
}
