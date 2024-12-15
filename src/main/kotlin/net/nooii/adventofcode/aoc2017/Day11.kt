package net.nooii.adventofcode.aoc2017

import net.nooii.adventofcode.helpers.*
import kotlin.math.max

class Day11 {

    private enum class HexDirection(val symbol: String, val dx: Int, val dy: Int) {
        NORTH("n", -1, 0),
        NORTH_EAST("ne", -1, 1),
        NORTH_WEST("nw", 0, -1),
        SOUTH("s", 1, 0),
        SOUTH_EAST("se", 0, 1),
        SOUTH_WEST("sw", 1, -1);

        fun next(point: Point): Point {
            return Point(point.x + dx, point.y + dy)
        }

        companion object {

            fun fromSymbol(symbol: String) = entries.find { it.symbol == symbol }!!
        }
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2017).loadStrings("Day11Input")
            val directions = processInput(input)
            part1(directions)
            // Runtime ~ 41 sec
            part2(directions)
        }

        private fun part1(directions: List<HexDirection>) {
            val destination = findDestination(directions)
            val steps = findShortestPath(Point(0, 0), destination)
            println(steps)
        }

        private fun part2(directions: List<HexDirection>) {
            val start = Point(0, 0)
            var position = start
            var maxDistance = 0L
            for (direction in directions) {
                position = direction.next(position)
                maxDistance = max(maxDistance, findShortestPath(start, position))
            }
            println(maxDistance)
        }

        private fun findDestination(directions: List<HexDirection>): Point {
            var position = Point(0, 0)
            for (direction in directions) {
                position = direction.next(position)
            }
            return position
        }

        private fun findShortestPath(start: Point, end: Point): Long {
            val result = traverse(
                start = start,
                traverseMode = TraverseMode.ToEnd { it == end },
                heuristic = { it.manhattanDistance(end).toLong() },
                nextItems = { current ->
                    HexDirection.entries
                        .map { ItemWithCost(it.next(current)) }
                }
            )
            return result?.cost!!
        }

        private fun processInput(input: List<String>): List<HexDirection> {
            return input.first().split(",").map { HexDirection.fromSymbol(it) }
        }
    }
}