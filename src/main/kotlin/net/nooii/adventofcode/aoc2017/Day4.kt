package net.nooii.adventofcode.aoc2017

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader

object Day4 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2017).loadStrings("Day4Input")
        part1(input)
        part2(input)
    }

    private fun part1(input: List<String>) {
        solution(input)
    }

    private fun part2(input: List<String>) {
        solution(input) { it.toCharArray().sorted().toString() }
    }

    private fun solution(input: List<String>, transform: (String) -> String = { it }) {
        val validCount = input.count {
            val words = it.split(Regex("\\s+")).map { s -> transform(s) }
            val wordSet = words.toSet()
            words.size == wordSet.size
        }
        println(validCount)
    }
}