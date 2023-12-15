package net.nooii.adventofcode.aoc2016

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.add

class Day6 {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2016).loadStrings("Day6Input")
            part1(input)
            part2(input)
        }

        private fun part1(input: List<String>) {
            solution(input) { map -> map.maxBy { it.value }.key }
        }

        private fun part2(input: List<String>) {
            solution(input) { map -> map.minBy { it.value }.key }
        }

        private fun solution(input: List<String>, decode: (map: Map<Char, Int>) -> Char) {
            val message = StringBuilder()
            for (x in input.first().indices) {
                val charMap = mutableMapOf<Char, Int>()
                for (line in input) {
                    charMap.add(line[x], 1)
                }
                message.append(decode.invoke(charMap))
            }
            println(message.toString())
        }
    }
}