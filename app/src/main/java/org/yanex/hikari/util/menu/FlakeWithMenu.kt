package org.yanex.hikari.util.menu

import android.view.MenuItem
import org.yanex.flake.FlakeBase
import org.yanex.flake.FlakeHolder
import org.yanex.flake.FlakeManager
import org.yanex.hikari.menuManager

interface FlakeWithMenu<T : FlakeHolder> : FlakeBase<T> {
    override fun setup(h: T, manager: FlakeManager) {
        setMenu(h, manager)
    }

    override fun update(h: T, manager: FlakeManager, result: Any?) {
        setMenu(h, manager)
    }

    private fun setMenu(h: T, manager: FlakeManager) {
        val menuFactory = this.menuFactory
        val menuManager = manager.flakeContext.menuManager
        if (menuFactory != null) {
            menuManager.showMenu(menuFactory) { onMenuItemSelected(h, manager, it) }
        } else {
            menuManager.clearMenu()
        }
    }

    val menuFactory: MenuFactory?
        get() = null

    fun onMenuItemSelected(h: T, manager: FlakeManager, item: MenuItem): Boolean = false
}