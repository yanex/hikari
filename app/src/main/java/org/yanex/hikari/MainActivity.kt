package org.yanex.hikari

import android.bluetooth.BluetoothManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import org.jetbrains.anko.*
import org.yanex.flake.FlakeContext
import org.yanex.flake.FlakeLayout
import org.yanex.flake.FlakeManager
import org.yanex.hikari.flakes.DevicesFlake
import org.yanex.hikari.lamp.Device
import org.yanex.hikari.lamp.DeviceManager
import org.yanex.hikari.lamp.DeviceState
import org.yanex.hikari.util.menu.DelegatedMenuActivity
import org.yanex.hikari.util.menu.MenuFactory
import org.yanex.hikari.util.menu.consume
import org.yanex.hikari.util.getSystemService

class MainActivity : DelegatedMenuActivity() {
    private val ID_FLAKE_LAYOUT = 1000
    private lateinit var flakeManager: FlakeManager

    private val newDeviceHandler = object : DeviceManager.Handler {
        private fun sendMessage(msg: Any) = flakeManager.flakeContext.sendMessage<DevicesFlake>(msg)

        override fun onDeviceStateUpdated(state: DeviceState) {
            flakeManager.flakeContext.sendBroadcastMessage(state)
        }

        override fun onResyncRequested() = sendMessage(DevicesFlake.ResyncRequested)
        override fun onNewDevice(device: Device) = sendMessage(device)
        override fun onDeviceAvailabilityStateChanged(device: Device, isAvailable: Boolean) {
            sendMessage(DevicesFlake.DeviceAvailabilityStatusChanged(device, isAvailable))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //todo reuse on configuration changes
        val deviceManager = createDeviceManager() ?: return

        val flakeContext = FlakeContext.create(this, savedInstanceState)
        flakeContext.useComponent(menuManager)

        verticalLayout {
            include<Toolbar>(R.layout.toolbar) {
                setSupportActionBar(this)
                flakeContext.useComponent(this)
            }

            flakeLayout {
                id = ID_FLAKE_LAYOUT
            }.lparams(matchParent, matchParent)
        }

        flakeManager = FlakeManager.create(find<FlakeLayout>(ID_FLAKE_LAYOUT), flakeContext)

        deviceManager.apply {
            flakeContext.useComponent(this)
            addHandler(newDeviceHandler)
            scan()
            connectAll()
        }

        flakeManager.restoreStateOrShow { DevicesFlake() }
    }

    private fun createDeviceManager(): DeviceManager? {
        val bluetoothAdapter = getSystemService<BluetoothManager>()?.adapter
        if (bluetoothAdapter == null) {
            toast(R.string.main_bluetooth_not_supported)
            finish()
            return null
        }

        return DeviceManager(bluetoothAdapter, this)
    }

    override fun onDestroy() {
        super.onDestroy()
        flakeManager.flakeContext.deviceManager.apply {
            removeHandler(newDeviceHandler)
            disconnectAll()
        }
    }

    override fun onBackPressed() {
        val flakeManager = flakeManager
        if (!flakeManager.onBackPressed()) {
            super.onBackPressed()
        }
    }
}