package com.better.alarm;

import java.util.Calendar;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.BaseColumns;

import com.better.alarm.BuildConfig;
import com.better.alarm.model.DaysOfWeek;
import com.better.alarm.model.IAlarmContainer;
import com.better.alarm.model.interfaces.Intents;
import com.github.androidutils.logger.Logger;
import com.github.androidutils.wakelock.WakeLockManager;

/**
 * Active record container for all alarm data.
 *
 * @author Yuriy
 *
 */
public class TestContainer implements IAlarmContainer {

    // This string is used to indicate a silent alarm in the db.
    private static final String ALARM_ALERT_SILENT = "silent";

    private int id;
    private boolean enabled;
    private int hour;
    private int minutes;
    private DaysOfWeek daysOfWeek;
    private boolean vibrate;
    private String label;
    private Uri alert;
    private boolean silent;
    private boolean prealarm;
    /**
     * Time when AlarmCore would normally go next time. Used to disable expired
     * alarms if devicee was off-line when they were supposed to fire
     *
     */
    private Calendar nextTime;
    private String state;

    public TestContainer(int id) {
        this.id = id;
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        hour = c.get(Calendar.HOUR_OF_DAY);
        minutes = c.get(Calendar.MINUTE);
        vibrate = true;
        daysOfWeek = new DaysOfWeek(0);
        nextTime = c;
        alert = null;
        prealarm = false;

        state = "";
    }

    /**
     * Persist data in the database
     */
    @Override
    public void writeToDb() {
    }

    private ContentValues createContentValues() {
        ContentValues values = new ContentValues(12);
        // id
        values.put(com.better.alarm.model.persistance.AlarmContainer.Columns.ENABLED, enabled);
        values.put(com.better.alarm.model.persistance.AlarmContainer.Columns.HOUR, hour);
        values.put(com.better.alarm.model.persistance.AlarmContainer.Columns.MINUTES, minutes);
        values.put(com.better.alarm.model.persistance.AlarmContainer.Columns.DAYS_OF_WEEK, daysOfWeek.getCoded());
        values.put(com.better.alarm.model.persistance.AlarmContainer.Columns.VIBRATE, vibrate);
        values.put(com.better.alarm.model.persistance.AlarmContainer.Columns.MESSAGE, label);
        // A null alert Uri indicates a silent
        values.put(com.better.alarm.model.persistance.AlarmContainer.Columns.ALERT, alert == null ? ALARM_ALERT_SILENT : alert.toString());
        values.put(com.better.alarm.model.persistance.AlarmContainer.Columns.PREALARM, prealarm);
        values.put(com.better.alarm.model.persistance.AlarmContainer.Columns.ALARM_TIME, nextTime.getTimeInMillis());
        values.put(com.better.alarm.model.persistance.AlarmContainer.Columns.STATE, state);

        return values;
    }

    @Override
    public void delete() {
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        writeToDb();
    }

    @Override
    public int getHour() {
        return hour;
    }

    @Override
    public void setHour(int hour) {
        this.hour = hour;
    }

    @Override
    public int getMinutes() {
        return minutes;
    }

    @Override
    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    @Override
    public DaysOfWeek getDaysOfWeek() {
        return daysOfWeek;
    }

    @Override
    public void setDaysOfWeek(DaysOfWeek daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    @Override
    public boolean isVibrate() {
        return vibrate;
    }

    @Override
    public void setVibrate(boolean vibrate) {
        this.vibrate = vibrate;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public Uri getAlert() {
        return alert;
    }

    @Override
    public void setAlert(Uri alert) {
        this.alert = alert;
    }

    @Override
    public boolean isSilent() {
        return silent;
    }

    @Override
    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    @Override
    public boolean isPrealarm() {
        return prealarm;
    }

    @Override
    public void setPrealarm(boolean prealarm) {
        this.prealarm = prealarm;
    }

    @Override
    public Calendar getNextTime() {
        return nextTime;
    }

    @Override
    public void setNextTime(Calendar nextTime) {
        this.nextTime = nextTime;
        writeToDb();
    }

    @Override
    public String getState() {
        return state;
    }

    @Override
    public void setState(String state) {
        this.state = state;
        writeToDb();
    }

    @Override
    public String toString() {
        return "AlarmContainer [id=" + id + ", enabled=" + enabled + ", hour=" + hour + ", minutes=" + minutes
                + ", daysOfWeek=" + daysOfWeek + ", vibrate=" + vibrate + ", label=" + label + ", alert=" + alert
                + ", silent=" + silent + ", prealarm=" + prealarm + ", nextTime=" + nextTime + ", state=" + state + "]";
    }
}
