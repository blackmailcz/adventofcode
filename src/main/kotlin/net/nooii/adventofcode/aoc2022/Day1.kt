package net.nooii.adventofcode.aoc2022

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader

class Day1 {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2022).loadStrings("Day1Input")
            val sortedElves = computeElves(input)
            part1(sortedElves)
            part2(sortedElves)
        }

        private fun computeElves(input: List<String>): List<Int> {
            // Ensure last elf is processed as well
            val lines = if (input.lastOrNull()?.isNotEmpty() == true) input.plus("") else input
            val elves = mutableListOf<Int>()
            var calories = 0
            for (line in lines) {
                if (line.isEmpty()) {
                    elves.add(calories)
                    calories = 0
                } else {
                    calories += line.toInt()
                }
            }
            elves.sortDescending()
            return elves
        }

        private fun part1(sortedElves: List<Int>) {
            println(sortedElves.first())
        }

        private fun part2(sortedElves: List<Int>) {
            println(sortedElves.take(3).sum())
        }
    }
}