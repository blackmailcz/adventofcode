package net.nooii.adventofcode.aoc2024

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.nn
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
                .filter { isUpdateCorrect(it, ordering) }
                .sumOf { it[it.size / 2] } // Rounding not specified, because input has always even size
            println(result)
        }

        private fun part2(ordering: Map<Int, Set<Int>>, updates: List<List<Int>>) {
            val result = updates
                .filter { !isUpdateCorrect(it, ordering) }
                .map { correctUpdate(it, ordering) }
                .sumOf { it[it.size / 2] } // Rounding not specified, because input has always even size
            println(result)
        }

        private fun isUpdateCorrect(update: List<Int>, ordering: Map<Int, Set<Int>>): Boolean {
            // Convert to map for fast access
            val pageToIndex = update.mapIndexed { index, value -> value to index }.toMap()
            // Check each page in the update against the rules for each page in the ordering
            for ((pageIndex, page) in update.withIndex()) {
                val rules = ordering[page] ?: continue
                for (rule in rules) {
                    val pageWithGreaterIndex = pageToIndex[rule] ?: continue
                    if (pageWithGreaterIndex < pageIndex) {
                        return false
                    }
                }
            }
            return true
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