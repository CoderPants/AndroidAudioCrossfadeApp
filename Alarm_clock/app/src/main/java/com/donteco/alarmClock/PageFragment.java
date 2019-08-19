package com.donteco.alarmClock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.donteco.alarmClock.help.ConstantsForApp;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class PageFragment extends Fragment
{
    private static final String KEY = "key_for_bundle";

    private Activity activity;

    private int pageNumber;

    private Timer timer;
    private TimeHandler timeHandler;
    private TextView currentTimeTV;

    //Because it's highly recommended by doc
    //Constructor needs to be empty
    public static PageFragment getInstance(int pageNum)
    {
        PageFragment pageFragment = new PageFragment();
        Bundle argsForFragment = new Bundle();
        argsForFragment.putInt(KEY, pageNum);
        pageFragment.setArguments(argsForFragment);
        return pageFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(getArguments() == null)
        {
            Log.e(ConstantsForApp.LOG_TAG, "Null pointer in getArguments function in PageFragment class onCreate method");
            pageNumber = 1;
        }
        else
            pageNumber = getArguments().getInt(KEY);

        activity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        int viewId = Integer.MAX_VALUE;

        switch (pageNumber)
        {
            case 0:
                System.out.println("Got into the first page ");
                viewId = R.layout.show_time_fragment;
                break;

            case 1:
                System.out.println("Got into the second page ");
                viewId = R.layout.alarm_clock_fragment;
                break;

            case 2:
                //for the third page
                break;
        }

        if(viewId == Integer.MAX_VALUE)
        {
            Log.wtf(ConstantsForApp.LOG_TAG,
                    "In PageFormat class in onCreateView method error - can't find view layout");
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        //Why null????
        //NEED TO BE FIXED
        //first one was (show_time...., null)
        return inflater.inflate(viewId, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        switch (pageNumber)
        {
            case 0:
                currentTimeTV = view.findViewById(R.id.show_time_tv_current_time);
                timeFragmentLogic();
                break;

            case 1:
                RecyclerView recyclerView = view.findViewById(R.id.rv_alarm_clocks);
                alarmFragmentLogic(recyclerView);

                ImageButton imageButton = view.findViewById(R.id.ib_add_alarm_clock_btn);
                addAlarmClockBtnLogic(imageButton);
                break;

        }
    }

    //For timer deleting
    @Override
    public void onPause() {

        if(pageNumber == 0 && timer != null)
        {
            currentTimeTV.setText("");
            timer.cancel();
            timer = null;
        }

        super.onPause();
    }

    //For timer resuming
    @Override
    public void onResume() {

        if (pageNumber == 0)
            timeFragmentLogic();

        super.onResume();
    }

    //Get info about threadsafe state
    //Not on destroy, cos we won't close all out app
    //Ony one fragment
    @Override
    public void onDestroyView()
    {
        if(pageNumber == 0 && timer != null)
        {
            currentTimeTV.setText("");
            timer.cancel();
            timer = null;
        }

        super.onDestroyView();
    }

    private void timeFragmentLogic()
    {
        if(timer != null)
            timer.cancel();

        //If JVM stop it before quiting from app
        timer = new Timer(true);
        timeHandler = new TimeHandler();
        timer.schedule(timeHandler, 0, ConstantsForApp.ONE_SECOND_PAUSE);
    }

    private void alarmFragmentLogic(RecyclerView recyclerView)
    {
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(new AlarmClockAdapter());
    }

    private void addAlarmClockBtnLogic(ImageButton imageButton)
    {
        imageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent addAlarmIntent = new Intent(getActivity(), ChooseAlarmClockActivity.class);
                startActivityForResult(addAlarmIntent, ConstantsForApp.ALARM_INFO_REQUEST);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class TimeHandler extends TimerTask
    {
        private String timeFormat;

        private TimeHandler() {
            timeFormat = "HH:mm:ss";
        }

        //Main thread for getting time
        @Override
        public void run()
        {
            activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run() {
                    updateTime();
                }
            });
        }

        //Need to find better way to handle it
        private void updateTime()
        {
            currentTimeTV.setText(android.text.format.DateFormat.format( timeFormat,
                    Calendar.getInstance().getTime() ));
        }
    }
}
