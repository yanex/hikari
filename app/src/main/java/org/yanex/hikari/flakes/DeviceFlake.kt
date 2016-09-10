package org.yanex.hikari.flakes

import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.design.onTabSelectedListener
import org.jetbrains.anko.design.tabLayout
import org.jetbrains.anko.verticalLayout
import org.yanex.flake.Flake
import org.yanex.flake.FlakeManager
import org.yanex.hikari.R
import org.yanex.hikari.flakeLayout
import org.yanex.hikari.lamp.Commands
import org.yanex.hikari.lamp.Device
import org.yanex.hikari.menuManager
import org.yanex.hikari.util.AnkoFlakeHolder
import org.yanex.hikari.util.make
import org.yanex.hikari.util.menu.FlakeWithMenu

class DeviceFlake(val device: Device) : Flake<DeviceFlake.Holder>(), FlakeWithMenu<DeviceFlake.Holder> {
    private companion object {
        private val ID_FLAKE_LAYOUT = 1000
    }

    override fun createHolder(manager: FlakeManager) = Holder(manager)

    override fun setup(h: Holder, manager: FlakeManager) {
        super<Flake>.setup(h, manager)
        super<FlakeWithMenu>.setup(h, manager)
    }

    override fun update(h: Holder, manager: FlakeManager, result: Any?) {
        super<FlakeWithMenu>.update(h, manager, result)
        device.updateState()
    }

    override fun messageReceived(h: Holder, manager: FlakeManager, message: Any) {
        when (message) {
            is UpdateWhiteTemperature -> device.execute(Commands.setWhiteTemperature(message.newValue))
            is UpdateBrightness -> device.execute(Commands.setBrightness(message.newValue))
            is SetRGBColor -> device.execute(Commands.rgbColor(message.color))
        }
    }

    inner class Holder(manager: FlakeManager) : AnkoFlakeHolder(manager) {
        override fun initView(ctx: AnkoContext<FlakeManager>) = ctx.make {
            verticalLayout {
                val tabLayout = tabLayout {
                    addTab(newTab().setText(R.string.device_presets))
                    addTab(newTab().setText(R.string.device_custom))
                }

                flakeLayout {
                    id = ID_FLAKE_LAYOUT
                    val manager = FlakeManager.create(this, ctx.owner.flakeContext)
                    onTabSelected(manager, 0)

                    tabLayout.onTabSelectedListener {
                        onTabSelected { tab ->
                            onTabSelected(manager, tab!!.position)
                        }
                    }
                }
            }
        }

        fun onTabSelected(flakeManager: FlakeManager, position: Int): Unit = flakeManager.replace(when (position) {
            0 -> PresetsFlake()
            1 -> CustomFlake(device)
            else -> throw IllegalArgumentException("Invalid tab position: $position")
        })
    }

    class UpdateWhiteTemperature(val newValue: Int)
    class UpdateBrightness(val newValue: Int)
    class SetRGBColor(val color: Int)
}