package com.better.alarm.presenter.background

import android.content.Intent
import android.content.res.Resources
import android.os.PowerManager

import com.better.alarm.model.interfaces.IAlarmsManager
import com.github.androidutils.logger.Logger
import com.github.androidutils.wakelock.WakeLockManager

import io.reactivex.Observable

internal class AlertService(
        logger: Logger,
        powerManager: PowerManager,
        wakelocks: WakeLockManager,
        alarms: IAlarmsManager,
        wrapperCallback: AlertServiceWrapperCallback,
        resources: Resources,
        callState: Observable<Int>,
        prealarmVolume: Observable<Int>,
        fadeInTimeInSeconds: Observable<Int>
) : AlertServiceWrapper.KlaxonDelegate {

    override fun onDestroy() {

    }

    override fun onStartCommand(intent: Intent): Boolean {
        return false
    }
}
