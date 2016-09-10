package org.yanex.lighting

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
import org.yanex.lighting.flakes.DevicesFlake
import org.yanex.lighting.lamp.Device
import org.yanex.lighting.lamp.DeviceManager
import org.yanex.lighting.lamp.DeviceState
import org.yanex.lighting.util.MenuFactory
import org.yanex.lighting.util.consume
import org.yanex.lighting.util.getSystemService

class MainActivity : AppCompatActivity() {
    private object MainMenu : MenuFactory() {
        val MENU_ID_ABOUT = menuItem("About").showAsAction()
        val MENU_ID_SYNC = menuItem("Sync").showAsAction()
    }

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
            toast("Bluetooth is not supported on this phone.")
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

    override fun onCreateOptionsMenu(menu: Menu) = MainMenu.init(menu)

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        MainMenu.MENU_ID_ABOUT.itemId -> consume {
            toast("About!")
        }
        MainMenu.MENU_ID_SYNC.itemId -> consume {
            flakeManager.flakeContext.deviceManager.resync()
        }
        else -> super.onOptionsItemSelected(item)
    }
}