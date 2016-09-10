package org.yanex.lighting.lamp

import android.bluetooth.BluetoothAdapter
import android.content.Context
import org.jetbrains.anko.AnkoLogger
import org.yanex.lighting.util.booleanGuard
import org.yanex.lighting.util.postDelayed
import java.util.concurrent.atomic.AtomicBoolean

private val DEVICE_MODES = mapOf(
        "Tint B7" to "RGBCW",
        "B710" to "RGBCW",
        "B720" to "RGBCW",
        "B730" to "RGBCW",
        "Tint B7C" to "RGBC",
        "Tint B7W" to "RGRW",
        "Tint B7S" to "CW",
        "Tint B9" to "RGBCW",
        "B910" to "RGBCW",
        "B930" to "RGBCW",
        "Tint B9C" to "RGBC",
        "Tint B9W" to "RGRW",
        "Tint B9S" to "CW",
        "SML-c9" to "RGBCW",
        "SML-w7" to "CW",
        "ledergb" to "RGBCW",
        "BeeWi SmartLite" to "RGBCW",
        "Strip_RGBC" to "RGBC",
        "Strip S20" to "RGBC",
        "S1" to "RGBC",
        "Tint S2" to "RGBC",
        "K4" to "RGRW",
        "P9" to "RGRW",
        "H1" to "TIMER",
        "B530" to "RGBCW",
        "SimpleBLEPeripheral" to "TIMER")

class DeviceManager(val bluetoothAdapter: BluetoothAdapter, val ctx: Context) : AnkoLogger {
    private val mutableDevices = mutableListOf<Device>()
    val devices: List<Device>
        get() = mutableDevices

    private val discoveringNow = AtomicBoolean(false)
    private var connectAutomatically = false

    private val scanCallback = BluetoothAdapter.LeScanCallback { device, rssi, scanRecord ->
        if (mutableDevices.any { it.device.address == device.address }) return@LeScanCallback
        val modes = DEVICE_MODES[device.name] ?: return@LeScanCallback

        Device(modes, device.name, device, delegateHandler).apply {
            mutableDevices += this
            if (connectAutomatically) connect(ctx)
            delegateHandler.onNewDevice(this)
        }
    }

    fun resync() {
        delegateHandler.onResyncRequested()
        mutableDevices.forEach { it.disconnect() }
        mutableDevices.clear()
        scan(true)
    }

    fun scan(connectAutomatically: Boolean = true) {
        booleanGuard(discoveringNow) {
            if (connectAutomatically) {
                connectAll()
            }

            postDelayed(3000) {
                bluetoothAdapter.stopLeScan(scanCallback)
                discoveringNow.set(false)
            }

            bluetoothAdapter.startLeScan(scanCallback)
        }
    }

    fun connectAll() {
        connectAutomatically = true
        devices.forEach { it.connect(ctx) }
    }

    fun disconnectAll() {
        connectAutomatically = false
        devices.forEach { it.disconnect() }
    }

    private val handlers = mutableListOf<DeviceManager.Handler>()
    fun addHandler(handler: DeviceManager.Handler) { handlers += handler }
    fun removeHandler(handler: DeviceManager.Handler) { handlers -= handler }

    private val delegateHandler = object : Handler {
        override fun onResyncRequested() = handlers.forEach { it.onResyncRequested() }
        override fun onNewDevice(device: Device) = handlers.forEach { it.onNewDevice(device) }
        override fun onDeviceStateUpdated(state: DeviceState) = handlers.forEach { it.onDeviceStateUpdated(state) }

        override fun onDeviceAvailabilityStateChanged(device: Device, isAvailable: Boolean) {
            handlers.forEach { it.onDeviceAvailabilityStateChanged(device, isAvailable) }
        }

        override fun onDeviceFailedToConnect(device: Device) {
            device.disconnect()
            mutableDevices -= device
        }
    }

    interface Handler {
        fun onResyncRequested() {}
        fun onNewDevice(device: Device)
        fun onDeviceFailedToConnect(device: Device) {}
        fun onDeviceAvailabilityStateChanged(device: Device, isAvailable: Boolean)
        fun onDeviceStateUpdated(state: DeviceState)
    }
}