package com.better.alarm;

import android.content.Context;

import com.better.alarm.model.AlarmCore;
import com.better.alarm.model.CalendarType;
import com.better.alarm.model.IAlarmContainer;
import com.better.alarm.model.IAlarmsScheduler;
import com.better.alarm.model.ImmedeateHandlerFactory;
import com.better.alarm.model.interfaces.Intents;
import com.github.androidutils.logger.Logger;
import com.github.androidutils.statemachine.HandlerFactory;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AlarmCoreTest {
    private AlarmCore alarmCore;
    private IAlarmsScheduler alarmsScheduler;
    private AlarmCore.IStateNotifier broadcaster;

    @Before
    public void setup() {
        IAlarmContainer container = new TestContainer(1);
        Context context = mock(Context.class);
        Logger logger = Logger.getDefaultLogger();
        alarmsScheduler = mock(IAlarmsScheduler.class);
        broadcaster = mock(AlarmCore.IStateNotifier.class);
        HandlerFactory handlerFactory = new ImmedeateHandlerFactory();
        alarmCore = new AlarmCore(container, context, logger, alarmsScheduler, broadcaster, handlerFactory);
    }

    @Test
    public void enable_should_set_up_the_alarm() {
        alarmCore.enable(true);
        verify(alarmsScheduler).setAlarm(eq(1), eq(CalendarType.NORMAL), any(Calendar.class));
    }

    @Test
    public void fired_alarm_should_start_the_service() {
        alarmCore.enable(true);
        verify(alarmsScheduler).setAlarm(eq(1), eq(CalendarType.NORMAL), any(Calendar.class));
        alarmCore.onAlarmFired(CalendarType.NORMAL);
        verify(broadcaster).broadcastAlarmState(eq(1), eq(Intents.ALARM_ALERT_ACTION));
    }
}
