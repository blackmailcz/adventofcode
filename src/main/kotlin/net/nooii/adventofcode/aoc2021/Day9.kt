package net.nooii.adventofcode.aoc2021

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.Point

/**
 * Created by Nooii on 09.12.2021
 */
class Day9 {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2021).loadStrings("Day9Input")
            val map = processInput(input)
            val diffPoints = listOf(Point(0, 1), Point(0, -1), Point(-1, 0), Point(1, 0))
            val lowPoints = findLowPoints(map, diffPoints)
            part1(map, lowPoints)
            part2(map, diffPoints, lowPoints)
        }

        private fun part1(map: List<List<Int>>, lowPoints: List<Point>) {
            println(lowPoints.sumOf { map[it.y][it.x] + 1 })
        }

        private fun part2(map: List<List<Int>>, diffPoints: List<Point>, lowPoints: List<Point>) {
            val basins = mutableListOf<Set<Point>>()
            for (lowPoint in lowPoints) {
                val basin = mutableSetOf<Point>()
                var basinPoints = setOf(lowPoint)
                while (basinPoints.isNotEmpty()) {
                    basin.addAll(basinPoints)
                    val currentBasinPoints = mutableSetOf<Point>()
                    for (basinPoint in basinPoints) {
                        currentBasinPoints.addAll(findAdjacentBasinPoints(map, diffPoints, basinPoint))
                    }
                    basinPoints = currentBasinPoints
                }
                basins.add(basin)
            }
            basins.sortByDescending { it.size }
            val output = basins[0].size * basins[1].size * basins[2].size
            println(output)
        }

        private fun findLowPoints(map: List<List<Int>>, diffPoints: List<Point>): List<Point> {
            val lowPoints = mutableListOf<Point>()
            for (y in map.indices) {
                for (x in map[y].indices) {
                    if (findAdjacentLowPoint(map, diffPoints, x, y)) {
                        lowPoints.add(Point(x, y))
                    }
                }
            }
            return lowPoints
        }

        private fun findAdjacentLowPoint(map: List<List<Int>>, diffPoints: List<Point>, x: Int, y: Int): Boolean {
            return !diffPoints.any { d ->
                val point = map.getOrNull(y + d.y)?.getOrNull(x + d.x)
                point != null && map[y][x] >= point
            }
        }

        private fun findAdjacentBasinPoints(map: List<List<Int>>, diffPoints: List<Point>, lowPoint: Point): Set<Point> {
            val basinPoints = mutableSetOf<Point>()
            for (d in diffPoints) {
                val low = map[lowPoint.y][lowPoint.x]
                val basinPoint = map.getOrNull(lowPoint.y + d.y)?.getOrNull(lowPoint.x + d.x) ?: continue
                if (basinPoint != 9 && low < basinPoint) {
                    basinPoints.add(Point(lowPoint.x + d.x, lowPoint.y + d.y))
                }
            }
            return basinPoints
        }

        private fun processInput(input: List<String>): List<List<Int>> {
            return input.map { line -> line.map { it.digitToInt() } }
        }

    }
}