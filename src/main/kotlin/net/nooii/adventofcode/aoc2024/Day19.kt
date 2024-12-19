package net.nooii.adventofcode.aoc2024

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.add
import net.nooii.adventofcode.helpers.splitByEmptyLine

class Day19 {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2024).loadStrings("Day19Input")
            val (towelsInput, designs) = input.splitByEmptyLine()
            val towels = towelsInput.first().split(", ").toSet()
            // Runtime ~ 600 ms
            solution(towels, designs)
        }

        private fun solution(towels: Set<String>, designs: List<String>) {
            val combinations = designs.map { countCombinations(towels, it) }
            part1(combinations)
            part2(combinations)
        }

        private fun part1(combinations: List<Long>) {
            println(combinations.count { it > 0L })
        }

        private fun part2(combinations: List<Long>) {
            println(combinations.sum())
        }

        private fun countCombinations(towels: Set<String>, design: String): Long {
            var states = mapOf(design to 1L)
            var totalCount = 0L
            while (states.isNotEmpty()) {
                val nextStates = mutableMapOf<String, Long>()
                for ((state, stateCount) in states) {
                    if (state.isEmpty()) {
                        totalCount += stateCount
                        continue
                    }
                    for (towel in towels) {
                        if (state.startsWith(towel)) {
                            nextStates.add(state.drop(towel.length), stateCount)
                        }
                    }
                }
                states = nextStates
            }
            return totalCount
        }
    }
}