package net.nooii.adventofcode.aoc2015

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.Point

object Day25 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2015).loadStrings("Day25Input")
        val point = processInput(input)
        part1(point)
    }

    private fun part1(point: Point) {
        var n = 20151125L
        val iterations = getNumberOfIterations(point)
        for (i in 0 until iterations - 1) {
            n = n * 252533L % 33554393L
        }
        println(n)
    }

    private fun getNumberOfIterations(point: Point): Long {
        // Formula for sum of sequence of numbers
        val n = (point.y + point.x - 2).toLong()
        return n + n * (n - 1) / 2 + point.y
    }

    private fun processInput(input: List<String>): Point {
        val (x, y) = Regex("(\\d+)").findAll(input.first()).map { it.value.toInt() }.toList()
        return Point(x, y)
    }
}