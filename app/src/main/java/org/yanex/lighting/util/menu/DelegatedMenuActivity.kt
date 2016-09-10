package org.yanex.lighting.util.menu

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

abstract class DelegatedMenuActivity : AppCompatActivity() {
    protected lateinit var menuManager: MenuManager
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        menuManager = MenuManager(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        menuManager.currentFactory?.let { it.init(menu) }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        menuManager.menuListener?.let { if (it(item)) return true }
        return super.onOptionsItemSelected(item)
    }
}