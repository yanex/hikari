package org.yanex.hikari.list

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import org.yanex.hikari.util.holder.RecyclerItemReferenceHolder

abstract class MutableAdapter<T : Any, VH> : RecyclerView.Adapter<VH>()
        where VH : RecyclerView.ViewHolder, VH : RecyclerItemReferenceHolder<T>
{
    private val items = mutableListOf<T>()

    override fun getItemCount() = items.size

    fun setItems(newItems: List<T>) {
        items.clear()
        plusAssign(newItems)
    }

    operator fun plusAssign(newItem: T) {
        items += newItem
        notifyDataSetChanged()
    }

    operator fun plusAssign(newItems: List<T>) {
        items += newItems
        notifyDataSetChanged()
    }

    abstract fun init(parent: ViewGroup): VH
    abstract fun bind(holder: VH, item: T)

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = init(parent)

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.ref.item = item
        bind(holder, item)
    }
}