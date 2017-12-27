package com.fitken.lanchat.ui.model

/**
 * Created by ken on 12/27/17.
 */
import android.content.Context
import de.mannodermaus.rxbonjour.Driver

enum class DriverImpl(
        private val library: String,
        private val artifact: String,
        val factory: (Context) -> Driver) {

    JMDNS(
            "JmDNS",
            "rxbonjour-driver-jmdns",
            { de.mannodermaus.rxbonjour.drivers.jmdns.JmDNSDriver.create() }),

    NSDMANAGER(
            "NsdManager",
            "rxbonjour-driver-nsdmanager",
            { de.mannodermaus.rxbonjour.drivers.nsdmanager.NsdManagerDriver.create(it) });

    override fun toString(): String = "$library ($artifact)"
}
