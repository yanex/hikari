package org.yanex.hikari.flakes

import android.widget.SeekBar
import org.jetbrains.anko.*
import org.yanex.flake.FlakeManager
import org.yanex.hikari.R
import org.yanex.hikari.lamp.Device
import org.yanex.hikari.lamp.WhiteModeDeviceState
import org.yanex.hikari.util.AnkoFlakeHolder
import org.yanex.hikari.util.make

class WhiteModeFlake(val device: Device) : LampModeFlake<WhiteModeFlake.Holder>() {
    override fun createHolder(manager: FlakeManager) = Holder(manager)

    override fun messageReceived(h: WhiteModeFlake.Holder, manager: FlakeManager, message: Any) {
        if (message is WhiteModeDeviceState && message.device === device) {
            h.temperature.progress = h.temperature.max - message.temperature
        }
    }

    inner class Holder(manager: FlakeManager) : AnkoFlakeHolder(manager) {
        lateinit var temperature: SeekBar private set

        override fun initView(ctx: AnkoContext<FlakeManager>) = ctx.make {
            verticalLayout {
                textView(R.string.whitemode_temperature, theme = 0) {
                    textSize = 18f
                }

                temperature = seekBar {
                    verticalPadding = dip(8)
                    max = 9

                    val temperature = (device.state as? WhiteModeDeviceState)?.temperature ?: max
                    progress = max - temperature

                    onSeekBarChangeListener {
                        onStopTrackingTouch {
                            val value = max - progress
                            val msg = DeviceFlake.UpdateWhiteTemperature(value)
                            ctx.sendCommand(msg)
                        }
                    }
                }
            }
        }
    }
}