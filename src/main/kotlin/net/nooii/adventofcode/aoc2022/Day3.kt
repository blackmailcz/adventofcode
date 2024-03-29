package net.nooii.adventofcode.aoc2022

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.multiIntersection

class Day3 {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2022).loadStrings("Day3Input")
            part1(input)
            part2(input)
        }

        private fun part1(input: List<String>) {
            val sum = input.sumOf { line ->
                val firstHalf = line.toList().subList(0, line.length / 2)
                val secondHalf = line.toList().subList(line.length / 2, line.length)
                val intersection = firstHalf.intersect(secondHalf.toSet()).first()
                computeScore(intersection)
            }
            println(sum)
        }

        private fun part2(input: List<String>) {
            val windows = input.windowed(3, 3)
            val sum = windows.sumOf { window ->
                val intersection = multiIntersection(window.map { it.toList().toMutableSet() }).first()
                computeScore(intersection)
            }
            println(sum)
        }

        private fun computeScore(item: Char): Int {
            val startValue = (if (item.isLowerCase()) 'a' else 'A').code
            val scoreOffset = if (item.isLowerCase()) 1 else 27
            return item.code - startValue + scoreOffset
        }
    }
}