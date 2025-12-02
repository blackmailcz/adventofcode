package net.nooii.adventofcode.aoc2024

import net.nooii.adventofcode.helpers.*

object Day16 {

    private data class Area(
        val walls: Set<Point>,
        val start: Point,
        val end: Point
    )

    private data class Node(
        val point: Point,
        val direction: PointDirection
    )

    private data class State(
        val path: List<Node>,
        val cost: Long
    )

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2024).loadStrings("Day16Input")
        val area = processMap(input)
        part1(area)
        // Runtime ~ 6.5 seconds
        part2(area)
    }

    private fun part1(area: Area) {
        val result = findShortestPath(area)
        println(result.cost)
    }

    private fun part2(area: Area) {
        // Repeat solution from part 1, but this time use the shortest path data
        val result = findShortestPath(area)
        val shortestPath = result.path
        val shortestPathSet = result.path.toSet() // For querying in constant time
        // Build a cost map from the shortest path
        val costMap = createCostMapFromShortestPath(shortestPath)
        // Walk the map, explore all alternative turns.
        val targetPoints = shortestPathSet.map { it.point }.toMutableSet() // Having this prefilled cuts 1 second
        var states = setOf(State(listOf(Node(area.start, PointDirection.RIGHT)), 0L))
        while (states.isNotEmpty()) {
            val nextStates = mutableSetOf<State>()
            for (state in states) {
                for (nextState in getNextStates(state, area)) {
                    // Check all next states before adding them to the queue
                    val tail = nextState.path.last()
                    if (tail in costMap && nextState.cost <= costMap[tail]!!) {
                        // Node was already explored, but this time with the same or cheaper cost.
                        costMap[tail] = nextState.cost // Update the cost
                        if (tail in shortestPathSet) {
                            // Alternative path merged with the shortest path, collect its points
                            targetPoints.addAll(state.path.map { it.point })
                        }
                        // Add it for exploration
                        nextStates.add(nextState)
                    } else if (tail !in costMap) {
                        // Node was not explored yet, mark the cost and add it for exploration
                        costMap[tail] = nextState.cost
                        nextStates.add(nextState)
                    }
                }
            }
            states = nextStates
        }
        println(targetPoints.size)
    }

    /**
     * Creates a cost map from the shortest path.
     *
     * This function takes a list of nodes representing the shortest path and generates a map
     * where each node is associated with its cumulative cost from the start of the path.
     *
     * @param path A list of [Node] objects representing the shortest path.
     * @return A [MutableMap] where each key is a [Node] from the path and the value is the
     *         cumulative cost (as a [Long]) to reach that node from the start of the path.
     */
    private fun createCostMapFromShortestPath(path: List<Node>): MutableMap<Node, Long> {
        var totalCost = 0L
        val costMap = mutableMapOf(path.first() to 0L)
        path.windowed(2, 1) { (current, next) ->
            totalCost += when {
                current.direction == next.direction -> 1
                else -> 1000
            }
            costMap[next] = totalCost
        }
        return costMap
    }

    /**
     * Finds the shortest path from the start to the end point in the given area.
     *
     * This function uses an A* search algorithm to find the optimal path through the area.
     * It considers the Manhattan distance as a heuristic and uses custom logic for generating next nodes.
     *
     * @param area The Area object containing information about the map, including start and end points.
     * @return A TraverseResult<Node> object representing the shortest path found. The result includes
     *         the path of nodes and the total cost.
     */
    private fun findShortestPath(area: Area): TraverseResult<Node> {
        return traverse(
            start = Node(area.start, PointDirection.RIGHT),
            heuristic = { it.point.manhattanDistance(area.end).toLong() },
            traverseMode = TraverseMode.ToEnd { it.point == area.end },
            nextItems = { current ->
                getNextNodes(current, area).map { (node, cost) -> ItemWithCost(node, cost) }
            }
        ) ?: error("No path found from start to end")
    }

    /**
     * Generates the next possible states from the current state.
     *
     * This function calculates the next possible states by considering the last node in the current state's path.
     * If the last node is at the end point, no further states are possible. Otherwise, it generates new states
     * based on the possible next nodes and their associated costs.
     *
     * @param state The current state containing the path and accumulated cost.
     * @param area The area object containing information about the map, including the end point.
     * @return A set of new states representing possible next moves. Returns an empty set if the current state
     *         has reached the end point.
     */
    private fun getNextStates(state: State, area: Area): Set<State> {
        val tail = state.path.last()
        if (tail.point == area.end) return emptySet()
        return getNextNodes(tail, area)
            .map { (node, cost) ->
                State(state.path + node, state.cost + cost)
            }
            .toSet()
    }

    /**
     * Generates the next possible nodes and their associated costs from the current node.
     *
     * This function calculates three potential next nodes:
     * 1. Moving forward in the current direction (cost: 1)
     * 2. Rotating clockwise (cost: 1000)
     * 3. Rotating counter-clockwise (cost: 1000)
     *
     * It then filters out any nodes that would result in a collision with a wall.
     *
     * @param node The current node, containing position and direction information.
     * @param area The area object containing information about the map, including walls.
     * @return A set of pairs, each containing a possible next node and its associated cost.
     */
    private fun getNextNodes(node: Node, area: Area): Set<Pair<Node, Long>> {
        return setOf(
            Node(node.direction.next(node.point), node.direction) to 1L,
            Node(node.point, node.direction.rotateCW()) to 1000L,
            Node(node.point, node.direction.rotateCCW()) to 1000L
        ).filter { it.first.point !in area.walls }.toSet()
    }

    private fun processMap(input: List<String>): Area {
        val width = input.first().length
        val height = input.size
        var start: Point? = null
        var end: Point? = null
        val walls = mutableSetOf<Point>()
        forEachPoint(xRange = 0 until width, yRange = 0 until height) { point ->
            when (input[point.y][point.x]) {
                '#' -> walls.add(point)
                'S' -> start = point
                'E' -> end = point
            }
        }
        return Area(walls, start!!, end!!)
    }
}