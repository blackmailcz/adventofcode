package net.nooii.adventofcode.aoc2024

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.PointDirection
import net.nooii.adventofcode.helpers.PointMap
import java.awt.Point

class Day10 {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2024).loadStrings("Day10Input")
            val map = processInput(input)
            val trailheads = map.filter { it.value == 0 }.keys
            part1(map, trailheads)
            part2(map, trailheads)
        }

        private fun part1(map: PointMap<Int>, trailheads: Set<Point>) {
            println(trailheads.sumOf { countTrailScore(map, it) })
        }

        private fun part2(map: PointMap<Int>, trailheads: Set<Point>) {
            println(trailheads.sumOf { countDistinctTrails(map, it) })
        }

        private fun countTrailScore(map: PointMap<Int>, trailhead: Point): Int {
            var points = setOf(trailhead)
            var level = 0
            while (level < 9 && points.isNotEmpty()) {
                val nextPoints = mutableSetOf<Point>()
                for (point in points) {
                    for (direction in PointDirection.entries) {
                        val nextPoint = direction.next(point)
                        if (map.isInRange(nextPoint) && map[nextPoint] == level + 1) {
                            nextPoints.add(nextPoint)
                        }
                    }
                }
                points = nextPoints
                level++
            }
            return points.size
        }

        private fun countDistinctTrails(map: PointMap<Int>, trailhead: Point): Int {
            var trails = setOf(listOf(trailhead))
            var sum = 0
            while (trails.isNotEmpty()) {
                val nextTrails = mutableSetOf<List<Point>>()
                for (trail in trails) {
                    for (direction in PointDirection.entries) {
                        val point = trail.last()
                        val nextPoint = direction.next(point)
                        if (map.isInRange(nextPoint) && map[nextPoint] == map[point] + 1) {
                            val nextTrail = trail + nextPoint
                            if (nextTrail.size == 10) {
                                sum++
                            } else {
                                nextTrails.add(nextTrail)
                            }
                        }
                    }
                }
                trails = nextTrails
            }
            return sum
        }

        private fun processInput(input: List<String>): PointMap<Int> {
            return PointMap.filled(input.first().length, input.size) { x, y -> input[y][x].digitToInt() }
        }
    }
}