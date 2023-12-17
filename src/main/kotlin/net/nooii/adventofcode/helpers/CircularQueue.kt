package net.nooii.adventofcode.helpers

import java.util.LinkedList

class CircularQueue<E>(private val capacity: Int) : LinkedList<E>() {

    constructor(capacity: Int, element: E): this(capacity) {
        add(element)
    }

    override fun add(element: E): Boolean {
        if (size >= capacity) removeFirst()
        return super.add(element)
    }

    override fun addFirst(e: E) {
        if (size >= capacity) removeLast()
        super.addFirst(e)
    }

    fun copy(): CircularQueue<E> {
        val copy = CircularQueue<E>(capacity)
        copy.addAll(this)
        return copy
    }
}