package org.yanex.hikari.lamp

import android.bluetooth.*
import android.content.Context
import android.graphics.Color
import org.yanex.hikari.util.postDelayed
import java.util.*
import kotlin.properties.Delegates

data class Device(
        val modes: String,
        val name: String,
        val device: BluetoothDevice,
        val handler: DeviceManager.Handler
) {
    private companion object {
        val LIGHTMANIA_SERVICE_ID: UUID = UUID.fromString("0000FFF0-0000-1000-8000-00805F9B34FB")
        val CH_ID: UUID = UUID.fromString("0000FFF1-0000-1000-8000-00805F9B34FB")
        val CH_READ: UUID = UUID.fromString("0000FFF2-0000-1000-8000-00805F9B34FB")
    }

    var state: DeviceState? = null
        private set

    var isConnected: Boolean = false
        private set

    val isAvailable: Boolean
        get() = ch != null && chRead != null

    private var cmdCounter: Byte = 0
        get() = field++

    private var servicesDiscovered = false
    private var ch: BluetoothGattCharacteristic? = null
    private var chRead: BluetoothGattCharacteristic? = null

    private var failCount: Int by Delegates.observable(0) { p, oldValue, newValue ->
        if (newValue > 10) {
            handler.onDeviceFailedToConnect(this)
        } else {
            postDelayed(500) {
                val gatt = gatt ?: return@postDelayed
                if (isConnected) {
                    gatt.discoverServices()
                } else if (!isConnected) {
                    gatt.connect()
                }
            }
        }
    }

    private val callback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (status != 0) {
                failCount++
                return
            }

            if (newState == BluetoothProfile.STATE_CONNECTED && !servicesDiscovered) {
                gatt.discoverServices()
            }
            isConnected = newState == BluetoothProfile.STATE_CONNECTED
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status != 0) {
                failCount++
                return
            }

            servicesDiscovered = true
            val lightManiaService = gatt.getService(LIGHTMANIA_SERVICE_ID)
            if (lightManiaService != null) {
                ch = lightManiaService.getCharacteristic(CH_ID)
                chRead = lightManiaService.getCharacteristic(CH_READ)

                handler.onDeviceAvailabilityStateChanged(this@Device, true)
                updateState()
            }
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            if (status != 0) return

            if (characteristic.uuid == CH_READ) {
                val value = characteristic.value
                if (value.size < 5) return
                if (value.take(5).all { it == 0.toByte() }) return
                fun status(i: Int) = value[i].toInt() and 0xFF

                val isOn = status(0) and 0x1 == 0x1
                val brightness = ((status(1) and 0xF0) shr 4) - 2
                val temperature = (status(1) and 0xF) - 2
                val color = Color.rgb(status(2), status(3), status(4))
                val isRgb = temperature == -2

                val state = when (isRgb) {
                    true -> RGBModeDeviceState(this@Device, isOn, brightness, color)
                    false -> WhiteModeDeviceState(this@Device, isOn, brightness, temperature)
                }

                this@Device.state = state
                handler.onDeviceStateUpdated(state)
            }
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            if (status == 0) {
                updateState()
            }
        }
    }

    private var gatt: BluetoothGatt? = null

    fun connect(ctx: Context) {
        if (gatt != null) return
        gatt = device.connectGatt(ctx, false, callback)
    }

    fun execute(command: Command): Boolean {
        val gatt = gatt ?: return false
        val ch = ch ?: return false

        val cmd = command.cmd
        cmdCounter++

        if (command is CommandWithRandom) {
            cmd[command.counterByte] = cmdCounter
        }

        command.checkSumByte?.let { cmd[it] = calcCheckSum(cmd) }

        ch.value = cmd
        return gatt.writeCharacteristic(ch)
    }

    private fun calcCheckSum(arr: ByteArray): Byte {
        var b: Byte = 0
        for (i in 1..arr.size - 3) {
            b = (b + arr[i]).toByte()
        }
        return (b + 85).toByte()
    }

    fun updateState(): Boolean {
        val gatt = gatt ?: return false
        val chRead = chRead ?: return false
        return gatt.readCharacteristic(chRead)
    }

    fun disconnect() {
        handler.onDeviceAvailabilityStateChanged(this, false)
        gatt?.close()
        gatt = null
    }
}

abstract class DeviceState(val device: Device, val isOn: Boolean, val brightness: Int)
class WhiteModeDeviceState(device: Device, isOn: Boolean, brightness: Int, val temperature: Int) : DeviceState(device, isOn, brightness)
class RGBModeDeviceState(device: Device, isOn: Boolean, brightness: Int, val color: Int) : DeviceState(device, isOn, brightness)