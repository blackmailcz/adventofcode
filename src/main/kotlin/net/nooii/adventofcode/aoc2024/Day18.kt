package net.nooii.adventofcode.aoc2024

import net.nooii.adventofcode.helpers.*

class Day18 {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2024).loadStrings("Day18Input")
            val points = processInput(input)
            part1(points)
            // Runtime ~ 23 seconds
            part2(points)
        }

        private fun part1(points: List<Point>) {
            println(findShortestPath(points.take(1024))!!.cost)
        }

        private fun part2(points: List<Point>) {
            var i = -1
            while (i < points.size - 1) {
                if (findShortestPath(points.take(i + 1)) == null) {
                    break
                }
                i++
            }
            val result = points[i]
            println("${result.x},${result.y}")
        }

        private fun findShortestPath(points: List<Point>): TraverseResult<Point>? {
            val start = Point(0, 0)
            val end = Point(70, 70)
            val result = traverse(
                start = start,
                traverseMode = TraverseMode.ToEnd { it == end },
                heuristic = { it.manhattanDistance(end).toLong() },
                nextItems = { current ->
                    PointDirection.entries
                        .map { it.next(current) }
                        .filter { it !in points && it.x in start.x..end.x && it.y in start.y..end.y }
                        .map { ItemWithCost(it) }
                }
            )
            return result
        }

        private fun processInput(input: List<String>): List<Point> {
            return input.map { line ->
                val (x, y) = line.split(",").map { it.toInt() }
                Point(x, y)
            }
        }
    }
}