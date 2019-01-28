/*
 * Copyright (C) 2008 The Android Open Source Project
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

package com.better.alarm.presenter.background

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.IBinder
import android.os.PowerManager
import android.preference.PreferenceManager
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager

import com.better.alarm.model.AlarmsManager
import com.better.alarm.presenter.SettingsActivity
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.github.androidutils.logger.Logger
import com.github.androidutils.wakelock.WakeLockManager

import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe

import com.better.alarm.model.interfaces.Intents.DEFAULT_PREALARM_VOLUME
import com.better.alarm.model.interfaces.Intents.KEY_PREALARM_VOLUME


/**
 * Delegate everything to a [AlertService] which will play some awesome music, vibrate, show notfications
 * or whatever else. A wrapper is required to free [AlertService] from Android-SDK-dependent code making
 * it suitable for unit tests.
 */
class AlertServiceWrapper : Service(), AlertServiceWrapperCallback {
    /**
     * android.media.AudioManagerDispatches intents to the KlaxonService
     */
    class Receiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            intent.setClass(context, AlertServiceWrapper::class.java)
            WakeLockManager.getWakeLockManager().acquirePartialWakeLock(intent, "ForAlertServiceWrapper")
            context.startService(intent)
        }
    }

    private lateinit var delegate: KlaxonDelegate

    override fun onCreate() {
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        val callState = Observable.create(ObservableOnSubscribe<Int> { emitter ->
            emitter.onNext(tm.callState)

            val listener = object : PhoneStateListener() {
                override fun onCallStateChanged(state: Int, ignored: String) {
                    emitter.onNext(state)
                }
            }

            emitter.setCancellable {
                // Stop listening for incoming calls.
                tm.listen(listener, PhoneStateListener.LISTEN_NONE)
            }

            tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE)
        })

        val rxPrefs = RxSharedPreferences.create(PreferenceManager.getDefaultSharedPreferences(this))
        val fadeInTimeInSeconds = rxPrefs
                .getString(SettingsActivity.KEY_FADE_IN_TIME_SEC, "30")
                .asObservable()
                .map { s -> Integer.parseInt(s) * 1000 }

        delegate = AlertService(
                logger = Logger.getDefaultLogger(),
                powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager,
                wakelocks = WakeLockManager.getWakeLockManager(),
                alarms = AlarmsManager.getAlarmsManager(),
                wrapperCallback = this as AlertServiceWrapperCallback,
                resources = resources,
                callState = callState,
                prealarmVolume = rxPrefs.getInteger(KEY_PREALARM_VOLUME, DEFAULT_PREALARM_VOLUME).asObservable(),
                fadeInTimeInSeconds = fadeInTimeInSeconds
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return when (intent) {
            null -> Service.START_NOT_STICKY
            else -> {
                WakeLockManager.getWakeLockManager().releasePartialWakeLock(intent)
                when {
                    delegate.onStartCommand(intent) -> Service.START_STICKY
                    else -> Service.START_NOT_STICKY
                }
            }
        }
    }

    override fun onDestroy() {
        delegate.onDestroy()
    }

    override fun getDefaultUri(type: Int): Uri {
        return RingtoneManager.getDefaultUri(type)
    }

    override fun createMediaPlayer(): MediaPlayer {
        return MediaPlayer()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    interface KlaxonDelegate {
        fun onDestroy()

        fun onStartCommand(intent: Intent): Boolean
    }
}