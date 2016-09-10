package org.yanex.hikari.util.menu

import android.app.Activity
import android.view.MenuItem
import org.yanex.hikari.util.menu.MenuFactory

class MenuManager(val activity: Activity) {
    var currentFactory: MenuFactory? = null
        private set

    var menuListener: ((MenuItem) -> Boolean)? = null
        private set

    fun clearMenu() {
        currentFactory = null
        menuListener = null
        activity.invalidateOptionsMenu()
    }

    fun showMenu(factory: MenuFactory, listener: (MenuItem) -> Boolean) {
        currentFactory = factory
        menuListener = listener
        activity.invalidateOptionsMenu()
    }
}