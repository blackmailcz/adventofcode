package net.nooii.adventofcode.aoc2022

import net.nooii.adventofcode.helpers.*
import kotlin.math.max
import kotlin.math.min

class Day18 {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2022).loadStrings("Day18Input")
            val points = parseInput(input)
            part1(points)
            part2(points)
        }

        private fun part1(points: Set<Point3D>) {
            val touches = mutableMapOf<Point3D, Int>()
            for (point in points) {
                touches[point] = 0
                for (adjacentPoint in point.getAdjacentSidePoints()) {
                    if (adjacentPoint in points) {
                        touches.add(point, 1)
                    }
                }
            }
            val untouched = touches.values.sumOf { 6 - it }
            println(untouched)
        }

        private fun part2(lavaPoints: Set<Point3D>) {
            val min = lavaPoints.minOf { min(min(it.x, it.y), it.z) } - 1
            val max = lavaPoints.maxOf { max(max(it.x, it.y), it.z) } + 1
            val touches = mutableMapOf<Point3D, Int>()
            val visited = mutableSetOf<Point3D>()
            var waterPoints = mutableSetOf(
                Point3D(min, min, min)
            )
            while (waterPoints.isNotEmpty()) {
                val nextWaterPoints = mutableSetOf<Point3D>()
                for (waterPoint in waterPoints) {
                    if (waterPoint in visited) {
                        continue
                    }
                    visited.add(waterPoint)
                    for (adjacentPoint in waterPoint.getAdjacentSidePoints()) {
                        if (adjacentPoint.x in min..max && adjacentPoint.y in min..max && adjacentPoint.z in min..max) {
                            if (adjacentPoint in lavaPoints) {
                                touches.add(adjacentPoint, 1)
                            } else {
                                nextWaterPoints.add(adjacentPoint)
                            }
                        }
                    }
                }
                waterPoints = nextWaterPoints
            }
            val touchSum = touches.values.sum()
            println(touchSum)
        }

        private fun parseInput(input: List<String>): Set<Point3D> {
            return input.map { line ->
                val (x, y, z) = line.splitToTriple(",") { it.toInt() }
                Point3D(x, y, z)
            }.toSet()
        }
    }
}