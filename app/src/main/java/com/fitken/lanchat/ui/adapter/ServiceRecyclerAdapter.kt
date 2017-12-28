package com.fitken.lanchat.ui.adapter

/**
 * Created by ken on 12/27/17.
 */
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.fitken.lanchat.R
import com.fitken.lanchat.ui.widget.RecyclerBaseAdapter
import com.fitken.lanchat.ui.widget.RecyclerBaseHolder
import de.mannodermaus.rxbonjour.BonjourService


class ServiceRecyclerAdapter :
        RecyclerBaseAdapter<BonjourService>() {
    override fun createViewHolder(inflater: LayoutInflater, parent: ViewGroup,
                                  viewType: Int) = Holder(inflater, parent)

    class Holder(inflater: LayoutInflater,
                 parent: ViewGroup)
        : RecyclerBaseHolder<BonjourService>(inflater, parent, R.layout.item_bonjour_service) {


        private var tvName: TextView = itemView.findViewById(R.id.tv_name)
        private var tvType: TextView = itemView.findViewById(R.id.tv_type)
        private var tvHostPortV4: TextView = itemView.findViewById(R.id.tv_host_port_v4)
        private var tvHostPortV6: TextView = itemView.findViewById(R.id.tv_host_port_v6)
        private var tvTxtRecords: TextView = itemView.findViewById(R.id.tv_txtrecords)

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
        }

    }
}