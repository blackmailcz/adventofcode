package net.nooii.adventofcode.aoc2015

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader

object Day1 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2015).loadStrings("Day1Input")
        part1(input)
        part2(input)
    }

    private fun part1(input: List<String>) {
        var level = 0
        for (char in input.first()) {
            when (char) {
                '(' -> level++
                ')' -> level--
            }
        }
        println(level)
    }

    private fun part2(input: List<String>) {
        var level = 0
        for ((index, char) in input.first().withIndex()) {
            when (char) {
                '(' -> level++
                ')' -> level--
            }
            if (level == -1) {
                println(index + 1)
                break
            }
        }
    }
}