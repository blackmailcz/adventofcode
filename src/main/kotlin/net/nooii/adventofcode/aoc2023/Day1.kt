package net.nooii.adventofcode.aoc2023

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader

class Day1 {

    companion object {

        private val literals = mutableMapOf(
            "one" to "1",
            "two" to "2",
            "three" to "3",
            "four" to "4",
            "five" to "5",
            "six" to "6",
            "seven" to "7",
            "eight" to "8",
            "nine" to "9",
            null to "0"
        )

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2023).loadStrings("Day1Input")
            part1(input)
            part2(input)
        }

        private fun part1(input: List<String>) {
            solution(input, literals.values)
        }

        private fun part2(input: List<String>) {
            solution(input, literals.keys.filterNotNull() + literals.values)
        }

        private fun solution(input: List<String>, accepted: Collection<String>) {
            val sum = input.sumOf { getNumber(it, accepted) }
            println(sum)
        }

        private fun getNumber(line: String, accepted: Collection<String>): Int {
            val first = line.findAnyOf(accepted)!!.second.convertToDigit()
            val last = line.findLastAnyOf(accepted)!!.second.convertToDigit()
            return "$first$last".toInt()
        }

        private fun String.convertToDigit(): Char {
            val literal = when (this) {
                in literals.keys -> literals[this]!!
                in literals.values -> this
                else -> null
            }
            return literal!!.first()
        }
    }
}