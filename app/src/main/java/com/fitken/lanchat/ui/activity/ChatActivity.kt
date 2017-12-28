package com.fitken.lanchat.ui.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.fitken.lanchat.MyApplication
import com.fitken.lanchat.R
import com.fitken.lanchat.databinding.ActivityChatBinding
import com.fitken.lanchat.handler.ChatEventHandler
import com.fitken.lanchat.thread.SendMessageAsync
import com.fitken.lanchat.ui.adapter.ChatAdapter
import com.fitken.lanchat.ui.model.MessageSocket

class ChatActivity : AppCompatActivity(), ChatEventHandler {
    override fun send() {
        SendMessageAsync(MyApplication.instance.getSocket()).execute(mBinding.etInput.text.toString())
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
        for (i in 0..10) {
            val messageSocket = MessageSocket()
            messageSocket.senderName = "Hung $i"
            messageSocket.message = "Hello $i"
            mMessages.add(messageSocket)
        }

        mChatAdapter.dataSource = mMessages
    }


}
