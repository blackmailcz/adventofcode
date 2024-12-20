package net.nooii.adventofcode.aoc2024

import net.nooii.adventofcode.helpers.*

class Day20 {

    private data class Area(
        val width: Int,
        val height: Int,
        val start: Point,
        val end: Point,
        val walls: Set<Point>
    ) {
        fun isInRange(point: Point) = point.x in 0 until width && point.y in 0 until height
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2024).loadStrings("Day20Input")
            val area = processInput(input)
            // Does not work for an arbitrary maze, we are using a fact that shortest path goes through every
            // free field of the maze, otherwise it would not possible to solve it this way.
            // But I think this was intended :)
            part1(area)
            part2(area)
        }

        private fun part1(area: Area) {
            solution(area, 2)
        }

        private fun part2(area: Area) {
            solution(area, 20)
        }

        private fun solution(area: Area, maxCheatSteps: Int) {
            // Start by finding the only possible path
            val pathInfo = findShortestPath(area)
            // For each point of the path, remember distance to the end
            val distances = pathInfo.path
                .mapIndexed { index, point -> point to pathInfo.cost - index }
                .toMap()
                .nn()
            // Track the cost occurrences
            val costs = mutableMapOf<Long, Long>()
            // Iterate over every point of the path
            for ((currentCost, current) in pathInfo.path.withIndex()) {
                // Analyze area around current point within a radius of maxCheatSteps
                for (yDiff in -maxCheatSteps..maxCheatSteps) {
                    for (xDiff in -maxCheatSteps..maxCheatSteps) {
                        val cheated = Point(current.x + xDiff, current.y + yDiff)
                        val cheatSteps = current.manhattanDistance(cheated)
                        // Only consider cheats with up to maxCheatSteps that land inside the area and not on a wall
                        if (cheatSteps <= maxCheatSteps && area.isInRange(cheated) && cheated !in area.walls) {
                            // Compute the discount cost and track the occurrence
                            costs.add(pathInfo.cost - (distances[cheated] + currentCost + cheatSteps), 1L)
                        }
                    }
                }
            }
            println(costs.filterKeys { it >= 100 }.values.sum())
        }

        private fun findShortestPath(area: Area): TraverseResult<Point> {
            return traverse(
                start = area.start,
                traverseMode = TraverseMode.ToEnd { it == area.end },
                heuristic = { it.manhattanDistance(area.end).toLong() },
                nextItems = { current ->
                    PointDirection.entries
                        .map { it.next(current) }
                        .filter { area.isInRange(it) && !area.walls.contains(it) }
                        .map { ItemWithCost(it) }
                }
            ) ?: error("No path found")
        }

        private fun processInput(input: List<String>): Area {
            val width = input.first().length
            val height = input.size
            var start: Point? = null
            var end: Point? = null
            val walls = mutableSetOf<Point>()
            forEachPoint(0 until width, 0 until height) { point ->
                when (input[point.y][point.x]) {
                    'S' -> start = point
                    'E' -> end = point
                    '#' -> walls.add(point)
                }
            }
            return Area(width, height, start!!, end!!, walls)
        }
    }
}