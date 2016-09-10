@file:Suppress("NOTHING_TO_INLINE", "UsePropertyAccessSyntax")

package org.yanex.hikari

import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewManager
import org.jetbrains.anko.custom.ankoView
import org.yanex.flake.FlakeContext
import org.yanex.flake.FlakeLayout
import org.yanex.hikari.lamp.DeviceManager
import org.yanex.hikari.util.menu.MenuManager

inline fun ViewManager.flakeLayout(theme: Int = 0): FlakeLayout = flakeLayout(theme) {}
inline fun ViewManager.flakeLayout(theme: Int = 0, init: FlakeLayout.() -> Unit): FlakeLayout {
    return ankoView({ FlakeLayout(it) }, theme) { init() }
}

fun <T : View> T.nextId(): T {
    id = View.generateViewId()
    return this
}

val FlakeContext.deviceManager: DeviceManager
    get() = getComponent()

val FlakeContext.menuManager: MenuManager
    get() = getComponent()