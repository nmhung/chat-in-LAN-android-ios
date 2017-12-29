package com.fitken.lanchat.ui.model

import com.fitken.lanchat.common.Constants.TYPE_ME
import com.fitken.lanchat.common.Constants.TYPE_OTHER
import java.io.Serializable

/**
 * Created by ken on 12/28/17.
 */

class MessageSocket : Serializable {

    var senderName: String? = null
    var message: String? = null
    var type: Int = TYPE_OTHER
}
