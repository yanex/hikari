package org.yanex.lighting.flakes

import android.widget.SeekBar
import org.jetbrains.anko.*
import org.yanex.flake.Flake
import org.yanex.flake.FlakeHolder
import org.yanex.flake.FlakeLayout
import org.yanex.flake.FlakeManager
import org.yanex.lighting.lamp.Commands
import org.yanex.lighting.lamp.Device
import org.yanex.lighting.lamp.DeviceState
import org.yanex.lighting.lamp.RGBModeDeviceState
import org.yanex.lighting.util.AnkoFlakeHolder
import org.yanex.lighting.util.make

abstract class LampModeFlake<T : FlakeHolder> : Flake<T>() {
    fun AnkoContext<FlakeManager>.sendCommand(msg: Any) = owner.flakeContext.sendMessage<DeviceFlake>(msg)
}

class CustomFlake(val device: Device) : Flake<CustomFlake.Holder>() {
    private companion object {
        val ID_WHITE_MODE = 1000
        val ID_RGB_MODE = 1001
    }

    override fun messageReceived(h: Holder, manager: FlakeManager, message: Any) {
        if (message is DeviceState && message.device === device) {
            h.brightness.progress = message.brightness
        }
    }

    override fun createHolder(manager: FlakeManager) = Holder(manager)

    inner class Holder(manager: FlakeManager) : AnkoFlakeHolder(manager) {
        lateinit var brightness: SeekBar private set

        override fun initView(ctx: AnkoContext<FlakeManager>) = ctx.make {
            verticalLayout {
                padding = dip(8)

                textView("Brightness") {
                    textSize = 18f
                }

                brightness = seekBar {
                    max = 9
                    device.state?.brightness?.let { progress = it }

                    onSeekBarChangeListener {
                        onStopTrackingTouch {
                            ctx.owner.flakeContext.sendMessage<DeviceFlake>(DeviceFlake.UpdateBrightness(progress))
                        }
                    }
                }.lparams(width = matchParent) { bottomMargin = dip(8) }

                val flakeLayout = FlakeLayout(ctx.owner.activity)
                val flakeManager = FlakeManager.create(flakeLayout, ctx.owner.flakeContext)

                val grp = radioGroup {
                    radioButton {
                        id = ID_WHITE_MODE
                        text = "White mode"
                        isChecked = true
                    }
                    radioButton {
                        id = ID_RGB_MODE
                        text = "RGB mode"
                    }

                    onCheckedChange { radioGroup, i -> when (i) {
                        ID_WHITE_MODE -> {
                            flakeManager.show(WhiteModeFlake(device))
                            device.execute(Commands.setWhiteTemperature(0))
                        }
                        ID_RGB_MODE -> flakeManager.show(RGBModeFlake(device))
                    }}
                }.lparams { bottomMargin = dip(8) }

                addView(flakeLayout)

                when (device.state) {
                    is RGBModeDeviceState -> {
                        grp.check(ID_RGB_MODE)
                        flakeManager.show(RGBModeFlake(device))
                    }
                    else -> {
                        grp.check(ID_WHITE_MODE)
                        flakeManager.show(WhiteModeFlake(device))
                    }
                }
            }
        }
    }
}