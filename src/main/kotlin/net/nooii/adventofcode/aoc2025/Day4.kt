package net.nooii.adventofcode.aoc2025

import net.nooii.adventofcode.helpers.*

object Day4 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2025).loadStrings("Day4Input")
        val points = processInput(input)
        part1(points)
        part2(points)
    }

    private fun part1(points: Set<Point>) {
        val solution = points.count { canBeRemoved(points, it) }
        println(solution)
    }

    private fun part2(points: Set<Point>) {
        var current = points
        var sum = 0
        do {
            val removed = current.filter { canBeRemoved(current, it) }.toSet()
            current = current - removed
            sum += removed.size
        } while (removed.isNotEmpty())
        println(sum)
    }

    private fun canBeRemoved(points: Set<Point>, point: Point): Boolean {
        return PointDirectionDiagonal.entries.filter { it.next(point) in points }.size < 4
    }

    private fun processInput(input: List<String>): Set<Point> {
        val width = input.first().length
        val height = input.size
        return pointRange(
            xRange = 0 until width,
            yRange = 0 until height,
            filter = { input[it.y][it.x] == '@' }
        )
    }
}