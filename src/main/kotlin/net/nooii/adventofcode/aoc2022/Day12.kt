package net.nooii.adventofcode.aoc2022

import net.nooii.adventofcode.helpers.*

class Day12 {

    private class Area(
        val map: NNMap<Point, Int>,
        val start: Point,
        val end: Point
    )

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2022).loadStrings("Day12Input")
            val area = parseInput(input)
            part1(area)
            part2(area)
        }

        private fun part1(area: Area) {
            val distance = shortestPath(area.start, area)
            println(distance)
        }

        private fun part2(area: Area) {
            val distances = area.map.filterValues { it == 0 }.keys.mapNotNull { shortestPath(it, area) }
            println(distances.minOrNull())
        }

        private fun shortestPath(start: Point, area: Area): Int? {
            val result = traverse(
                start = start,
                traverseMode = TraverseMode.ToEnd { item -> item == area.end },
                nextItems = { current ->
                    PointDirection.entries
                        .map { it.next(current) }
                        .filter { isValidMove(area, current, it) }
                        .map { ItemWithCost(it) }
                }
            )
            return result?.cost?.toInt()
        }

        private fun isValidMove(area: Area, from: Point, to: Point): Boolean {
            return area.map.contains(to) && area.map[from] >= area.map[to] - 1
        }

        private fun parseInput(input: List<String>): Area {
            val map = mutableNNMapOf<Point, Int>()
            var start: Point? = null
            var end: Point? = null
            for ((y, line) in input.withIndex()) {
                for ((x, char) in line.withIndex()) {
                    val point = Point(x, y)
                    val elevation = when (char) {
                        'S' -> 'a'.also { start = point }
                        'E' -> 'z'.also { end = point }
                        else -> char
                    }
                    map[point] = elevation.code - 'a'.code
                }
            }
            return Area(
                map,
                start ?: error("No start"),
                end ?: error("No end")
            )
        }
    }
}