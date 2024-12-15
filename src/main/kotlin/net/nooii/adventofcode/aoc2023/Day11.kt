package net.nooii.adventofcode.aoc2023

import com.github.shiguruikai.combinatoricskt.combinations
import net.nooii.adventofcode.helpers.*

class Day11 {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2023).loadStrings("Day11Input")
            part1(input)
            part2(input)
        }

        private fun part1(input: List<String>) {
            solution(input, 2)
        }

        private fun part2(input: List<String>) {
            solution(input, 1_000_000)
        }

        private fun solution(input: List<String>, spaceSize: Int) {
            val points = processInput(input, spaceSize)
            // Shortest distance = Manhattan distance
            val sum = points.combinations(2).sumOf { (from, to) -> from.manhattanDistance(to).toLong() }
            println(sum)
        }

        private fun processInput(input: List<String>, spaceSize: Int): Set<Point> {
            check(spaceSize > 0)
            // Scan vertically
            val yShifts = mutableSetOf<Int>()
            for ((y, line) in input.withIndex()) {
                if (!line.contains('#')) {
                    yShifts.add(y)
                }
            }
            // Scan horizontally
            val xShifts = mutableSetOf<Int>()
            for (x in input.first().indices) {
                if (IntRange(0, input.size - 1).none { y -> input[y][x] == '#' }) {
                    xShifts.add(x)
                }
            }
            // Build points and apply spacing
            val points = mutableSetOf<Point>()
            for (y in input.indices) {
                for (x in input[y].indices) {
                    if (input[y][x] == '#') {
                        val xShift = xShifts.count { it < x } * (spaceSize - 1)
                        val yShift = yShifts.count { it < y } * (spaceSize - 1)
                        points.add(Point(x + xShift, y + yShift))
                    }
                }
            }
            return points
        }
    }
}