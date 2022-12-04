package net.nooii.adventofcode.aoc2022

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.splitToPair

class Day4 {

    private class RangePair(
        val r1: IntRange,
        val r2: IntRange
    ) {

        fun fullyOverlap(): Boolean {
            return (r1.first >= r2.first && r1.last <= r2.last) || (r2.first >= r1.first && r2.last <= r1.last)
        }

        fun partiallyOverlap(): Boolean {
            return r1.first in r2 || r1.last in r2 || r2.first in r1 || r2.last in r1
        }
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2022).loadStrings("Day4Input")
            val rangePairs = parseInput(input)
            part1(rangePairs)
            part2(rangePairs)
        }

        private fun part1(rangePairs: List<RangePair>) {
            println(rangePairs.count { it.fullyOverlap() })
        }

        private fun part2(rangePairs: List<RangePair>) {
            println(rangePairs.count { it.partiallyOverlap() })
        }

        private fun parseInput(input: List<String>): List<RangePair> {
            return input.map { line ->
                val (r1, r2) = line.splitToPair(",")
                RangePair(
                    parseElfRange(r1),
                    parseElfRange(r2)
                )
            }
        }

        private fun parseElfRange(elfRange: String): IntRange {
            val (from, toInclusive) = elfRange.splitToPair("-")
            return IntRange(from.toInt(), toInclusive.toInt())
        }
    }
}