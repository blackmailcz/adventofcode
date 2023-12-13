package net.nooii.adventofcode.aoc2015

import com.github.shiguruikai.combinatoricskt.combinations
import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.product

class Day24 {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2015).loadInts("Day24Input")
            part1(input)
            part2(input)
        }

        private fun part1(input: List<Int>) {
            println(solution(input, 3))
        }

        private fun part2(input: List<Int>) {
            println(solution(input, 4))
        }

        private fun solution(input: List<Int>, groups: Int): Long {
            val targetSum = input.sum() / groups
            for (n in 1 until input.size) {
                for (combination in input.combinations(n)) {
                    if (combination.sum() == targetSum) {
                        return combination.map { it.toLong() }.product()
                    }
                }
            }
            error("No solution found")
        }
    }
}