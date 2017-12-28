package com.fitken.lanchat.thread

import android.content.Context
import android.content.Intent
import android.os.Handler
import com.fitken.lanchat.MyApplication
import com.fitken.lanchat.common.Constants
import com.fitken.lanchat.ui.activity.ChatActivity
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket

/**
 * Created by ken on 12/28/17.
 */

class ServerThread(context: Context, handler: Handler) : Runnable {
    private var mContext: Context = context
    private var mHandler: Handler = handler
    private lateinit var serverSocket: ServerSocket
    override fun run() {
        var socket: Socket?
        try {
            serverSocket = ServerSocket(Constants.PORT)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        while (!Thread.currentThread().isInterrupted) {
            try {
                socket = serverSocket.accept()
                MyApplication.instance.setServerSocket(socket)

                val commThread = CommunicationThread(mContext, mHandler, socket)
                Thread(commThread).start()
                mContext.startActivity(Intent(mContext, ChatActivity::class.java))

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}