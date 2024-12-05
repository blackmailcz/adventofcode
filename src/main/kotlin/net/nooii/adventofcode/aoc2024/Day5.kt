package net.nooii.adventofcode.aoc2024

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.splitByEmptyLine

class Day5 {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2024).loadStrings("Day5Input")
            val (orderingInput, updatesInput) = input.splitByEmptyLine()
            val ordering = processOrderingInput(orderingInput)
            val updates = processUpdatesInput(updatesInput)
            part1(ordering, updates)
            part2(ordering, updates)
        }

        private fun part1(ordering: Map<Int, Set<Int>>, updates: List<List<Int>>) {
            val result = updates
                .filter { it == correctUpdate(it, ordering) }
                .sumOf { it[it.size / 2] }
            println(result)
        }

        private fun part2(ordering: Map<Int, Set<Int>>, updates: List<List<Int>>) {
            val result = updates
                .mapNotNull { update -> correctUpdate(update, ordering).takeIf { it != update } }
                .sumOf { it[it.size / 2] }
            println(result)
        }

        private fun correctUpdate(update: List<Int>, ordering: Map<Int, Set<Int>>): List<Int> {
            return update.sortedWith { left, right ->
                val shouldLeftComeBeforeRight = ordering[left]?.contains(right) ?: false
                if (shouldLeftComeBeforeRight) -1 else 1
            }
        }

        private fun processOrderingInput(orderingInput: List<String>): Map<Int, Set<Int>> {
            return buildMap<Int, MutableSet<Int>> {
                for ((first, second) in orderingInput.map { line -> line.split("|").map { it.toInt() } }) {
                    computeIfAbsent(first) { mutableSetOf() }.add(second)
                }
            }
        }

        private fun processUpdatesInput(updatesInput: List<String>): List<List<Int>> {
            return updatesInput.map { line -> line.split(",").map { it.toInt() } }
        }
    }
}