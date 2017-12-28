package com.fitken.lanchat.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.fitken.lanchat.base.BAdapter;
import com.fitken.lanchat.databinding.ItemChatMeBinding;
import com.fitken.lanchat.databinding.ItemChatOtherBinding;
import com.fitken.lanchat.ui.model.MessageSocket;

/**
 * Created by ken on 12/28/17.
 */

public class ChatAdapter extends BAdapter<BAdapter.BHolder, MessageSocket> {

    public static final int TYPE_ME = 0;
    public static final int TYPE_OTHER = 1;

    @Override
    protected BHolder getViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_ME:
                return new ItemMeViewHolder(ItemChatMeBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false));
            case TYPE_OTHER:
                return new ItemOtherViewHolder(ItemChatOtherBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false));

            default:
                return new ItemMeViewHolder(ItemChatMeBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false));
        }
    }

    public static class ItemMeViewHolder extends BAdapter.BHolder<ItemChatMeBinding, MessageSocket> {


        public ItemMeViewHolder(ItemChatMeBinding boundView) {
            super(boundView);
        }

    }

    public static class ItemOtherViewHolder extends BAdapter.BHolder<ItemChatOtherBinding, MessageSocket> {


        public ItemOtherViewHolder(ItemChatOtherBinding boundView) {
            super(boundView);
        }

    }
}
