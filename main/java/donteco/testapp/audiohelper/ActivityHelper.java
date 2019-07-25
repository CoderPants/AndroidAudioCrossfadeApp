package donteco.testapp.audiohelper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.view.WindowManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class ActivityHelper  {

    private Activity activity;

    public ActivityHelper(Activity activity) {
        this.activity = activity;
    }

    public void getRidOfTopBar(){
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void getPermission()
    {
        if(ContextCompat.checkSelfPermission(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    ConstantsForApp.MY_PERMISSION_REQUEST);
    }

}
