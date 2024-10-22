package net.nooii.adventofcode.aoc2017

import com.github.shiguruikai.combinatoricskt.permutations
import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader

class Day2 {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2017).loadStrings("Day2Input")
            val numbers = processInput(input)
            part1(numbers)
            part2(numbers)
        }

        private fun part1(numbers: List<List<Int>>) {
            val solution = numbers.sumOf { it.max() - it.min() }
            println(solution)
        }

        private fun part2(numbers: List<List<Int>>) {
            val solution = numbers.sumOf {
                it.permutations(2)
                    .find { (first, second) -> first % second == 0 }
                    ?.let { (first, second) -> first / second }
                    ?: 0
            }
            println(solution)
        }

        private fun processInput(input: List<String>): List<List<Int>> {
            return input.map { it.split(Regex("\\s")).map(String::toInt) }
        }
    }
}