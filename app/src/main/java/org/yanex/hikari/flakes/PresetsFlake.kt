package org.yanex.hikari.flakes

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.TextView
import org.jetbrains.anko.*
import org.yanex.flake.Flake
import org.yanex.flake.FlakeManager
import org.yanex.hikari.list.MutableAdapter
import org.yanex.hikari.list.RecyclerViewFlakeHolder
import org.yanex.hikari.util.holder.RecyclerHolder1
import org.yanex.hikari.util.holder.createHolder

private typealias PresetsFlakeHolder = RecyclerViewFlakeHolder<PresetAdapter>
class PresetsFlake : Flake<PresetsFlakeHolder>() {
    private val presets = listOf("Evening", "Late at night", "Romantic").map(::Preset)

    override fun createHolder(manager: FlakeManager) = RecyclerViewFlakeHolder(manager) { PresetAdapter(it) }

    override fun setup(h: RecyclerViewFlakeHolder<PresetAdapter>, manager: FlakeManager) {
        h.listAdapter += presets
    }
}

class Preset(val name: String)

private typealias PresetAdapterHolder = RecyclerHolder1<Preset, TextView>
class PresetAdapter(val manager: FlakeManager) : MutableAdapter<Preset, PresetAdapterHolder>() {
    override fun init(parent: ViewGroup): PresetAdapterHolder = createHolder(parent) {
        preset, name ->

        name % textView {
            backgroundResource = android.R.drawable.list_selector_background
            textSize = 20f
            padding = dip(12)
            layoutParams = RecyclerView.LayoutParams(matchParent, wrapContent)

            onClick {
                manager.activity.toast("Preset ${preset.item.name}")
            }
        }
    }

    override fun bind(holder: PresetAdapterHolder, item: Preset) = holder.bind { name ->
        name.text = item.name
    }
}