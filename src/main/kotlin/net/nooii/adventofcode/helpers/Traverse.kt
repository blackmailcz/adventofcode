package net.nooii.adventofcode.helpers

import java.util.*

data class ItemWithCost<T : Any>(
    val item: T,
    val cost: Long = 1
)

class TraverseResult<T : Any>(
    val start: T,
    val end: T,
    val tree: Map<T, ItemWithCost<T>>
) {

    fun path(from: T = start, to: T = end): TraversePath<T> {
        return pathOrNull(from, to) ?: error("No path found")
    }

    fun pathOrNull(from: T = start, to: T = end): TraversePath<T>? {
        val cost = tree[to]?.cost ?: return null
        val path = mutableListOf<T>()
        var current = to
        while (true) {
            path.add(current)
            if (current == from) {
                break
            }
            val previous = tree.getValue(current).item
            if (previous == current) {
                break
            }
            current = previous
        }
        return TraversePath(path.reversed(), cost)
    }

    fun cost(from: T = start, to: T = end): Long {
        return pathOrNull(from, to)?.cost ?: error("No path found")
    }
}

class TraversePath<T : Any>(
    val path: List<T>,
    val cost: Long
)

fun <T : Any> traverse(
    start: T,
    initialCost: Long = 0,
    costLimit: Long = Long.MAX_VALUE,
    heuristic: (T) -> Long = { 0 },
    onNodeVisited: (T) -> Unit = {},
    isEnd: (T) -> Boolean,
    nextItems: (T) -> Collection<ItemWithCost<T>?>
): TraverseResult<T>? {
    val queue = PriorityQueue(compareBy<ItemWithCost<T>> { it.cost })
    queue.add(ItemWithCost(start, 0))
    val tree = mutableMapOf(start to ItemWithCost(start, initialCost))
    while (true) {
        val (node, costSoFar) = queue.poll() ?: return null
        onNodeVisited.invoke(node)
        if (isEnd.invoke(node)) {
            return TraverseResult(start, node, tree)
        }

        for (itemWithCost in nextItems.invoke(node)) {
            if (itemWithCost == null) {
                continue
            }
            val (next, cost) = itemWithCost
            if (next in tree) {
                continue
            }
            val nextCost = costSoFar + cost
            if (nextCost <= costLimit && nextCost <= (tree[next]?.cost ?: Long.MAX_VALUE)) {
                queue.add(ItemWithCost(next, heuristic.invoke(next) + nextCost))
                tree[next] = ItemWithCost(next, nextCost)
            }
        }
    }
}