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
            // Precompute the best distance from the every point to the end
            val costMap = computeBestCostMap(area)
            // Track the cost occurrences
            val costs = mutableMapOf<Int, Long>()
            // Iterate over every reachable point
            for ((current, currentCost) in costMap) {
                // Analyze area around current point within a radius of maxCheatSteps
                // Doing full range check like this is faster to compute than flood fill
                for (yDiff in -maxCheatSteps..maxCheatSteps) {
                    for (xDiff in -maxCheatSteps..maxCheatSteps) {
                        val cheated = Point(current.x + xDiff, current.y + yDiff)
                        val cheatSteps = current.manhattanDistance(cheated)
                        // Only consider cheats with up to maxCheatSteps that land inside the area and not on a wall
                        if (cheatSteps <= maxCheatSteps && area.isInRange(cheated) && cheated !in area.walls) {
                            val costFromCheated = costMap[cheated]
                            // End must be reachable from the cheated point
                            if (costFromCheated != null) {
                                // Compute the discount cost and track the occurrence
                                costs.add(costFromCheated - currentCost - cheatSteps, 1L)
                            }
                        }
                    }
                }
            }
            println(costs.filterKeys { it >= 100 }.values.sum())
        }

        /**
         * Calculates the best cost (shortest distance) from every point in the area to the end point.
         *
         * This function uses a breadth-first search algorithm to compute the minimum distance
         * from each accessible point in the area to the end point, considering walls as obstacles.
         *
         * @param area The Area object containing the dimensions, start point, end point, and walls.
         * @return A Map where each key is a Point in the area, and the value is the best
         *         cost (as a Long) from that point to the end point. Points that are
         *         unreachable (e.g., walled off) will not be included in the map.
         */
        private fun computeBestCostMap(area: Area): Map<Point, Int> {
            var distance = 0
            val costMap = mutableMapOf<Point, Int>()
            var points = setOf(area.end)
            while (points.isNotEmpty()) {
                val nextPoints = mutableSetOf<Point>()
                for (point in points) {
                    val bestCost = costMap[point]
                    if (bestCost == null || distance < bestCost) {
                        costMap[point] = distance
                        if (point == area.start) {
                            continue
                        }
                        for (direction in PointDirection.entries) {
                            val next = direction.next(point)
                            if (area.isInRange(next) && next !in area.walls) {
                                nextPoints.add(next)
                            }
                        }
                    }
                }
                points = nextPoints
                distance++
            }
            return costMap
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