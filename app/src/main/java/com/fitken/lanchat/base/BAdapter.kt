package com.fitken.lanchat.base

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

import java.lang.reflect.InvocationTargetException
import java.util.ArrayList

import android.support.v7.widget.RecyclerView.NO_POSITION

/**
 * Created by vophamtuananh on 4/8/17.
 */

abstract class BAdapter<VH : BAdapter.BHolder<*, *>, T>
protected constructor(onItemClickListener: OnItemClickListener?) : RecyclerView.Adapter<VH>() {

    var dataSource: MutableList<T>? = null
    private var onItemClickListener: OnItemClickListener? = onItemClickListener

    protected abstract fun getViewHolder(parent: ViewGroup, viewType: Int): VH?

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH? {
        val viewHolder = getViewHolder(parent, viewType)
        viewHolder?.boundView?.root?.setOnClickListener { view ->
            val pos = viewHolder.adapterPosition
            if (pos != NO_POSITION) {
                onItemClicked(view, pos)
                if (onItemClickListener != null)
                    onItemClickListener!!.onItemClick(view, pos)
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bindData(dataSource!![position])
    }

    override fun getItemCount(): Int {
        return if (dataSource != null) dataSource!!.size else 0
    }

    protected fun onItemClicked(v: View, position: Int) {}

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    fun updateItem(position: Int, item: T) {
        dataSource!![position] = item
        notifyItemChanged(position)
    }

    fun appendItem(item: T?) {
        if (dataSource == null)
            dataSource = ArrayList()
        if (item != null) {
            dataSource!!.add(item)
            notifyItemInserted(dataSource!!.size)
        }
    }

    fun appenItems(items: List<T>?) {
        if (dataSource == null) {
            dataSource = items as MutableList<T>?
        } else {
            if (items != null && items.isNotEmpty()) {
                val positionStart = dataSource!!.size - 1
                dataSource!!.addAll(items)
                if (positionStart < 0)
                    notifyDataSetChanged()
                else
                    notifyItemRangeInserted(positionStart, items.size)
            }
        }
    }

    fun release() {
        onItemClickListener = null
    }

    open class BHolder<V : ViewDataBinding, T>(var boundView: V) : RecyclerView.ViewHolder(boundView.root) {

        fun bindData(model: Any?) {
            if (model != null) {
                val bindingMethods = boundView.javaClass.declaredMethods
                if (bindingMethods != null && bindingMethods.isNotEmpty()) {
                    for (method in bindingMethods) {
                        val parameterTypes = method.parameterTypes
                        if (parameterTypes != null && parameterTypes.size == 1) {
                            val clazz = parameterTypes[0]
                            try {
                                if (clazz.isInstance(model)) {
                                    method.isAccessible = true
                                    method.invoke(boundView, model)
                                } else if (clazz.isAssignableFrom(this.javaClass)) {
                                    method.isAccessible = true
                                    method.invoke(boundView, this)
                                }
                            } catch (e1: InvocationTargetException) {
                                e1.printStackTrace()
                            } catch (e2: IllegalAccessException) {
                                e2.printStackTrace()
                            }

                        }
                    }
                }
            }
        }
    }

}
