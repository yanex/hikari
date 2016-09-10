package org.yanex.hikari.list

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.yanex.flake.FlakeManager
import org.yanex.hikari.util.AnkoFlakeHolder
import org.yanex.hikari.util.make

class RecyclerViewFlakeHolder<out T : RecyclerView.Adapter<*>>(
        manager: FlakeManager,
        private val fixedSize: Boolean = true,
        adapterFactory: (FlakeManager) -> T
) : AnkoFlakeHolder(manager) {
    val listAdapter = adapterFactory(manager)
    lateinit var list: RecyclerView private set

    /*
     *  init() is called from the AnkoFlakeHolder constructor,
     *  and we have not properties properly set yet.
     */
    override fun init() {}

    init {
        super.init()
    }

    override fun initView(ctx: AnkoContext<FlakeManager>) = ctx.make {
        list = recyclerView {
            if (fixedSize) hasFixedSize()
            layoutManager = LinearLayoutManager(ctx.ctx)
            adapter = listAdapter
        }
    }
}