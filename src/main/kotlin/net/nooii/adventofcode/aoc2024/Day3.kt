package net.nooii.adventofcode.aoc2024

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader

class Day3 {

    companion object {

        private const val DO = "do()"
        private const val DONT = "don't()"

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2024).loadStrings("Day3Input").joinToString("")
            part1(input)
            part2(input)
        }

        private fun part1(input: String) {
            solution(input, false)
        }

        private fun part2(input: String) {
            solution(input, true)
        }

        private fun solution(input: String, toggleEnabled: Boolean) {
            val regex = Regex("^mul\\((\\d{1,3}),(\\d{1,3})\\)")
            var line = input
            var sum = 0
            var enabled = true
            while (line.isNotEmpty()) {
                line = when {
                    toggleEnabled && line.startsWith(DO) -> {
                        enabled = true
                        line.drop(DO.length)
                    }
                    toggleEnabled && line.startsWith(DONT) -> {
                        enabled = false
                        line.drop(DONT.length)
                    }
                    else -> {
                        if (enabled) {
                            regex.find(line)?.groupValues?.drop(1)?.let { (a, b) ->
                                sum += a.toInt() * b.toInt()
                            }
                        }
                        line.drop(1)
                    }
                }
            }
            println(sum)
        }
    }
}