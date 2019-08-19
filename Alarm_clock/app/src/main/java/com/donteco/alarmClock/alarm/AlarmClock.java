package com.donteco.alarmClock.alarm;

import android.media.MediaPlayer;

public class AlarmClock
{
    private String name;

    private int hours;
    private int minutes;
    //Goes from 1 to 7
    private DayOfWeek dayOfWeek;

    //For music playing
    private MediaPlayer audioPlayer;

    private boolean repeat;
    private boolean hasMusic;

    public AlarmClock(String name, int hours, int minutes, DayOfWeek dayOfWeek)
    {
        this.name = name;
        this.hours = hours;
        this.minutes = minutes;
        this.dayOfWeek = dayOfWeek;

        audioPlayer = null;
        repeat = false;
        hasMusic = false;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setAudioPlayer(MediaPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public void setHasMusic(boolean hasMusic) {
        this.hasMusic = hasMusic;
    }

    public String getName() {
        return name;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }


    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public MediaPlayer getAudioPlayer() {
        return audioPlayer;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public boolean isHasMusic() {
        return hasMusic;
    }
}
