package com.fitken.lanchat.thread

import android.content.Context
import android.widget.Toast

/**
 * Created by ken on 12/28/17.
 */

open class UpdateUIThread(context: Context, message: String) : Runnable {

    private var mContext : Context = context
    private var mMessage : String = message

    override fun run() {
        Toast.makeText(mContext, mMessage, Toast.LENGTH_SHORT).show()
    }
}