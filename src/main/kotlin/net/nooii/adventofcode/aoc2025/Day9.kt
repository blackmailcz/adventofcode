package net.nooii.adventofcode.aoc2025

import com.github.shiguruikai.combinatoricskt.combinations
import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.Point
import net.nooii.adventofcode.helpers.Polygon
import kotlin.math.abs

object Day9 {

    private class PolygonHelper(points: List<Point>) {

        // Polygon entity
        private val polygon = Polygon(points)

        // Memoize checking of a point in the polygon
        private val cache = mutableMapOf<Point, Boolean>()

        // Precompute borders of both horizontal and vertical edges
        val xBorders = polygon.edges
            .filter { (p1, p2) -> p1.y == p2.y }
            .flatMap { (p1, p2) -> setOf(p1.x, p2.x) }
            .toSet()

        val yBorders = polygon.edges
            .filter { (p1, p2) -> p1.x == p2.x }
            .flatMap { (p1, p2) -> setOf(p1.y, p2.y) }
            .toSet()

        operator fun contains(point: Point): Boolean {
            return cache.getOrPut(point) { point in polygon }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2025).loadStrings("Day9Input")
        val points = processInput(input)
        part1(points)
        // Runtime ~ 230 ms
        part2(points)
    }

    private fun part1(points: List<Point>) {
        val max = points.combinations(2).maxOf { (p1, p2) ->
            computeArea(p1, p2)
        }
        println(max)
    }

    private fun part2(points: List<Point>) {
        // !!! Assuming polygon with straight edges only !!!
        val polygon = PolygonHelper(points)

        // Try all rectangle combinations, sorted by their area in descending order
        val rectangles = points
            .combinations(2)
            .sortedByDescending { (p1, p2) -> computeArea(p1, p2) }

        val area = rectangles
            .find { (p1, p2) -> isRectangleInPolygon(polygon, p1, p2) }
            ?.let { (p1, p2) -> computeArea(p1, p2) }

        if (area != null) {
            println(area)
        } else {
            println("No solution found")
        }
    }

    private fun isRectangleInPolygon(polygon: PolygonHelper, p1: Point, p2: Point): Boolean {
        // It is enough to check the rectangle border crossings with X and Y borders of the polygon on resp. sides
        val xRange = IntRange(minOf(p1.x, p2.x), maxOf(p1.x, p2.x))
        val yRange = IntRange(minOf(p1.y, p2.y), maxOf(p1.y, p2.y))
        for (x in polygon.xBorders) {
            if (x !in xRange) continue
            if (Point(x, p1.y) !in polygon || Point(x, p2.y) !in polygon) {
                return false
            }
        }
        for (y in polygon.yBorders) {
            if (y !in yRange) continue
            if (Point(p1.x, y) !in polygon || Point(p2.x, y) !in polygon) {
                return false
            }
        }
        return true
    }

    private fun computeArea(p1: Point, p2: Point): Long {
        return (abs(p1.x - p2.x).toLong() + 1) * (abs(p1.y - p2.y).toLong() + 1)
    }

    private fun processInput(input: List<String>): List<Point> {
        return input.map { line ->
            val (x, y) = line.split(",").map { it.toInt() }
            Point(x, y)
        }
    }
}