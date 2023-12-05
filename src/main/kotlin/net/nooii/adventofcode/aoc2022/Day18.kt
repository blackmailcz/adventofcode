package net.nooii.adventofcode.aoc2022

import net.nooii.adventofcode.helpers.*

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
            val min = Point3D(
                lavaPoints.minOf { it.x } - 1,
                lavaPoints.minOf { it.y } - 1,
                lavaPoints.minOf { it.z } - 1
            )
            val max = Point3D(
                lavaPoints.maxOf { it.x } + 1,
                lavaPoints.maxOf { it.y } + 1,
                lavaPoints.maxOf { it.z } + 1
            )
            val touches = mutableMapOf<Point3D, Int>()
            val visited = mutableSetOf<Point3D>()
            var waterPoints = mutableSetOf(
                Point3D(min.x, min.y, min.z)
            )
            while (waterPoints.isNotEmpty()) {
                val nextWaterPoints = mutableSetOf<Point3D>()
                for (waterPoint in waterPoints) {
                    if (waterPoint in visited) {
                        continue
                    }
                    visited.add(waterPoint)
                    for (adjacentPoint in waterPoint.getAdjacentSidePoints()) {
                        if (adjacentPoint.x in min.x..max.x && adjacentPoint.y in min.y..max.y && adjacentPoint.z in min.z..max.z) {
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
                val (x, y, z) = line.split(",").map { it.toInt() }
                Point3D(x, y, z)
            }.toSet()
        }
    }
}