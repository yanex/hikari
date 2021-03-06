package org.yanex.hikari.util.menu

import android.graphics.drawable.Drawable
import android.view.Menu
import android.view.MenuItem

class MenuItemFactory internal constructor(
        private val titleResource: Int?,
        private val titleText: CharSequence?,
        val itemId: Int,
        val groupId: Int,
        private val order: Int
) {
    var iconResource: Int? = null
    var iconDrawable: Drawable? = null
    var showAsAction: Int? = null
    var checkable: Boolean? = null
    var checked: Boolean? = null
    var enabled: Boolean? = null
    var visible: Boolean? = null

    fun icon(resource: Int): MenuItemFactory {
        this.iconResource = resource
        return this
    }

    fun icon(drawable: Drawable): MenuItemFactory {
        this.iconDrawable = drawable
        return this
    }

    fun showAsAction(): MenuItemFactory {
        this.showAsAction = MenuItem.SHOW_AS_ACTION_ALWAYS
        return this
    }

    fun showAsActionIfRoom(): MenuItemFactory {
        this.showAsAction = MenuItem.SHOW_AS_ACTION_IF_ROOM
        return this
    }

    fun showAsActionWithText(): MenuItemFactory {
        this.showAsAction = MenuItem.SHOW_AS_ACTION_WITH_TEXT
        return this
    }

    internal fun addTo(menu: Menu): MenuItem {
        val iconResource = iconResource
        val iconDrawable = iconDrawable
        assert(iconResource == null || iconDrawable == null) {
            "Both 'iconResource' and 'iconDrawable' should not be set"
        }

        val showAsAction = showAsAction

        val checkable = checkable
        val checked = checked
        val enabled = enabled
        val visible = visible

        val menuItem = if (titleResource != null) {
            menu.add(groupId, itemId, order, titleResource)
        } else if (titleText != null) {
            menu.add(groupId, itemId, order, titleText)
        } else error("titleResource or titleText should not be null")

        if (iconResource != null)
            menuItem.setIcon(iconResource)

        if (iconDrawable != null)
            menuItem.setIcon(iconDrawable)

        if (showAsAction != null)
            menuItem.setShowAsAction(showAsAction)

        if (checkable != null)
            menuItem.setCheckable(checkable)

        if (checked != null)
            menuItem.setChecked(checked)

        if (enabled != null)
            menuItem.setEnabled(enabled)

        if (visible != null)
            menuItem.setVisible(visible)

        return menuItem
    }
}