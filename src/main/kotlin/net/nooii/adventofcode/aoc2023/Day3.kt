package net.nooii.adventofcode.aoc2023

import net.nooii.adventofcode.helpers.*

class Day3 {

    companion object {

        private class Number(
            val number: Int,
            val points: Set<Point>
        )

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2023).loadStrings("Day3Input")
            val pointMap = parsePoints(input)
            val numbers = parseNumbers(pointMap)
            part1(pointMap, numbers)
            part2(pointMap, numbers)
        }

        private fun part1(pointMap: PointMap<Char>, numbers: List<Number>) {
            val output = numbers.filter { number ->
                val pointsToCheck = mutableSetOf<Point>()
                for (point in number.points) {
                    for (dir in PointDirectionDiagonal.entries) {
                        val adj = dir.next(point)
                        if (pointMap.isInRange(adj) && adj !in number.points) {
                            pointsToCheck.add(adj)
                        }
                    }
                }
                pointsToCheck.any { !pointMap[it].isDigit() && pointMap[it] != '.' }
            }
            println(output.sumOf { it.number })
        }

        private fun part2(pointMap: PointMap<Char>, numbers: List<Number>) {
            val stars = pointMap.filterValues { it == '*' }.keys
            var output = 0L
            for (star in stars) {
                val adjacentStarPoints = PointDirectionDiagonal.entries.map { it.next(star) }
                val touchingNumbers = numbers.filter { adjacentStarPoints.intersect(it.points).isNotEmpty() }
                if (touchingNumbers.size == 2) {
                    output += touchingNumbers.map { it.number }.product()
                }
            }
            println(output)
        }

        private fun parseNumbers(pointMap: PointMap<Char>): List<Number> {
            val output = mutableListOf<Number>()
            for (y in 0 until pointMap.height) {
                var x = 0
                while (x < pointMap.width) {
                    val point = Point(x, y)
                    var scanPoint = point
                    var number = ""
                    val numberPoints = mutableSetOf<Point>()
                    while (pointMap.isInRange(scanPoint) && pointMap[scanPoint].isDigit()) {
                        number += pointMap[scanPoint]
                        numberPoints.add(scanPoint)
                        scanPoint = PointDirectionDiagonal.EAST.next(scanPoint)
                    }
                    if (numberPoints.isNotEmpty()) {
                        output.add(Number(number.toInt(), numberPoints))
                    }
                    x += numberPoints.size + 1
                }
            }
            return output
        }

        private fun parsePoints(input: List<String>): PointMap<Char> {
            return PointMap.filled(input.first().length, input.size) { x, y -> input[y][x] }
        }
    }
}