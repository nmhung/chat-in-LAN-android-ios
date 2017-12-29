package com.fitken.lanchat.thread

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import com.fitken.lanchat.common.Constants
import com.fitken.lanchat.ui.model.MessageSocket

/**
 * Created by ken on 12/28/17.
 */

open class UpdateUIThread(context: Context, messageSocket: MessageSocket?) : Runnable {

    private var mContext: Context = context
    private var mMessage: MessageSocket? = messageSocket

    override fun run() {
        val intent = Intent()
        intent.action = Constants.BROADCAST_MESSAGE_SOCKET_RECEIVED
        val bundle = Bundle()
        bundle.putSerializable(Constants.KEY_MESSAGE_SOCKET, mMessage)
        intent.putExtras(bundle)
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent)
    }

}