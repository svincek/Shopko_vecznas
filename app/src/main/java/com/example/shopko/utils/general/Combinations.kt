package com.example.shopko.utils.general

fun <T> List<T>.combinations(k: Int): List<List<T>> {
    if (k == 0) return listOf(emptyList())
    if (this.isEmpty()) return emptyList()

    val head = first()
    val tail = drop(1)

    val withHead = tail.combinations(k - 1).map { listOf(head) + it }
    val withoutHead = tail.combinations(k)

    return withHead + withoutHead
}