package com.fitken.lanchat.ui.activity

import android.content.*
import android.databinding.DataBindingUtil
import android.os.Build
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
import com.fitken.lanchat.util.AlertDialogUtils
import com.google.gson.Gson

class ChatActivity : AppCompatActivity(), ChatEventHandler {


    private val mBroadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            when (intent?.action) {
                BROADCAST_MESSAGE_SOCKET_RECEIVED -> {
                    val messageSocket = intent.extras.getSerializable(Constants.KEY_MESSAGE_SOCKET) as MessageSocket?
                    if (messageSocket != null) {
                        ShowLog.debug(messageSocket)
                        mChatAdapter.dataSource?.add(messageSocket)
                    }
                    mBinding.rvChat.adapter.notifyDataSetChanged()
                }

            }
        }
    }

    override fun send() {
        val message = mBinding.etInput.text.toString().trim()
        if (message.isBlank()) {
            return
        }
        val messageSocket = MessageSocket()
        messageSocket.message = message
        messageSocket.senderName = Build.MANUFACTURER + Build.MODEL
        messageSocket.type = Constants.TYPE_ME
        SendMessageAsync(MyApplication.instance.getSocket()).execute(Gson().toJson(messageSocket))
        if (MyApplication.instance.getType() == Constants.TYPE_SERVER) {
            mChatAdapter.dataSource?.add(messageSocket)
        }
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

    override fun onBackPressed() {
        AlertDialogUtils.showConfirm(this, "Are you sure you want to exit this conversation?",
                DialogInterface.OnClickListener { _, _ ->
                    super.onBackPressed()
                }, DialogInterface.OnClickListener { _, _ -> })

    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(MyApplication.instance)
                .unregisterReceiver(mBroadCastReceiver)
        super.onDestroy()
    }

}
