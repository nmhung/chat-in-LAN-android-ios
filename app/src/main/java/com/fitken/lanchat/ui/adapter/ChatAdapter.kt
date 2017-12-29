package com.fitken.lanchat.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup

import com.fitken.lanchat.base.BAdapter
import com.fitken.lanchat.common.Constants
import com.fitken.lanchat.databinding.ItemChatMeBinding
import com.fitken.lanchat.databinding.ItemChatOtherBinding
import com.fitken.lanchat.ui.model.MessageSocket

/**
 * Created by ken on 12/28/17.
 */

class ChatAdapter : BAdapter<BAdapter.BHolder<*, *>, MessageSocket>() {


    override fun getViewHolder(parent: ViewGroup, viewType: Int): BAdapter.BHolder<*, *> {
        return when (viewType) {
            Constants.TYPE_ME -> ItemMeViewHolder(ItemChatMeBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false))
            Constants.TYPE_OTHER -> ItemOtherViewHolder(ItemChatOtherBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false))

            else -> ItemMeViewHolder(ItemChatMeBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return dataSource[position].type
    }

    class ItemMeViewHolder(boundView: ItemChatMeBinding) : BAdapter.BHolder<ItemChatMeBinding, MessageSocket>(boundView)

    class ItemOtherViewHolder(boundView: ItemChatOtherBinding) : BAdapter.BHolder<ItemChatOtherBinding, MessageSocket>(boundView)
}
