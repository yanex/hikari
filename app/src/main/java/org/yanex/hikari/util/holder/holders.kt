package org.yanex.hikari.util.holder

interface AnkoHolder1<out T1> {
    val t1: T1

    fun bind(f: (T1) -> Unit) {
        f(t1)
    }
}

interface AnkoHolder2<out T1, out T2> {
    val t1: T1
    val t2: T2

    fun bind(f: (T1, T2) -> Unit) {
        f(t1, t2)
    }
}

interface AnkoHolder3<out T1, out T2, out T3> {
    val t1: T1
    val t2: T2
    val t3: T3

    fun bind(f: (T1, T2, T3) -> Unit) {
        f(t1, t2, t3)
    }
}

interface AnkoHolder4<out T1, out T2, out T3, out T4> {
    val t1: T1
    val t2: T2
    val t3: T3
    val t4: T4

    fun bind(f: (T1, T2, T3, T4) -> Unit) {
        f(t1, t2, t3, t4)
    }
}

interface AnkoHolder5<out T1, out T2, out T3, out T4, out T5> {
    val t1: T1
    val t2: T2
    val t3: T3
    val t4: T4
    val t5: T5

    fun bind(f: (T1, T2, T3, T4, T5) -> Unit) {
        f(t1, t2, t3, t4, t5)
    }
}