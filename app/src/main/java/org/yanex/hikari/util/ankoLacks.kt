package org.yanex.hikari.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import java.util.concurrent.atomic.AtomicBoolean

private val handler by lazy(LazyThreadSafetyMode.NONE) { Handler(Looper.getMainLooper()) }

fun postDelayed(delayMillis: Long, f: () -> Unit) {
    handler.postDelayed(f, delayMillis)
}

inline fun booleanGuard(guard: AtomicBoolean, f: () -> Unit): Boolean {
    if (!guard.compareAndSet(false, true)) return false
    f()
    return true
}

inline fun <reified T : Any> Context.getSystemService(): T? = getSystemService(T::class.java)