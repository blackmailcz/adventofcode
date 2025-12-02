package net.nooii.adventofcode.aoc2017

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader

object Day1 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2017).loadStrings("Day1Input")
        val sequence = input.first()
        part1(sequence)
        part2(sequence)
    }

    private fun part1(input: String) {
        solution(input) { it + 1 }
    }

    private fun part2(input: String) {
        solution(input) { it + input.length / 2 }
    }

    private fun solution(input: String, next: (current: Int) -> Int) {
        val sum = input.withIndex().sumOf { (index, char) ->
            val nextChar = input[next.invoke(index) % input.length]
            if (nextChar == char) nextChar.digitToInt() else 0
        }
        println(sum)
    }
}