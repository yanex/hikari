package org.yanex.lighting.util

import android.content.Context
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.AnkoAsyncContext
import org.jetbrains.anko.AnkoContext
import org.yanex.flake.FlakeHolder
import org.yanex.flake.FlakeManager

abstract class AnkoFlakeHolder(manager: FlakeManager): FlakeHolder, AnkoContext<FlakeManager> {
    protected lateinit var ankoContext: AnkoContext<FlakeManager>
        private set

    final override lateinit var root: View
        private set

    init {
        val ctx = AnkoContext.create(manager.activity, manager)
        ankoContext = ctx
        init()
    }

    protected open fun init() = with (ankoContext) {
        initView(this)
        root = view
    }

    abstract fun initView(ctx: AnkoContext<FlakeManager>)

    override val ctx: Context
        get() = ankoContext.ctx

    override val owner: FlakeManager
        get() = ankoContext.owner

    override val view: View
        get() = ankoContext.view

    override fun addView(view: View?, params: ViewGroup.LayoutParams?) = TODO()
}

inline fun <T> AnkoContext<T>.make(ui: AnkoContext<T>.() -> Unit): Unit = ui()

fun <A : AnkoFlakeHolder, T : AnkoAsyncContext<A>> T.activityUiThread(f: (A) -> Unit) {
    val h = weakRef.get() ?: return
    val flakeManager = h.owner
    val activity = flakeManager.activity
    if (activity.isFinishing) return
    activity.runOnUiThread { f(h) }
}