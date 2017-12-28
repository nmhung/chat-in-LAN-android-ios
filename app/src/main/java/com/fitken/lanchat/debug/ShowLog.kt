package com.fitken.lanchat.debug

import android.util.Log

import com.fitken.lanchat.BuildConfig

/**
 * Created by ken on 12/26/17.
 */

object ShowLog {
    private val TAG = ShowLog::class.java.simpleName

    fun debug(msg: Any) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, StackTraceInfo.invokingMethodNameFqn)
            Log.d(TAG, msg.toString())
        }
    }

    fun error(msg: Any) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, StackTraceInfo.invokingFileNameFqn)
            Log.e(TAG, msg.toString())
        }
    }
}
