package com.fitken.lanchat.thread

import android.content.Context
import android.content.Intent
import android.os.Handler
import com.fitken.lanchat.MyApplication
import com.fitken.lanchat.ui.activity.ChatActivity
import de.mannodermaus.rxbonjour.BonjourService
import java.io.IOException
import java.net.Socket
import java.net.UnknownHostException

/**
 * Created by ken on 12/28/17.
 */

class ClientThread(context: Context, handler: Handler, bonjourService: BonjourService) : Runnable {

    private var mContext: Context = context
    private var mHandler: Handler = handler
    private lateinit var mSocket: Socket
    private var mBonjourService: BonjourService = bonjourService
    override fun run() {
        try {
            mSocket = Socket(mBonjourService.v4Host, mBonjourService.port)
            MyApplication.instance.setClientSocket(mSocket)
            val commThread = CommunicationThread(mContext, mHandler, mSocket)
            Thread(commThread).start()
            mContext.startActivity(Intent(mContext, ChatActivity::class.java))
        } catch (e1: UnknownHostException) {
            e1.printStackTrace()
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
    }

    fun getSocket(): Socket {
        return mSocket
    }
}