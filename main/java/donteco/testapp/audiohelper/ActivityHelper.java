package donteco.testapp.audiohelper;

import android.app.Activity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class ActivityHelper  {

    private Activity activity;

    public ActivityHelper(Activity activity) {
        this.activity = activity;
    }

    public void getRidOfTopBar(){
        activity.getWindow().setFlags(ConstantsForApp.FULL_SCREEN, ConstantsForApp.FULL_SCREEN);
    }

    public void getPermission()
    {
        if(ContextCompat.checkSelfPermission(activity, ConstantsForApp.STORAGE_PERMISSION_REQUEST)
                != ConstantsForApp.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(activity,
                    new String[]{ConstantsForApp.STORAGE_PERMISSION_REQUEST},
                    ConstantsForApp.MY_PERMISSION_REQUEST);
    }

}
