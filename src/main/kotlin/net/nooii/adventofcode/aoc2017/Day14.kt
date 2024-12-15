package net.nooii.adventofcode.aoc2017

import net.nooii.adventofcode.aoc2017.cross.AoC2017_Day10_KnotHash
import net.nooii.adventofcode.helpers.*

class Day14 {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2017).loadStrings("Day14Input").first()
            val pointMap = processInput(input)
            part1(pointMap)
            part2(pointMap)
        }

        private fun part1(pointMap: PointMap<Boolean>) {
            println(pointMap.values.count { it })
        }

        private fun part2(pointMap: PointMap<Boolean>) {
            val visited = mutableSetOf<Point>()
            var groups = 0
            for (y in 0 until pointMap.height) {
                for (x in 0 until pointMap.width) {
                    val point = Point(x, y)
                    if (point in visited || !pointMap[point]) {
                        continue
                    }
                    findAdjacentPoints(point, pointMap, visited)
                    groups++
                }
            }
            println(groups)
        }

        private fun findAdjacentPoints(
            initialPoint: Point,
            pointMap: PointMap<Boolean>,
            visited: MutableSet<Point>
        ) {
            var points = setOf(initialPoint)
            while (points.isNotEmpty()) {
                val nextPoints = mutableSetOf<Point>()
                for (point in points) {
                    visited.add(point)
                    for (direction in PointDirection.entries) {
                        val nextPoint = direction.next(point)
                        if (pointMap.isInRange(nextPoint) && nextPoint !in visited && pointMap[nextPoint]) {
                            nextPoints.add(nextPoint)
                        }
                    }
                }
                points = nextPoints
            }
        }

        private fun processInput(input: String): PointMap<Boolean> {
            val knotHash = AoC2017_Day10_KnotHash()
            val pointMap = PointMap<Boolean>(128, 128)
            for (y in 0 until 128) {
                val row = knotHash.hash("$input-$y")
                    .map { hexToBin(it.toString()) }
                    .joinToString("")
                for ((x, bit) in row.withIndex()) {
                    pointMap[Point(x, y)] = bit == '1'
                }
            }
            return pointMap
        }
    }
}