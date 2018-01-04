package com.fitken.lanchat.thread

import android.content.Context
import android.os.Build
import android.os.Handler
import com.fitken.lanchat.MyApplication
import com.fitken.lanchat.common.Constants
import com.fitken.lanchat.ui.model.MessageSocket
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.Socket

/**
 * Created by ken on 12/28/17.
 */

class CommunicationThread(context: Context, handler: Handler, clientSocket: Socket, threads: Array<CommunicationThread?>?) : Runnable {

    private var mContext: Context = context
    private var mInput: BufferedReader? = null
    var mClientSocket = clientSocket
    private var mThreads: Array<CommunicationThread?>? = threads

    init {
        try {
            this.mInput = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private val mUpdateConversationHandler: Handler = handler

    override fun run() {

        while (!Thread.currentThread().isInterrupted) {
            try {
                val read = mInput!!.readLine()
                val messageSocket = Gson().fromJson(read, MessageSocket::class.java) ?: continue
                messageSocket.type = if (messageSocket.senderName.equals(Build.MANUFACTURER + Build.MODEL)) Constants.TYPE_ME else Constants.TYPE_OTHER
                val updateUIThread = UpdateUIThread(mContext, messageSocket)
                mUpdateConversationHandler.post(updateUIThread)
                if (MyApplication.instance.getType() == Constants.TYPE_SERVER && mThreads != null) {
                    (0 until mThreads!!.size)
                            .filter { mThreads!![it] != null && mThreads!![it] != this }
                            .forEach { SendMessageAsync(mThreads!![it]!!.mClientSocket).execute(Gson().toJson(messageSocket)) }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
    }
}