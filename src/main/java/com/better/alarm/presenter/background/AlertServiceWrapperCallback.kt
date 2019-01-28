package com.better.alarm.presenter.background

import android.media.MediaPlayer
import android.net.Uri

interface AlertServiceWrapperCallback {
    fun stopSelf()

    fun getDefaultUri(type: Int): Uri

    fun createMediaPlayer(): MediaPlayer
}