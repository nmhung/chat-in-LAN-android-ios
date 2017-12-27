package com.fitken.lanchat.ui.adapter

import android.content.Context
import android.widget.ArrayAdapter
import com.fitken.lanchat.R
import com.fitken.lanchat.ui.model.DriverImpl

/**
 * Created by ken on 12/27/17.
 */
class DriverImplAdapter(context: Context)
    : ArrayAdapter<DriverImpl>(
        context,
        R.layout.support_simple_spinner_dropdown_item,
        DriverImpl.values())