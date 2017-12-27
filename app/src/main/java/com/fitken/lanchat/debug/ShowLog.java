package com.fitken.lanchat.debug;

import android.util.Log;

import com.fitken.lanchat.BuildConfig;

/**
 * Created by ken on 12/26/17.
 */

public class ShowLog {
    public static final String TAG = ShowLog.class.getSimpleName();

    public static void debug(Object msg) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, StackTraceInfo.getInvokingMethodNameFqn());
            Log.d(TAG, msg.toString());
        }
    }

    public static void error(Object msg) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, StackTraceInfo.getInvokingFileNameFqn());
            Log.e(TAG, msg.toString());
        }
    }
}
