package org.yanex.hikari.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.EditText
import org.jetbrains.anko.*
import org.yanex.hikari.R
import org.yanex.hikari.flakes.DevicesFlake
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

fun AnkoContext<*>.textInputBox(
        defaultValue: String?,
        title: String? = null,
        hint: String? = null,
        init: (AlertDialogBuilder.() -> Unit)? = null,
        onNewValue: (String) -> Unit
) {
    alert {
        title?.let { this.title(it) }

        customView {
            frameLayout {
                padding = dip(8)

                editText {
                    id = android.R.id.text1

                    hint?.let { this.hint = it }

                    defaultValue?.let {
                        setText(it)
                        setSelection(it.length)
                    }
                }
            }
        }

        okButton {
            val text = dialog?.findViewById(android.R.id.text1) as? EditText
            if (text != null) {
                onNewValue(text.text.toString())
            }
            dismiss()
        }

        cancelButton()

        init?.let { it() }
    }.show()
}