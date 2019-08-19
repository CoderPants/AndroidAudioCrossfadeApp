package com.donteco.alarmClock;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

public class TimeHandler implements Runnable
{
    private TextView timeTV;
    private Activity activity;
    private boolean countingTime;

    private String timeFormat;

    public TimeHandler(Activity activity, TextView timeTV)
    {
        this.timeTV = timeTV;
        this.activity = activity;

        timeFormat = "HH:mm:ss";
        countingTime = true;
    }

    public void setCountingTime(boolean countingTime) {
        this.countingTime = countingTime;
    }


    //Main thread for getting time
    @Override
    public void run()
    {
        try
        {
            while (countingTime)
            {
                activity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        updateTime();
                    }
                });

                Thread.sleep(ConstantsForApp.ONE_SECOND_PAUSE);
            }
        }
        catch (InterruptedException e)
        {
            Log.e(ConstantsForApp.LOG_TAG, "InterruptedException in time thread", e);
        }
    }

    //Need to find better way to handle it
    private void updateTime()
    {
        Date curDate = Calendar.getInstance().getTime();
        timeTV.setText(android.text.format.DateFormat.format(timeFormat, curDate));
    }
}
