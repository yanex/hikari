package org.yanex.hikari.flakes

import android.support.v4.view.ViewCompat
import android.view.MenuItem
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.design.onTabSelectedListener
import org.jetbrains.anko.design.tabLayout
import org.jetbrains.anko.verticalLayout
import org.yanex.flake.Flake
import org.yanex.flake.FlakeManager
import org.yanex.hikari.FlakeLayoutIds
import org.yanex.hikari.R
import org.yanex.hikari.flakeLayout
import org.yanex.hikari.lamp.Commands
import org.yanex.hikari.lamp.Device
import org.yanex.hikari.toolbar
import org.yanex.hikari.util.AnkoFlakeHolder
import org.yanex.hikari.util.make
import org.yanex.hikari.util.menu.FlakeWithMenu
import org.yanex.hikari.util.menu.MenuFactory
import org.yanex.hikari.util.menu.consume

class DeviceFlake(
        val device: Device,
        val deviceName: String
) : Flake<DeviceFlake.Holder>(), FlakeWithMenu<DeviceFlake.Holder> {
    private object DeviceMenu : MenuFactory() {
        val MENU_ID_ONOFF = menuItem(R.string.device_on_off).icon(R.drawable.ic_power_settings_new_black_24dp).showAsAction()
    }

    override val menuFactory: MenuFactory?
        get() = DeviceMenu

    override fun createHolder(manager: FlakeManager) = Holder(manager)

    override fun setup(h: Holder, manager: FlakeManager) {
        super<FlakeWithMenu>.setup(h, manager)
        setToolbarTitle(manager)
        updateOnOffIcon(h)
    }

    override fun update(h: Holder, manager: FlakeManager, result: Any?) {
        device.updateState()
    }

    private fun setToolbarTitle(manager: FlakeManager) {
        manager.flakeContext.toolbar.title = deviceName
    }

    private fun updateOnOffIcon(h: Holder, state: Boolean? = null) {
        ViewCompat.postOnAnimation(h.root) {
            val isOn = state ?: device.state?.isOn ?: false
            val icon = if (isOn) R.drawable.ic_power_settings_new_white_24dp else R.drawable.ic_power_settings_new_black_24dp
            h.owner.flakeContext.toolbar.menu.findItem(DeviceMenu.MENU_ID_ONOFF.itemId)?.setIcon(icon)
        }
    }

    override fun onMenuItemSelected(h: Holder, manager: FlakeManager, item: MenuItem) = when (item.itemId) {
        DeviceMenu.MENU_ID_ONOFF.itemId -> consume {
            val newState = device.state?.isOn?.not() ?: true
            device.execute(Commands.onOff(newState))
            updateOnOffIcon(h, newState)
        }
        else -> false
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
                    id = FlakeLayoutIds.DEVICE_FLAKE_LAYOUT_ID
                    val manager = FlakeManager.create(this, ctx.owner.flakeContext)
                    // todo: save selected tab on configuration change
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