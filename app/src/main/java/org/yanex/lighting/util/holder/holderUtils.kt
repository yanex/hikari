package org.yanex.lighting.util.holder

import android.view.View
import android.widget.RelativeLayout
import org.jetbrains.anko.above
import org.jetbrains.anko.below

internal val KEYS = (1..5).map { ViewLink<View>() }

class ViewLink<T: View?> {
    var view: T? = null
        internal set

    operator fun mod(view: T) {
        this.view = view
    }

    internal fun clear() {
        view = null
    }
}

/**
 * Place the current View above [view].
 * It is an alias for [above].
 */
inline fun RelativeLayout.LayoutParams.topOf(view: ViewLink<*>): Unit = addRule(RelativeLayout.ABOVE, view.view!!.id)

/**
 * Place the current View above [view].
 */
inline fun RelativeLayout.LayoutParams.above(view: ViewLink<*>): Unit = addRule(RelativeLayout.ABOVE, view.view!!.id)

/**
 * Place the current View below [view].
 * It is an alias for [below].
 */
inline fun RelativeLayout.LayoutParams.bottomOf(view: ViewLink<*>): Unit = addRule(RelativeLayout.BELOW, view.view!!.id)

/**
 * Place the current View below [view].
 */
inline fun RelativeLayout.LayoutParams.below(view: ViewLink<*>): Unit = addRule(RelativeLayout.BELOW, view.view!!.id)

/**
 * Place the current View to the left of [view].
 */
inline fun RelativeLayout.LayoutParams.leftOf(view: ViewLink<*>): Unit = addRule(RelativeLayout.LEFT_OF, view.view!!.id)

/**
 * Place the current View to the right of [view].
 */
inline fun RelativeLayout.LayoutParams.rightOf(view: ViewLink<*>): Unit = addRule(RelativeLayout.RIGHT_OF, view.view!!.id)

/**
 * Set the current View left attribute the same as for [view].
 */
inline fun RelativeLayout.LayoutParams.sameLeft(view: ViewLink<*>): Unit = addRule(RelativeLayout.ALIGN_LEFT, view.view!!.id)

/**
 * Set the current View top attribute the same as for [view].
 */
inline fun RelativeLayout.LayoutParams.sameTop(view: ViewLink<*>): Unit = addRule(RelativeLayout.ALIGN_TOP, view.view!!.id)

/**
 * Set the current View right attribute the same as for [view].
 */
inline fun RelativeLayout.LayoutParams.sameRight(view: ViewLink<*>): Unit = addRule(RelativeLayout.ALIGN_RIGHT, view.view!!.id)

/**
 * Set the current View bottom attribute the same as for [view].
 */
inline fun RelativeLayout.LayoutParams.sameBottom(view: ViewLink<*>): Unit = addRule(RelativeLayout.ALIGN_BOTTOM, view.view!!.id)