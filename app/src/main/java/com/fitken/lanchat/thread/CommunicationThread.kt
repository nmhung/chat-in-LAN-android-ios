package com.fitken.lanchat.thread

import android.content.Context
import android.os.Handler
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.Socket

/**
 * Created by ken on 12/28/17.
 */

class CommunicationThread(context: Context, handler: Handler, clientSocket: Socket) : Runnable {

    private var mContext: Context = context
    private var mInput: BufferedReader? = null

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
                mUpdateConversationHandler.post(UpdateUIThread(mContext, read))
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
    }
}