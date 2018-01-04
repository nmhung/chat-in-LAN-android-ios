package com.fitken.lanchat.thread

import android.content.Context
import android.content.Intent
import android.os.Handler
import com.fitken.lanchat.MyApplication
import com.fitken.lanchat.common.Constants
import com.fitken.lanchat.debug.ShowLog
import com.fitken.lanchat.ui.activity.ChatActivity
import com.fitken.lanchat.ui.model.MessageSocket
import com.google.gson.Gson
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket

/**
 * Created by ken on 12/28/17.
 */

class ServerThread(context: Context, handler: Handler) : Runnable {
    private val MAX_CLIENT_COUNT: Int = 3
    private val mThreads = arrayOfNulls<CommunicationThread>(MAX_CLIENT_COUNT)
    private lateinit var mClientSocket: Socket
    private var mContext: Context = context
    private var mHandler: Handler = handler
    private lateinit var serverSocket: ServerSocket
    private var mIndex: Int = 0

    override fun run() {
        try {
            serverSocket = ServerSocket(Constants.PORT)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
            while (!Thread.currentThread().isInterrupted) {
                mClientSocket = serverSocket.accept()
                ShowLog.error("ket noi")
                if (!MyApplication.instance.isChatCreated()) {
                    MyApplication.instance.setServerSocket(mClientSocket)

                    val intent = Intent(mContext, ChatActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    mContext.startActivity(intent)
                    MyApplication.instance.setChatCreated(true)
                }
                for (mIndex in 0 until MAX_CLIENT_COUNT) {
                    ShowLog.error(mIndex)
                    if (mThreads[mIndex] == null) {
                        ShowLog.error("chay thread")
                        mThreads[mIndex] = CommunicationThread(mContext, mHandler, mClientSocket, mThreads)
                        Thread(mThreads[mIndex]).start()
                        break
                    }
                }
                MyApplication.instance.setThreads(mThreads)
                if (mIndex == MAX_CLIENT_COUNT) {
                    val messageSocket = MessageSocket()
                    messageSocket.message = "Server too busy. Try again later."
                    SendMessageAsync(mClientSocket).execute(Gson().toJson(messageSocket))
                }
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}

/*
 * The chat client thread. This client thread opens the input and the output
 * streams for a particular client, ask the client's name, informs all the
 * clients connected to the server about the fact that a new client has joined
 * the chat room, and as long as it receive data, echos that data back to all
 * other clients. When a client leaves the chat room this thread informs also
 * all the clients about that and terminates.
 */
internal class HandleClientsThread(context: Context, handler: Handler, private var clientSocket: Socket, private val threads: Array<HandleClientsThread?>) : Thread() {

    private val maxClientsCount: Int
    private var mContext: Context = context
    private var mHandler: Handler = handler

    init {
        this.clientSocket = clientSocket
        maxClientsCount = threads.size
    }

    override fun run() {
        val maxClientsCount = this.maxClientsCount
        val threads = this.threads
        try {
            /*
       * Create input and output streams for this client.
       */


//            val commThread = CommunicationThread(mContext, mHandler, clientSocket)
//            Thread(commThread).start()
            /*for (i in 0 until maxClientsCount) {

                if (threads[i] != null && threads[i] !== this) {
                    SendMessageAsync(clientSocket).execute("new user entered")
                }
            }


            for (i in 0 until maxClientsCount) {
                if (threads[i] != null && threads[i] !== this) {
                    SendMessageAsync(clientSocket).execute("a user has left")
                }
            }*/

            /*
       * Clean up. Set the current thread variable to null so that a new client
       * could be accepted by the server.
       */
            /*for (i in 0 until maxClientsCount) {
                if (threads[i] === this) {
                    threads[i] = null
                }
            }*/

            /*
       * Close the output stream, close the input stream, close the socket.
       */
            clientSocket.close()
        } catch (e: IOException) {
        }

    }
}