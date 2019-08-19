package com.donteco.alarmClock;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.donteco.alarmClock.alarm.AlarmClock;
import com.donteco.alarmClock.alarm.DayOfWeek;


import java.util.ArrayList;
import java.util.List;

public class AlarmClockAdapter extends RecyclerView.Adapter<AlarmClockAdapter.AlarmClockViewHolder>
{
    private List<AlarmClock> alarmClocks;

    public AlarmClockAdapter() {
        alarmClocks = new ArrayList<>();

        AlarmClock alarmClock = new AlarmClock("Alarm 5", 19, 50, DayOfWeek.FRIDAY);
        alarmClock.setRepeat(true);
        AlarmClock alarmClock1 = new AlarmClock("Alarm 8", 16, 47, DayOfWeek.MONDAY);
        alarmClock1.setRepeat(true);
        alarmClocks.add(new AlarmClock("Alarm 1", 23, 54, DayOfWeek.MONDAY));
        alarmClocks.add(new AlarmClock("Alarm 2", 22, 53, DayOfWeek.TUESDAY));
        alarmClocks.add(new AlarmClock("Alarm 3", 21, 52, DayOfWeek.WEDNESDAY));
        alarmClocks.add(new AlarmClock("Alarm 4", 20, 51, DayOfWeek.THURSDAY));
        alarmClocks.add(alarmClock);
        alarmClocks.add(new AlarmClock("Alarm 6", 18, 49, DayOfWeek.SATURDAY));
        alarmClocks.add(new AlarmClock("Alarm 7", 17, 48, DayOfWeek.SUNDAY));
        alarmClocks.add(alarmClock1);
        alarmClocks.add(new AlarmClock("Alarm 9", 15, 46, DayOfWeek.TUESDAY));
        alarmClocks.add(new AlarmClock("Alarm 10", 14, 45, DayOfWeek.WEDNESDAY));
    }

    public void clearItems(){
        alarmClocks.clear();
        //for adapter to know, that list changed
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AlarmClockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.alarm_clock_recyclerview_element, parent, false);
        return new AlarmClockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmClockViewHolder holder, int position) {
        holder.bind(alarmClocks.get(position));
    }

    @Override
    public int getItemCount()
    {
        return alarmClocks.size();
    }

    public class AlarmClockViewHolder extends RecyclerView.ViewHolder
    {
        private TextView alarmTime;
        private TextView alarmName;
        private Switch alarmSwitch;

        public AlarmClockViewHolder(@NonNull View itemView)
        {
            super(itemView);
            alarmTime = itemView.findViewById(R.id.alarm_clock_chosen_time);
            alarmName = itemView.findViewById(R.id.alarm_clock_name);
            alarmSwitch = itemView.findViewById(R.id.alarm_clock_switch_btn);
        }

        public void bind(AlarmClock alarmClock)
        {
            String time = alarmClock.getHours() + " : " + alarmClock.getMinutes();

            alarmTime.setText(time);
            alarmName.setText(alarmClock.getName());

            if(alarmClock.isRepeat())
                alarmSwitch.setChecked(true);

        }
    }
}
