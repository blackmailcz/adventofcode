package net.nooii.adventofcode.aoc2015

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.add

class Day5 {

    companion object {

        private val vowels = "aeiou"
        private val bannedPairs = setOf("ab", "cd", "pq", "xy")

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2015).loadStrings("Day5Input")
            part1(input)
            part2(input)
        }

        private fun part1(input: List<String>) {
            solution(input) { isStringNice1(it) }
        }

        private fun part2(input: List<String>) {
            solution(input) { isStringNice2(it) }
        }

        private fun solution(input: List<String>, predicate: (String) -> Boolean) {
            val niceStringCount = input.count(predicate)
            println(niceStringCount)
        }

        private fun isStringNice1(string: String): Boolean {
            val c1 = string.count { it in vowels } >= 3
            val c2 = string.windowed(2, 1).any { it[0] == it[1] }
            val c3 = string.windowed(2, 1).none { it in bannedPairs }
            return c1 && c2 && c3
        }

        private fun isStringNice2(string: String): Boolean {
            val c1map = mutableMapOf<String, Int>()
            var skipFlag = false
            for (pair in string.windowed(2, 1)) {
                if (!skipFlag) {
                    c1map.add(pair, 1)
                }
                skipFlag = pair[0] == pair[1] && c1map[pair] == 1
            }
            val c1 = c1map.values.any { it >= 2 }
            val c2 = string.windowed(3, 1).any { it[0] == it[2] }
            return c1 && c2
        }
    }
}