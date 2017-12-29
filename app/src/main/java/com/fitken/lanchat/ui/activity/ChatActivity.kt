package com.fitken.lanchat.ui.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import com.fitken.lanchat.MyApplication
import com.fitken.lanchat.R
import com.fitken.lanchat.common.Constants
import com.fitken.lanchat.common.Constants.BROADCAST_MESSAGE_SOCKET_RECEIVED
import com.fitken.lanchat.databinding.ActivityChatBinding
import com.fitken.lanchat.debug.ShowLog
import com.fitken.lanchat.handler.ChatEventHandler
import com.fitken.lanchat.thread.SendMessageAsync
import com.fitken.lanchat.ui.adapter.ChatAdapter
import com.fitken.lanchat.ui.model.MessageSocket

class ChatActivity : AppCompatActivity(), ChatEventHandler {


    private val mBroadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            when (intent?.action) {
                BROADCAST_MESSAGE_SOCKET_RECEIVED -> {
                    val messageSocket = intent.extras.getSerializable(Constants.KEY_MESSAGE_SOCKET) as MessageSocket?
                    if (messageSocket != null) {
                        ShowLog.debug(messageSocket)
                    }
                    mChatAdapter.dataSource?.add(messageSocket)
                    mBinding.rvChat.adapter.notifyDataSetChanged()
                }

            }
        }
    }
    override fun send() {
        val messageSocket = MessageSocket()
        messageSocket.message = mBinding.etInput.text.toString()
        messageSocket.senderName = MyApplication.instance.getSocket().inetAddress.toString()
        messageSocket.type = Constants.TYPE_ME
        SendMessageAsync(MyApplication.instance.getSocket()).execute(messageSocket.message)
        mChatAdapter.dataSource.add(messageSocket)
        mBinding.etInput.setText("")
    }

    private lateinit var mBinding: ActivityChatBinding

    private lateinit var mChatAdapter: ChatAdapter

    private lateinit var mMessages: ArrayList<MessageSocket>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat)

        mBinding.eventHandler = this
        mChatAdapter = ChatAdapter()
        mBinding.rvChat.adapter = mChatAdapter

        mMessages = ArrayList()
        mChatAdapter.dataSource = mMessages

        LocalBroadcastManager.getInstance(MyApplication.instance)
                .registerReceiver(mBroadCastReceiver, IntentFilter(BROADCAST_MESSAGE_SOCKET_RECEIVED))


    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(MyApplication.instance)
                .unregisterReceiver(mBroadCastReceiver)
        super.onDestroy()
    }

}
