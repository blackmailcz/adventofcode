package net.nooii.adventofcode.helpers

import java.util.PriorityQueue

/**
 * Universal Dijkstra algorithm.
 *
 * @param start Starting item. Its distance is 0.
 * @param isEnd Return true if the algorithm should stop. Total distance and current item are provided.
 * @param itemDistance Evaluate distance for given item.
 * @param nextItems Return collection of next valid items.
 */
class Dijkstra<I : Any>(
    private val start: I,
    private val isEnd: (totalDistance: Int, item: I) -> Boolean,
    private val itemDistance: (I) -> Int,
    private val nextItems: (I) -> Collection<I>
) {

    private class Node<I : Any>(
        val item: I,
        val distance: Int,
        val prev: Node<I>?
    ) : Comparable<Node<I>> {

        override fun compareTo(other: Node<I>) = distance.compareTo(other.distance)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Node<*>) return false

            if (item != other.item) return false

            return true
        }

        override fun hashCode(): Int {
            return item.hashCode()
        }
    }

    private class DijkstraResult<I : Any>(
        val distance: Int,
        val path: List<I>
    )

    private var result: DijkstraResult<I>? = null
    private var isExecuted: Boolean = false

    private fun ensureExecuted() {
        if (!isExecuted) {
            result = execute()
            isExecuted = true
        }
    }

    private fun execute(): DijkstraResult<I>? {
        val queue = PriorityQueue<Node<I>>()
        val startNode = Node(start, 0, null)
        queue.add(startNode)
        val visited = mutableSetOf<Node<I>>()
        while (queue.isNotEmpty()) {
            val node = queue.poll()
            if (visited.contains(node)) {
                continue
            }
            visited.add(node)
            if (isEnd.invoke(node.distance, node.item)) {
                return buildResult(node)
            }
            val nextNodes = nextItems.invoke(node.item).map {
                Node(
                    item = it,
                    distance = node.distance + itemDistance.invoke(node.item),
                    prev = node
                )
            }
            queue.addAll(nextNodes)
        }
        return null
    }

    private fun buildResult(node: Node<I>): DijkstraResult<I> {
        val path = mutableListOf<I>()
        var totalDistance = 0
        var currentNode = node
        while (true) {
            path.add(currentNode.item)
            val nextNode = currentNode.prev
            if (nextNode != null) {
                // Start node is not evaluated for distance.
                totalDistance += itemDistance.invoke(currentNode.item)
                currentNode = nextNode
            } else {
                break
            }
        }
        return DijkstraResult(totalDistance, path.reversed())
    }

    fun distance(): Int? {
        ensureExecuted()
        return result?.distance
    }

    fun path(): List<I>? {
        ensureExecuted()
        return result?.path
    }
}