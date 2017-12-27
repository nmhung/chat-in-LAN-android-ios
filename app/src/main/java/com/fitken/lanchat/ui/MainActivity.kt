package com.fitken.lanchat.ui

import android.databinding.DataBindingUtil
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.fitken.lanchat.R
import com.fitken.lanchat.databinding.ActivityMainBinding
import com.fitken.lanchat.debug.ShowLog
import com.fitken.lanchat.handler.MainEventHandler
import com.fitken.lanchat.ui.adapter.DriverImplAdapter
import com.fitken.lanchat.ui.adapter.ServiceRecyclerAdapter
import de.mannodermaus.rxbonjour.*
import de.mannodermaus.rxbonjour.platforms.android.AndroidPlatform
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
import java.io.*
import java.net.Inet4Address
import java.net.ServerSocket
import java.net.Socket
import java.net.UnknownHostException

class MainActivity : AppCompatActivity(), MainEventHandler {
    override fun apply() {
        val input = mBinding.etInput.text
        if (input != null && input.isNotEmpty()) {
            // For non-empty input, restart the discovery with the new input
            restartDiscovery()
        }
    }

    private lateinit var mBinding: ActivityMainBinding
    private var nsdDisposable = Disposables.empty()
    private var socket: Socket? = null

    private lateinit var mListAdapter: ServiceRecyclerAdapter

    private lateinit var mSpinnerAdapter: DriverImplAdapter

    private var serverSocket: ServerSocket? = null

    internal lateinit var updateConversationHandler: Handler

    private var serverThread: Thread? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)

        mBinding.handler = this

        // Setup RecyclerView
        val onItemClickListener = object : ServiceRecyclerAdapter.OnItemClickListener {
            override fun onItemClicked(item: BonjourService) {

                Thread(ClientThread(item)).start()
            }
        }
        mListAdapter = ServiceRecyclerAdapter(this@MainActivity, onItemClickListener)
        mBinding.rvBonjourService.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.rvBonjourService.setEmptyView(mBinding.tvEmpty)
        mBinding.rvBonjourService.adapter = mListAdapter

        // Setup Spinner

        mSpinnerAdapter = DriverImplAdapter(this)
        mBinding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                // This will fire immediately when the first item is set
                restartDiscovery()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }
        mBinding.spinner.adapter = mSpinnerAdapter

        updateConversationHandler = Handler()

        this.serverThread = Thread(ServerThread())
        this.serverThread!!.start()
    }
//
//    @OnClick(R.id.btn_send)
//    fun onClick(view: View) {
//        AsyncSendMessage().execute(SystemClock.uptimeMillis().toString())
//
//    }

    private fun sendMessage(message: String) {
        try {
            val out = PrintWriter(BufferedWriter(
                    OutputStreamWriter(socket!!.getOutputStream())),
                    true)
            out.println(message)
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class, ClassNotFoundException::class, InterruptedException::class)
    private fun connectToServer(name: String, host: Inet4Address, port: Int) {
        //get the localhost IP address, if server is running on some other IP, you need to use that
//    val host = InetAddress.getLocalHost()
        var socket: Socket? = null
        var oos: ObjectOutputStream? = null
        var ois: ObjectInputStream? = null
        //establish socket connection to server
        socket = Socket(host, port)
        //write to socket using ObjectOutputStream
        oos = ObjectOutputStream(socket.getOutputStream())
        println("Sending request to Socket Server")
        oos.writeObject("" + name)
        //read the server response message
        ois = ObjectInputStream(socket.getInputStream())
        val message = ois.readObject() as String



        println("Message: " + message)
        //close resources
        ois.close()
        oos.close()
        Thread.sleep(100)
    }

    inner class AsyncSendMessage : AsyncTask<String, Void, Void>() {

        override fun doInBackground(vararg params: String): Void? {
            sendMessage(params[0])
            return null
        }
    }

    override fun onStop() {
        super.onStop()

        // Unsubscribe from the network service discovery Observable
        unSubscribe()
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    /* Begin private */

    private fun unSubscribe() {
        nsdDisposable.dispose()
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
                type = "_hung._tcp",
                name = "Hung " + Build.MANUFACTURER,
                address = null,
                port = 13337,
                txtRecords = mapOf(
                        "my.record" to "my value",
                        "other.record" to "0815"))
         rxBonjour.newBroadcast(broadcastConfig)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()

        nsdDisposable = rxBonjour.newDiscovery(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { mBinding.progressBar.visibility = View.VISIBLE }
                .doOnComplete { mBinding.progressBar.visibility = View.INVISIBLE }
                .doOnError { mBinding.progressBar.visibility = View.INVISIBLE }
                .subscribe(
                        { event ->


                            // Depending on the type of event and the availability of the item, adjust the adapter
                            val item = event.service
                            ShowLog.debug( "Event: " + item)
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




    internal inner class ClientThread(private val bonjourService: BonjourService) : Runnable {

        override fun run() {

            try {
                socket = Socket(bonjourService.v4Host, bonjourService.port)

            } catch (e1: UnknownHostException) {
                e1.printStackTrace()
            } catch (e1: IOException) {
                e1.printStackTrace()
            }

        }

    }

    internal inner class ServerThread : Runnable {

        override fun run() {
            var socket: Socket?
            try {
                serverSocket = ServerSocket(13337)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            while (!Thread.currentThread().isInterrupted) {

                try {

                    socket = serverSocket!!.accept()

                    val commThread = CommunicationThread(socket)
                    Thread(commThread).start()

                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
    }

    internal inner class CommunicationThread(private val clientSocket: Socket) : Runnable {

        private var input: BufferedReader? = null

        init {

            try {

                this.input = BufferedReader(InputStreamReader(this.clientSocket.getInputStream()))

            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        override fun run() {

            while (!Thread.currentThread().isInterrupted) {

                try {

                    val read = input!!.readLine()

                    updateConversationHandler.post(UpdateUIThread(read))

                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: IllegalStateException){
                    e.printStackTrace()
                }

            }
        }

    }

    internal inner class UpdateUIThread(private val msg: String) : Runnable {

        override fun run() {
            //update ui
            Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
        }
    }

}
