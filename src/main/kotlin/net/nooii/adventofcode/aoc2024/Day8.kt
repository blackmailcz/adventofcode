package net.nooii.adventofcode.aoc2024

import com.github.shiguruikai.combinatoricskt.combinations
import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.Point
import net.nooii.adventofcode.helpers.forEachPoint

class Day8 {

    private class Area(
        val width: Int,
        val height: Int,
        val frequencies: Map<Char, Set<Point>>
    ) {
        fun isInArea(point: Point): Boolean {
            return point.x in 0 until width && point.y in 0 until height
        }
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2024).loadStrings("Day8Input")
            val area = processInput(input)
            part1(area)
            part2(area)
        }

        private fun part1(area: Area) {
            solution(area, limitDistance = true)
        }

        private fun part2(area: Area) {
            solution(area, limitDistance = false)
        }

        private fun solution(area: Area, limitDistance: Boolean) {
            val uniqueLocations = mutableSetOf<Point>()
            for (points in area.frequencies.filter { it.value.size > 1 }.values) {
                for ((a, b) in points.combinations(2)) {
                    var distance = if (limitDistance) 1 else 0
                    do {
                        val point1 = getNextPoint(area, a, b, distance)?.also { uniqueLocations.add(it) }
                        val point2 = getNextPoint(area, b, a, distance)?.also { uniqueLocations.add(it) }
                        distance++
                    } while (!limitDistance && (point1 != null || point2 != null))
                }
            }
            println(uniqueLocations.size)
        }

        private fun getNextPoint(area: Area, a: Point, b: Point, distance: Int): Point? {
            return Point(a.x + (a.x - b.x) * distance, a.y + (a.y - b.y) * distance).takeIf { area.isInArea(it) }
        }

        private fun processInput(input: List<String>): Area {
            val width = input.first().length
            val height = input.size
            val map = mutableMapOf<Char, MutableSet<Point>>()
            forEachPoint(xRange = 0 until width, yRange = 0 until height) {
                val frequency = input[it.y][it.x]
                if (frequency != '.') {
                    map.computeIfAbsent(frequency) { mutableSetOf() }.add(it)
                }
            }
            return Area(width, height, map)
        }
    }
}