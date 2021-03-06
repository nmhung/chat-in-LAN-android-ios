package com.fitken.lanchat

import android.app.Application
import android.os.Handler
import com.fitken.lanchat.common.Constants.TYPE_CLIENT
import com.fitken.lanchat.common.Constants.TYPE_SERVER
import com.fitken.lanchat.thread.ClientThread
import com.fitken.lanchat.thread.CommunicationThread
import com.fitken.lanchat.thread.ServerThread
import de.mannodermaus.rxbonjour.BonjourService
import java.net.Socket

/**
 * Created by ken on 12/28/17.
 */

class MyApplication : Application() {

    companion object {
        lateinit var instance: MyApplication
    }

    init {
        instance = this
    }


    private lateinit var mServerThread : Thread
    private lateinit var mUpdateConversationHandler: Handler
    private lateinit var mClientThread: Thread
    private lateinit var mServerSocket: Socket
    private lateinit var mClientSocket: Socket
    private var mType: Int = TYPE_SERVER
    private var mIsChatCreated: Boolean = false
    private var mThreads : Array<CommunicationThread?>? = null

    override fun onCreate() {
        super.onCreate()

        mType = TYPE_SERVER
        mUpdateConversationHandler = Handler()
        mServerThread = Thread(ServerThread(this, mUpdateConversationHandler))
        mServerThread.start()
    }

    fun runClientThread(bonjourService: BonjourService) {
        mClientThread = Thread(ClientThread(this, mUpdateConversationHandler, bonjourService))
        mClientThread.start()
    }

    fun setServerSocket(socket: Socket) {
        mServerSocket = socket
        mType = TYPE_SERVER
    }

    fun getSocket(): Socket {
        if (mType == TYPE_SERVER) {
            return mServerSocket
        }
        return mClientSocket
    }

    fun setClientSocket(socket: Socket) {
        mClientSocket = socket
        mType = TYPE_CLIENT
    }

    fun getType(): Int {
        return mType
    }

    fun isChatCreated(): Boolean {
        return mIsChatCreated
    }

    fun setChatCreated(isChatCreated: Boolean){
        mIsChatCreated = isChatCreated
    }

    fun getThreads() : Array<CommunicationThread?>? {
        return mThreads
    }

    fun setThreads(threads : Array<CommunicationThread?>?){
        mThreads = threads
    }
}