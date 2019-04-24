package com.uncle2000.generator.utils

inline fun <T, E> Iterable<T>.convert(action: (T) -> E): MutableList<E> {
    val list: MutableList<E> = mutableListOf()
    for (element in this) list.add(action(element))
    return list
}
