package net.nooii.adventofcode2021

import net.nooii.adventofcode2021.helpers.InputLoader
import java.awt.Point
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sign

/**
 * Created by Nooii on 05.12.2021
 */
class Day5 {

    private class Line(
        val from : Point,
        val to : Point
    ) {

        fun isFlat() = from.x == to.x || from.y == to.y

        fun isDiagonal() = abs(from.x - to.x) == abs(from.y - to.y)

        fun getPoints() : List<Point> {
            return if (isFlat() || isDiagonal()) {
                val points = mutableListOf<Point>()
                val distance = max(abs(from.x - to.x), abs(from.y - to.y))
                val diff = Point(sign(to.x - from.x), sign(to.y - from.y))
                for (d in 0 .. distance) {
                    points.add(Point(from.x + d * diff.x, from.y + d * diff.y))
                }
                points.distinct()
            } else {
                emptyList()
            }
        }

        private fun sign(n : Int) = sign(n.toDouble()).toInt()

    }

    companion object {

        @JvmStatic
        fun main(args : Array<String>) {
            val input = InputLoader().loadStrings("Day5Input")
            val lines = parseLines(input)
            part1(lines)
            part2(lines)
        }

        private fun part1(lines : List<Line>) {
            println(computeIntersectionCount(lines.filter { it.isFlat() }))
        }

        private fun part2(lines : List<Line>) {
            println(computeIntersectionCount(lines.filter { it.isFlat() || it.isDiagonal() }))
        }

        private fun computeIntersectionCount(lines : List<Line>) : Int {
            val collectedPoints = mutableMapOf<Point, Int>()
            for (line in lines) {
                for (point in line.getPoints()) {
                    collectedPoints[point] = (collectedPoints[point] ?: 0) + 1
                }
            }
            return collectedPoints.count { it.value > 1 }
        }

        private fun parseLines(input : List<String>) : List<Line> {
            return input.map { rawLine ->
                val points = rawLine.split(" -> ")
                Line(parsePoint(points[0]), parsePoint(points[1]))
            }
        }

        private fun parsePoint(pointStr : String) : Point {
            val pointNumbers = pointStr.split(",").map { it.toInt() }
            return Point(pointNumbers[0], pointNumbers[1])
        }

    }

}