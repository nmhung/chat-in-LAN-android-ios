package com.fitken.lanchat.ui.adapter

/**
 * Created by ken on 12/27/17.
 */
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.fitken.lanchat.R
import com.fitken.lanchat.ui.widget.RecyclerBaseAdapter
import com.fitken.lanchat.ui.widget.RecyclerBaseHolder
import de.mannodermaus.rxbonjour.BonjourService


class ServiceRecyclerAdapter(activity: Activity, onItemClickListener: ServiceRecyclerAdapter.OnItemClickListener) : RecyclerBaseAdapter<BonjourService>() {
    private var onItemClick: OnItemClickListener? = onItemClickListener
    private var activity: Activity = activity
    override fun createViewHolder(inflater: LayoutInflater, parent: ViewGroup,
                                  viewType: Int) = Holder(activity, inflater, parent, this.onItemClick!!)

    class Holder(activity: Activity,
                 inflater: LayoutInflater,
                 parent: ViewGroup, itemClickListener: OnItemClickListener)
        : RecyclerBaseHolder<BonjourService>(inflater, parent, R.layout.item_bonjour_service) {


        var tvName: TextView = activity.findViewById(R.id.tv_name)
        var tvType: TextView = activity.findViewById(R.id.tv_type)
        var tvHostPortV4: TextView = activity.findViewById(R.id.tv_host_port_v4)
        var tvHostPortV6: TextView = activity.findViewById(R.id.tv_host_port_v6)
        var tvTxtRecords: TextView = activity.findViewById(R.id.tv_txtrecords)

        //        lateinit var tvTxtRecords: TextView
        private var onItemClick: OnItemClickListener? = itemClickListener

        private var context: Context? = null

        override fun onBindItem(item: BonjourService) {
            context = tvName.context
            tvName.text = item.name
            tvType.text = item.type

            // Display host address information
            tvHostPortV4.text = item.v4Host?.let {
                context!!.getString(R.string.format_host_address_v4, it, item.port)
            } ?: ""
            tvHostPortV6.text = item.v6Host?.let {
                context!!.getString(R.string.format_host_address_v6, it, item.port)
            } ?: ""

            // Display TXT records, if any could be resolved
            val txtRecords = item.txtRecords
            val txtRecordCount = txtRecords.size
            if (txtRecordCount > 0) {
                val txtRecordsText = StringBuilder()
                val keyIterator = txtRecords.keys.iterator()
                for (i in 0 until txtRecordCount) {
                    // Append key-value information for the TXT record
                    val key = keyIterator.next()
                    txtRecordsText
                            .append(key)
                            .append(" -> ")
                            .append(txtRecords[key])

                    // Add line break if more is coming
                    if (i < txtRecordCount - 1) txtRecordsText.append('\n')
                }
                tvTxtRecords.text = txtRecordsText.toString()

            } else {
                tvTxtRecords.text = tvTxtRecords.resources.getString(R.string.tv_notxtrecords)
            }

            itemView.setOnClickListener {
                onItemClick!!.onItemClicked(item)
            }
        }

    }


    public interface OnItemClickListener {
        fun onItemClicked(item: BonjourService)
    }

}