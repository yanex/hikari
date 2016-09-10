package org.yanex.hikari.util.menu

import android.view.Menu
import org.yanex.hikari.util.menu.MenuItemFactory
import java.util.concurrent.atomic.AtomicInteger

abstract class MenuFactory {
    private val counter = AtomicInteger()
    fun nextItemId(): Int = counter.getAndIncrement()

    private val items = mutableListOf<MenuItemFactory>()

    inline fun menuItem(
            title: CharSequence,
            groupId: Int = Menu.NONE,
            order: Int = Menu.NONE,
            init: MenuItemFactory.() -> Unit
    ) = menuItem(title, groupId, order).apply(init)

    fun menuItem(title: CharSequence, groupId: Int = Menu.NONE, order: Int = Menu.NONE): MenuItemFactory {
        return MenuItemFactory(null, title, nextItemId(), groupId, order).apply { items += this }
    }

    inline fun menuItem(
            title: Int,
            groupId: Int = Menu.NONE,
            order: Int = Menu.NONE,
            init: MenuItemFactory.() -> Unit
    ) = menuItem(title, groupId, order).apply(init)

    fun menuItem(title: Int, groupId: Int = Menu.NONE, order: Int = Menu.NONE): MenuItemFactory {
        return MenuItemFactory(title, null, nextItemId(), groupId, order).apply { items += this }
    }

    fun init(menu: Menu): Boolean {
        items.forEach { it.addTo(menu) }
        return items.isNotEmpty()
    }
}

inline fun consume(action: () -> Unit): Boolean {
    action()
    return true
}