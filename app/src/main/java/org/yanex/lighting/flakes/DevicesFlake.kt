package org.yanex.lighting.flakes

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView
import org.yanex.flake.Flake
import org.yanex.flake.FlakeManager
import org.yanex.lighting.deviceManager
import org.yanex.lighting.lamp.Commands
import org.yanex.lighting.lamp.Device
import org.yanex.lighting.list.MutableAdapter
import org.yanex.lighting.list.RecyclerViewFlakeHolder
import org.yanex.lighting.nextId
import org.yanex.lighting.util.holder.RecyclerHolder2
import org.yanex.lighting.util.holder.createHolder
import org.yanex.lighting.util.holder.rightOf

private typealias DevicesFlakeHolder = RecyclerViewFlakeHolder<DeviceAdapter>
class DevicesFlake : Flake<DevicesFlakeHolder>(), AnkoLogger {
    private val devices = mutableListOf<Device>()

    override val loggerTag: String
        get() = "DevicesFlake"

    override fun setup(h: DevicesFlakeHolder, manager: FlakeManager) {
        setItems(h, devices)
        updateDevices(h, manager)
    }

    override fun update(h: DevicesFlakeHolder, manager: FlakeManager, result: Any?) {
        updateDevices(h, manager)
    }

    private fun updateDevices(h: DevicesFlakeHolder, manager: FlakeManager) {
        setItems(h, manager.flakeContext.deviceManager.devices)
    }

    override fun messageReceived(h: DevicesFlakeHolder, manager: FlakeManager, message: Any) {
        when (message) {
            ResyncRequested -> setItems(h, emptyList())
            is Device -> addItem(h, message)
            is DeviceAvailabilityStatusChanged -> h.listAdapter.notifyDataSetChanged()
        }
    }

    private fun setItems(h: DevicesFlakeHolder, devices: List<Device>) {
        this.devices.clear()
        this.devices += devices
        h.listAdapter.setItems(devices)
    }

    private fun addItem(h: DevicesFlakeHolder, lamp: Device) {
        this.devices += lamp
        h.listAdapter += lamp
    }

    override fun createHolder(manager: FlakeManager) = RecyclerViewFlakeHolder(manager) { DeviceAdapter(it) }

    class DeviceAvailabilityStatusChanged(val device: Device, val isAvailable: Boolean)
    object ResyncRequested
}

private typealias DeviceAdapterHolder = RecyclerHolder2<Device, ImageView, TextView>
class DeviceAdapter(val flakeManager: FlakeManager) : MutableAdapter<Device, DeviceAdapterHolder>() {
    override fun init(parent: ViewGroup): DeviceAdapterHolder = createHolder(parent) {
        device, img, name ->

        cardView {
            radius = dip(4).toFloat()
            layoutParams = RecyclerView.LayoutParams(matchParent, dip(50)).apply {
                horizontalMargin = dip(8)
                topMargin = dip(8)
            }

            relativeLayout {
                padding = dip(8)

                img % imageView {
                    onClick {
                        val newState = device.item.state?.let { !it.isOn } ?: true
                        device.item.execute(Commands.onOff(newState))
                    }
                }.nextId().lparams(dip(40), dip(40)) { centerVertically() }

                name % textView("Lamp name") {
                    textSize = 18f
                }.lparams { rightOf(img); leftMargin = dip(8); centerVertically() }
            }

            onClick {
                if (device.item.isAvailable) {
                    flakeManager.show(DeviceFlake(device.item))
                } else {
                    toast("Device is offline.")
                }
            }
        }
    }



    override fun bind(holder: DeviceAdapterHolder, item: Device) = holder.bind {
        img, name ->

        img.imageResource = if (item.isAvailable) android.R.drawable.ic_media_next else android.R.drawable.ic_delete
        name.text = item.name
    }
}
