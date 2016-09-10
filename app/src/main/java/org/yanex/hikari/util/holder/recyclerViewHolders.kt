package org.yanex.hikari.util.holder

import android.support.v7.widget.RecyclerView
import android.view.View
import org.jetbrains.anko.AnkoContext

fun <I : Any, T1 : View?> RecyclerView.Adapter<*>.createHolder(
        parent: View,
        init: AnkoContext<*>.(Reference<I>, ViewLink<T1>) -> Unit
): RecyclerHolder1<I, T1> {
    val key1 = KEYS.first()
    @Suppress("UNCHECKED_CAST")
    return with (AnkoContext.create(parent.context, false)) {
        val ref = Reference<I>()
        init(ref, key1 as ViewLink<T1>)
        RecyclerHolder1(ref, view, key1.view as T1).apply {
            key1.clear()
        }
    }
}

fun <I : Any, T1 : View?, T2 : View?> RecyclerView.Adapter<*>.createHolder(
        parent: View,
        init: AnkoContext<*>.(Reference<I>, ViewLink<T1>, ViewLink<T2>) -> Unit
): RecyclerHolder2<I, T1, T2> {
    val (key1, key2) = KEYS
    @Suppress("UNCHECKED_CAST")
    return with (AnkoContext.create(parent.context, false)) {
        val ref = Reference<I>()
        init(ref, key1 as ViewLink<T1>, key2 as ViewLink<T2>)
        RecyclerHolder2(ref, view, key1.view as T1, key2.view as T2).apply {
            key1.clear()
            key2.clear()
        }
    }
}

fun <I : Any, T1 : View?, T2 : View?, T3: View?> RecyclerView.Adapter<*>.createHolder(
        parent: View,
        init: AnkoContext<*>.(Reference<I>, ViewLink<T1>, ViewLink<T2>, ViewLink<T3>) -> Unit
): RecyclerHolder3<I, T1, T2, T3> {
    val (key1, key2, key3) = KEYS
    @Suppress("UNCHECKED_CAST")
    return with (AnkoContext.create(parent.context, false)) {
        val ref = Reference<I>()
        init(ref, key1 as ViewLink<T1>, key2 as ViewLink<T2>, key3 as ViewLink<T3>)
        RecyclerHolder3(ref, view, key1.view as T1, key2.view as T2, key3.view as T3).apply {
            key1.clear()
            key2.clear()
            key3.clear()
        }
    }
}

fun <I : Any, T1 : View?, T2 : View?, T3: View?, T4: View?> RecyclerView.Adapter<*>.createHolder(
        parent: View,
        init: AnkoContext<*>.(Reference<I>, ViewLink<T1>, ViewLink<T2>, ViewLink<T3>, ViewLink<T4>) -> Unit
): RecyclerHolder4<I, T1, T2, T3, T4> {
    val (key1, key2, key3, key4) = KEYS
    @Suppress("UNCHECKED_CAST")
    return with (AnkoContext.create(parent.context, false)) {
        val ref = Reference<I>()
        init(ref, key1 as ViewLink<T1>, key2 as ViewLink<T2>, key3 as ViewLink<T3>, key4 as ViewLink<T4>)
        RecyclerHolder4(ref, view, key1.view as T1, key2.view as T2, key3.view as T3, key4.view as T4).apply {
            key1.clear()
            key2.clear()
            key3.clear()
            key4.clear()
        }
    }
}

fun <I : Any, T1 : View?, T2 : View?, T3: View?, T4: View?, T5: View?> RecyclerView.Adapter<*>.createHolder(
        parent: View,
        init: AnkoContext<*>.(Reference<I>, ViewLink<T1>, ViewLink<T2>, ViewLink<T3>, ViewLink<T4>, ViewLink<T5>) -> Unit
): RecyclerHolder5<I, T1, T2, T3, T4, T5> {
    val (key1, key2, key3, key4, key5) = KEYS
    @Suppress("UNCHECKED_CAST")
    return with (AnkoContext.create(parent.context, false)) {
        val ref = Reference<I>()
        init(ref, key1 as ViewLink<T1>, key2 as ViewLink<T2>, key3 as ViewLink<T3>, key4 as ViewLink<T4>, key5 as ViewLink<T5>)
        RecyclerHolder5(ref, view, key1.view as T1, key2.view as T2, key3.view as T3, key4.view as T4, key5.view as T5).apply {
            key1.clear()
            key2.clear()
            key3.clear()
            key4.clear()
            key5.clear()
        }
    }
}

class Reference<I : Any> {
    lateinit var item: I
        internal set
}

interface RecyclerItemReferenceHolder<I : Any> {
    val ref: Reference<I>
}

class RecyclerHolder1<I : Any, out T1>(
        override val ref: Reference<I>,
        itemView: View,
        override val t1: T1
) : RecyclerView.ViewHolder(itemView), AnkoHolder1<T1>, RecyclerItemReferenceHolder<I>

class RecyclerHolder2<I : Any, out T1, out T2>(
        override val ref: Reference<I>,
        itemView: View,
        override val t1: T1,
        override val t2: T2
) : RecyclerView.ViewHolder(itemView), AnkoHolder2<T1, T2>, RecyclerItemReferenceHolder<I>

class RecyclerHolder3<I : Any, out T1, out T2, out T3>(
        override val ref: Reference<I>,
        itemView: View,
        override val t1: T1,
        override val t2: T2,
        override val t3: T3
) : RecyclerView.ViewHolder(itemView), AnkoHolder3<T1, T2, T3>, RecyclerItemReferenceHolder<I>

class RecyclerHolder4<I : Any, out T1, out T2, out T3, out T4>(
        override val ref: Reference<I>,
        itemView: View,
        override val t1: T1,
        override val t2: T2,
        override val t3: T3,
        override val t4: T4
) : RecyclerView.ViewHolder(itemView), AnkoHolder4<T1, T2, T3, T4>, RecyclerItemReferenceHolder<I>

class RecyclerHolder5<I : Any, out T1, out T2, out T3, out T4, out T5>(
        override val ref: Reference<I>,
        itemView: View,
        override val t1: T1,
        override val t2: T2,
        override val t3: T3,
        override val t4: T4,
        override val t5: T5
) : RecyclerView.ViewHolder(itemView), AnkoHolder5<T1, T2, T3, T4, T5>, RecyclerItemReferenceHolder<I>