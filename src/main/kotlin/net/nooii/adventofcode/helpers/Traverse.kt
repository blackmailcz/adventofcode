package net.nooii.adventofcode.helpers

import java.util.*

/**
 * Represents an item with an associated cost.
 *
 * @param T The type of the item, which must be a non-nullable type.
 * @property item The item being represented.
 * @property cost The cost associated with the item. Defaults to 1 if not specified.
 */
data class ItemWithCost<T : Any>(
    val item: T,
    val cost: Long = 1
)

/**
 * Represents the result of a traversal operation.
 *
 * @param T The type of elements in the path, which must be a non-nullable type.
 * @property path A list representing the traversed path from start to end.
 * @property cost The total cost of the traversal.
 * @property visitedCount The number of nodes visited during the traversal.
 */
data class TraverseResult<T : Any>(
    val path: List<T>,
    val cost: Long,
    val visitedCount: Int
)

/**
 * Defines the mode of traversal for graph-like structures.
 *
 * @param T The type of elements being traversed.
 */
sealed interface TraverseMode<T> {
    /**
     * Represents a traversal mode that continues until an end condition is met.
     *
     * @param T The type of elements being traversed.
     * @property isEnd A function that determines if the current element is the end of the traversal.
     */
    class ToEnd<T : Any>(val isEnd: (T) -> Boolean) : TraverseMode<T>

    /**
     * Represents a traversal mode that limits the number of steps taken.
     *
     * @param T The type of elements being traversed.
     * @property limit The maximum number of steps allowed in the traversal.
     */
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

/**
 * Performs a traversal of a graph-like structure using a priority queue-based algorithm.
 * This function can be used for pathfinding, searching, or exploring a state space.
 *
 * @param T The type of the nodes in the graph.
 * @param start The starting node for the traversal.
 * @param initialCost The initial cost associated with the start node. Defaults to 0.
 * @param costLimit The maximum allowed cost for the traversal. Defaults to Long.MAX_VALUE.
 * @param heuristic A function that estimates the cost from a given node to the goal. Defaults to a constant 0 function.
 * @param onNodeVisited A callback function invoked when a node is visited. Defaults to an empty function.
 * @param onEndFound A callback function invoked when the end node is found, providing the total cost. Defaults to an empty function.
 * @param traverseMode Specifies the mode of traversal, either [TraverseMode.ToEnd] or [TraverseMode.LimitedSteps].
 * @param nextItems A function that returns the neighboring nodes and their costs for a given node.
 * @return A [TraverseResult] containing the path, total cost, and number of visited nodes if a valid path is found, or null otherwise.
 */
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

/**
 * Reconstructs the path from the start node to the current node based on the 'cameFrom' map.
 *
 * @param T The type of the nodes in the path.
 * @param cameFrom A map representing the path taken, where each key is a node and its value is the node it came from.
 * @param current The current (end) node from which to reconstruct the path.
 * @return A list of nodes representing the path from the start to the current node, in order from start to end.
 */
private fun <T : Any> reconstructPath(cameFrom: Map<T, T?>, current: T): List<T> {
    val path = mutableListOf<T>()
    var currentNode: T? = current
    while (currentNode != null) {
        path.add(currentNode)
        currentNode = cameFrom[currentNode]
    }
    return path.reversed()
}