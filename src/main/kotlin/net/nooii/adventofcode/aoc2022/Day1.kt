package net.nooii.adventofcode.aoc2022

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.splitByEmptyLine

object Day1 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2022).loadStrings("Day1Input")
        val sortedElves = computeElves(input)
        part1(sortedElves)
        part2(sortedElves)
    }

    private fun computeElves(input: List<String>): List<Int> {
        return input.splitByEmptyLine()
            .map { elf -> elf.sumOf { it.toInt() } }
            .sortedDescending()
    }

    private fun part1(sortedElves: List<Int>) {
        println(sortedElves.first())
    }

    private fun part2(sortedElves: List<Int>) {
        println(sortedElves.take(3).sum())
    }
}