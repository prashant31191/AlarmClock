/*
 * Copyright (C) 2012 Yuriy Kulikov yuriy.kulikov.87@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.better.alarm.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.better.alarm.model.interfaces.AlarmNotFoundException;
import com.better.alarm.model.interfaces.PresentationToModelIntents;
import com.github.androidutils.logger.Logger;

public class AlarmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        Logger.getDefaultLogger().d("Got intent " + intent.getAction());

        Alarms alarms = AlarmsManager.getInstance();
        Logger log = Logger.getDefaultLogger();

        try {
            String action = intent.getAction();
            if (action.equals(AlarmsScheduler.ACTION_FIRED)) {
                int id = intent.getIntExtra(AlarmsScheduler.EXTRA_ID, -1);

                AlarmCore alarm = alarms.getAlarm(id);
                alarms.onAlarmFired(alarm,
                        CalendarType.valueOf(intent.getExtras().getString(AlarmsScheduler.EXTRA_TYPE)));
                log.d("AlarmCore fired " + id);

            } else if (action.equals(Intent.ACTION_BOOT_COMPLETED) || action.equals(Intent.ACTION_TIMEZONE_CHANGED)
                    || action.equals(Intent.ACTION_LOCALE_CHANGED) || action.equals(Intent.ACTION_MY_PACKAGE_REPLACED)) {
                log.d("Refreshing alarms because of " + action);
                alarms.refresh();

            } else if (action.equals(Intent.ACTION_TIME_CHANGED)) {
                alarms.onTimeSet();

            } else if (action.equals(PresentationToModelIntents.ACTION_REQUEST_SNOOZE)) {
                int id = intent.getIntExtra(AlarmsScheduler.EXTRA_ID, -1);
                alarms.getAlarm(id).snooze();

            } else if (action.equals(PresentationToModelIntents.ACTION_REQUEST_DISMISS)) {
                int id = intent.getIntExtra(AlarmsScheduler.EXTRA_ID, -1);
                alarms.getAlarm(id).dismiss();
            }
        } catch (AlarmNotFoundException e) {
            Logger.getDefaultLogger().d("Alarm not found");
        }
    }
}
