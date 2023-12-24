package net.nooii.adventofcode.helpers

import java.util.*

data class ItemWithCost<T : Any>(
    val item: T,
    val cost: Long = 1
)

data class TraverseResult<T : Any>(
    val path: List<T>,
    val cost: Long,
    val visitedCount: Int
)

sealed interface TraverseMode<T> {
    class ToEnd<T : Any>(val isEnd: (T) -> Boolean) : TraverseMode<T>
    class LimitedSteps<T : Any>(val limit: Int) : TraverseMode<T>
}

// TODO add to Traverse. Also add iterative version using stack.
fun <T: Any> greatestCost(
    start: T,
    end: T,
    visited: MutableSet<T> = mutableSetOf(),
    nextItems: (current: T) -> Collection<ItemWithCost<T>>,
): Long {
    if (start == end) {
        return 0
    }
    var maxDistance = Long.MIN_VALUE
    visited.add(start)
    for ((item, cost) in nextItems.invoke(start)) {
        if (item in visited) {
            continue
        }
        maxDistance = maxOf(maxDistance, greatestCost(item, end, visited, nextItems) + cost)
    }
    visited.remove(start)
    return maxDistance
}

fun <T : Any> traverse(
    start: T,
    initialCost: Long = 0,
    costLimit: Long = Long.MAX_VALUE,
    heuristic: (T) -> Long = { 0 },
    onNodeVisited: (T) -> Unit = {},
    onEndFound: (cost: Long) -> Unit = {},
    traverseMode: TraverseMode<T>,
    nextItems: (current: T) -> Collection<ItemWithCost<T>?>
): TraverseResult<T>? {

    val queue = PriorityQueue(compareBy<ItemWithCost<T>> { it.cost })
    val closedSet = mutableSetOf<T>()
    val cameFrom = mutableMapOf<T, T?>()
    val gScore = mutableMapOf<T, Long>()
    val steps = mutableMapOf<T, Int>()

    queue.add(ItemWithCost(start, initialCost))
    gScore[start] = initialCost
    steps[start] = 0

    while (queue.isNotEmpty()) {
        val (current, _) = queue.poll()
        if (closedSet.contains(current)) {
            continue
        }

        closedSet.add(current)
        onNodeVisited.invoke(current)

        if (traverseMode is TraverseMode.LimitedSteps && steps[current] == traverseMode.limit) {
            continue
        }

        if (traverseMode is TraverseMode.ToEnd && traverseMode.isEnd.invoke(current)) {
            onEndFound.invoke(gScore[current]!!)
            val path = reconstructPath(cameFrom, current)
            return TraverseResult(path, gScore[current]!!, closedSet.size)
        }

        for (itemWithCost in nextItems.invoke(current)) {
            if (itemWithCost == null) {
                continue
            }
            val (next, cost) = itemWithCost
            val tentativeGScore = gScore[current]!! + cost
            if (tentativeGScore < gScore.getOrDefault(next, Long.MAX_VALUE) && tentativeGScore <= costLimit) {
                gScore[next] = tentativeGScore
                cameFrom[next] = current
                steps[next] = (steps[current] ?: 0) + 1
                queue.add(ItemWithCost(next, tentativeGScore + heuristic.invoke(next)))
            }
        }
    }
    if (traverseMode is TraverseMode.LimitedSteps) {
        val lowestScoreNode = gScore.entries.minByOrNull { it.value }?.key
        return lowestScoreNode?.let { TraverseResult(reconstructPath(cameFrom, it), gScore[it] ?: 0, closedSet.size) }
    }
    return null
}

private fun <T : Any> reconstructPath(cameFrom: Map<T, T?>, current: T): List<T> {
    val path = mutableListOf<T>()
    var currentNode: T? = current
    while (currentNode != null) {
        path.add(currentNode)
        currentNode = cameFrom[currentNode]
    }
    return path.reversed()
}