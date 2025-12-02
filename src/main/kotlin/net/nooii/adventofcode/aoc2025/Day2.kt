package net.nooii.adventofcode.aoc2025

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.factors

class Day2 {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2025).loadStrings("Day2Input")
            val ranges = processInput(input)
            // Runtime ~ 270 ms
            part1(ranges)
            // Runtime ~ 1 sec
            part2(ranges)
        }

        private fun part1(ranges: List<LongRange>) {
            solution(ranges) {
                if (it.length % 2 == 0) listOf(it.length / 2) else emptyList()
            }
        }

        private fun part2(ranges: List<LongRange>) {
            // Caching the factors speeds up processing
            val factorsCache = mutableMapOf<Int, List<Int>>()
            solution(ranges) {
                factorsCache.getOrPut(it.length) { it.length.factors().dropLast(1) }
            }
        }

        private fun solution(ranges: List<LongRange>, factors: (String) -> List<Int>) {
            val sum = ranges.sumOf { range ->
                range.sumOf { id ->
                    val stringId = id.toString()
                    if (stringId.isWindowedByFactors(factors.invoke(stringId))) id else 0L
                }
            }
            println(sum)
        }

        private fun String.isWindowedByFactors(factors: List<Int>): Boolean {
            return factors.any { windowed(it, it).toSet().size == 1 }
        }

        private fun processInput(input: List<String>): List<LongRange> {
            return input.first().split(",").map {
                val parts = it.split("-")
                LongRange(parts.first().toLong(), parts.last().toLong())
            }
        }
    }
}