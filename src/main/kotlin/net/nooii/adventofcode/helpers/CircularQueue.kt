package net.nooii.adventofcode.helpers

import java.util.LinkedList

/**
 * Represents a circular queue with a fixed capacity.
 *
 * This queue maintains a maximum number of elements. When the capacity is reached,
 * adding a new element removes the oldest element to make space.
 *
 * @param E The type of elements held in this collection.
 * @property capacity The maximum number of elements that can be stored in the queue.
 */
class CircularQueue<E>(private val capacity: Int) : LinkedList<E>() {

    /**
     * Constructs a CircularQueue with the specified capacity and initial element.
     *
     * @param capacity The maximum number of elements that can be stored in the queue.
     * @param element The initial element to be added to the queue.
     */
    constructor(capacity: Int, element: E): this(capacity) {
        add(element)
    }

    /**
     * Adds the specified element to the end of this queue.
     *
     * If the queue is at capacity, the first (oldest) element is removed before adding the new element.
     *
     * @param element The element to add.
     * @return `true` if the element was added successfully, `false` otherwise.
     */
    override fun add(element: E): Boolean {
        if (size >= capacity) removeFirst()
        return super.add(element)
    }

    /**
     * Inserts the specified element at the beginning of this queue.
     *
     * If the queue is at capacity, the last element is removed before adding the new element.
     *
     * @param e The element to add.
     */
    override fun addFirst(e: E) {
        if (size >= capacity) removeLast()
        super.addFirst(e)
    }

    /**
     * Creates and returns a copy of this CircularQueue.
     *
     * The new CircularQueue has the same capacity and contains all elements from this queue.
     *
     * @return A new CircularQueue with the same elements and capacity as this queue.
     */
    fun copy(): CircularQueue<E> {
        val copy = CircularQueue<E>(capacity)
        copy.addAll(this)
        return copy
    }
}