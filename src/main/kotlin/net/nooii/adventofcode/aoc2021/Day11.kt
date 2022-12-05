package net.nooii.adventofcode.aoc2021

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.NonNullMap
import java.awt.Point

/**
 * Created by Nooii on 11.12.2021
 */
class Day11 {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2021).loadStrings("Day11Input")
            part1(processInput(input))
            part2(processInput(input))
        }

        private fun part1(map: NonNullMap<Point, Int>) {
            println(IntRange(1, 100).sumOf { processStep(map) })
        }

        private fun part2(map: NonNullMap<Point, Int>) {
            var steps = 0
            do {
                steps++
            } while (processStep(map) != map.size)
            println(steps)
        }

        private fun processStep(map: NonNullMap<Point, Int>): Int {
            val flashed = mutableSetOf<Point>()
            for (point in map.keys) {
                inc(point, map, flashed)
            }
            return flashed.size
        }

        private fun flash(point: Point, map: NonNullMap<Point, Int>, flashed: MutableSet<Point>) {
            map[point] = 0
            flashed.add(point)
            for (dy in -1..1) {
                for (dx in -1..1) {
                    if (dx != 0 || dy != 0) {
                        inc(Point(point.x + dx, point.y + dy), map, flashed)
                    }
                }
            }
        }

        private fun inc(point: Point, map: NonNullMap<Point, Int>, flashed: MutableSet<Point>) {
            if (!map.contains(point) || flashed.contains(point)) {
                return
            }
            map[point]++
            if (map[point] > 9) {
                flash(point, map, flashed)
            }
        }

        private fun processInput(input: List<String>): NonNullMap<Point, Int> {
            val pointMap = NonNullMap<Point, Int>()
            for ((y, line) in input.withIndex()) {
                for ((x, char) in line.withIndex()) {
                    pointMap[Point(x, y)] = char.digitToInt()
                }
            }
            return pointMap
        }

    }

}