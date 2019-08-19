package com.donteco.alarmClock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;

import com.donteco.alarmClock.components.MultiSpinner;
import com.donteco.alarmClock.help.ActivityHelper;

public class ChooseAlarmClockActivity extends AppCompatActivity {

    //Middle one
    NumberPicker pickerForHours;
    NumberPicker pickerForMinutes;

    //All in linear layout
    MultiSpinner daysSpinner;
    Button musicButton;
    Switch vibrationSwitch;
    EditText alarmName;
    EditText alarmDuration;

    ActivityHelper activityHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_alarm_clock);

        activityHelper = new ActivityHelper(this);
        activityHelper.getRidOfTopBar();

        Button cancelBtn = findViewById(R.id.choose_alarm_cancel_btn);
        Button acceptBtn = findViewById(R.id.choose_alarm_accept_btn);

        cancelBtnLogic(cancelBtn);

        pickerForHours = findViewById(R.id.np_choose_hours);
        pickerForMinutes = findViewById(R.id.np_choose_minutes);

        daysSpinner = findViewById(R.id.multispiner_choose_days);
        //implement
        //daysSpinner.setItems();

        musicButton = findViewById(R.id.choose_music);
        vibrationSwitch = findViewById(R.id.set_vibration_switch);
        alarmName = findViewById(R.id.et_alarm_clock_name);
        alarmDuration = findViewById(R.id.et_alarm_clock_duration);
    }

    private void cancelBtnLogic(Button cancel)
    {
        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(ChooseAlarmClockActivity.this, MainActivity.class);
                intent.putExtra("Canceled", true);
                startActivity(intent);
            }
        });
    }
}
