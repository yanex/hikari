package org.yanex.hikari.flakes

import android.graphics.Color
import com.github.danielnilsson9.colorpickerview.view.ColorPickerView
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.frameLayout
import org.yanex.flake.FlakeManager
import org.yanex.hikari.lamp.Device
import org.yanex.hikari.lamp.RGBModeDeviceState
import org.yanex.hikari.util.AnkoFlakeHolder
import org.yanex.hikari.util.make

class RGBModeFlake(val device: Device) : LampModeFlake<RGBModeFlake.Holder>() {
    override fun createHolder(manager: FlakeManager) = Holder(manager)

    inner class Holder(manager: FlakeManager) : AnkoFlakeHolder(manager) {
        override fun initView(ctx: AnkoContext<FlakeManager>) = ctx.make {
            frameLayout {
                val colorPicker = ColorPickerView(ctx.owner.activity).apply {
                    val color = (device.state as? RGBModeDeviceState)?.color ?: Color.RED
                    this.color = color

                    setOnColorChangedListener { color ->
                        ctx.sendCommand(DeviceFlake.SetRGBColor(color))
                    }
                }
                addView(colorPicker)
            }
        }
    }
}