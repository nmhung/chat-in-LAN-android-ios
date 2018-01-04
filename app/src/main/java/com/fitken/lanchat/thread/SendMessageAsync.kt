package com.fitken.lanchat.thread

import android.os.AsyncTask
import com.fitken.lanchat.MyApplication
import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.Socket
import java.net.UnknownHostException

/**
 * Created by ken on 12/28/17.
 */
class SendMessageAsync(socket: Socket) : AsyncTask<String, Void, Void>() {
    private var mSocket: Socket = socket

    override fun doInBackground(vararg params: String): Void? {
        sendMessage(params[0])
        return null
    }

    private fun sendMessage(message: String) {
        try {
            val out = PrintWriter(BufferedWriter(
                    OutputStreamWriter(mSocket.getOutputStream())),
                    true)
            out.println(message)
            val threads = MyApplication.instance.getThreads()
            for (i in 0 until  threads!!.size) {
                if (threads[i] != null && !threads[i]!!.mClientSocket.inetAddress.toString().equals(mSocket.inetAddress.toString())) {
                    val out = PrintWriter(BufferedWriter(
                            OutputStreamWriter(threads[i]!!.mClientSocket.getOutputStream())),
                            true)
                    out.println(message)
                }
            }
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
