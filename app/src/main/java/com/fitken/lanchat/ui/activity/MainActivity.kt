package com.fitken.lanchat.ui.activity

import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.fitken.lanchat.MyApplication
import com.fitken.lanchat.R
import com.fitken.lanchat.common.Constants
import com.fitken.lanchat.databinding.ActivityMainBinding
import com.fitken.lanchat.debug.ShowLog
import com.fitken.lanchat.handler.MainEventHandler
import com.fitken.lanchat.ui.adapter.DriverImplAdapter
import com.fitken.lanchat.ui.adapter.ServiceRecyclerAdapter
import com.fitken.lanchat.ui.widget.RecyclerBaseAdapter
import com.fitken.lanchat.ui.widget.RecyclerBaseHolder
import de.mannodermaus.rxbonjour.*
import de.mannodermaus.rxbonjour.platforms.android.AndroidPlatform
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity(), MainEventHandler, RecyclerBaseAdapter.OnItemClickListener<BonjourService> {
    override fun onItemClick(holder: RecyclerBaseHolder<BonjourService>, item: BonjourService) {
        MyApplication.instance.runClientThread(item)
    }

    override fun apply() {
        val input = mBinding.etInput.text.toString()
        if (input.isNotBlank()) {
            // For non-empty input, restart the discovery with the new input
            restartDiscovery()
        }
    }

    private lateinit var mBinding: ActivityMainBinding
    private var mNsdDisposable = Disposables.empty()
    private lateinit var mListAdapter: ServiceRecyclerAdapter
    private lateinit var mSpinnerAdapter: DriverImplAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)

        mBinding.handler = this
        mListAdapter = ServiceRecyclerAdapter()
        mListAdapter.setOnItemClickListener(this)
        mBinding.rvBonjourService.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.rvBonjourService.setEmptyView(mBinding.tvEmpty)
        mBinding.rvBonjourService.adapter = mListAdapter

        mSpinnerAdapter = DriverImplAdapter(this)
        mBinding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                // This will fire immediately when the first item is set
                restartDiscovery()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }
        mBinding.spinner.adapter = mSpinnerAdapter
    }

    override fun onStop() {
        super.onStop()
        // UnSubscribe from the network service discovery Observable
        unSubscribe()
    }


    /* Begin private */
    private fun unSubscribe() {
        mNsdDisposable.dispose()
    }

    private fun restartDiscovery() {
        // Check the current input, only proceed if valid
        val type = mBinding.etInput.text.toString()
        if (!type.isBonjourType()) {
            Toast.makeText(this, getString(R.string.toast_invalidtype, type), Toast.LENGTH_SHORT).show()
            return
        }

        // Cancel any previous subscription
        unSubscribe()

        // Clear the adapter's items, then start a new discovery
        mListAdapter.clearItems()

        // Construct a new RxBonjour instance with the currently selected Driver.
        // Usually, you'd simply add the Driver inside the Builder
        // and provide the entry point to RxBonjour globally,
        // e.g. through Dependency Injection, or as an instance field.
        //
        // RxBonjour rxBonjour = new RxBonjour.Builder()
        //      .driver(JmDNSDriver.create())
        //      .platform(AndroidPlatform.create(this))
        //      .create();
        //
        // Since in this sample Driver implementations can be switched,
        // we're using the SpinnerAdapter for this.
        val driverLibrary = mSpinnerAdapter.getItem(mBinding.spinner.selectedItemPosition)

        val rxBonjour = RxBonjour.Builder()
                .driver(driverLibrary.factory.invoke(this))
                .platform(AndroidPlatform.create(this))
                .create()

        val broadcastConfig = BonjourBroadcastConfig(
                type = type,
                name = Build.MANUFACTURER + Build.MODEL,
                address = null,
                port = Constants.PORT,
                txtRecords = mapOf(
                        "my.record" to "my value",
                        "other.record" to "0815"))
        rxBonjour.newBroadcast(broadcastConfig)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                        },
                        { error ->
                            error.printStackTrace()
                            Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_SHORT).show()
                        }
                )

        mNsdDisposable = rxBonjour.newDiscovery(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { mBinding.progressBar.visibility = View.VISIBLE }
                .doOnComplete { mBinding.progressBar.visibility = View.INVISIBLE }
                .doOnError { mBinding.progressBar.visibility = View.INVISIBLE }
                .subscribe(
                        { event ->


                            // Depending on the type of event and the availability of the item, adjust the adapter
                            val item = event.service
                            ShowLog.debug("Event: " + item)
                            when (event) {
                                is BonjourEvent.Added -> if (!mListAdapter.containsItem(item)) mListAdapter.addItem(
                                        item)
                                is BonjourEvent.Removed -> if (mListAdapter.containsItem(
                                        item)) mListAdapter.removeItem(item)
                            }
                        },
                        { error ->
                            error.printStackTrace()
                            Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_SHORT).show()
                        })


    }
}
