package net.nooii.adventofcode.aoc2023

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.add

class Day12 {

    private class Row(
        val springs: String,
        val stack: String
    )

    private data class State(
        val stack: String,
        val isPreviousHashtag: Boolean
    )

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2023).loadStrings("Day12Input")
            val rows = processInput(input)
            part1(rows)
            part2(rows)
        }

        private fun part1(rows: List<Row>) {
            solution(rows)
        }

        private fun part2(rows: List<Row>) {
            val expandedRows = rows.map { expandRow(it) }
            solution(expandedRows)
        }

        private fun solution(rows: List<Row>) {
            val sum = rows.sumOf { countArrangements(it) }
            println(sum)
        }

        private fun countArrangements(row: Row): Long {
            var states = mapOf(
                State(row.stack, false) to 1L
            )
            for (springSymbol in row.springs) {
                val nextStates = mutableMapOf<State, Long>()
                for ((state, stateCount) in states) {
                    if (state.stack.isEmpty()) {
                        // If state is empty, only transfer the state in case of '.' symbol, or '?' symbol (transformed to '.')
                        if (springSymbol != '#') {
                            nextStates.add(state.copy(), stateCount)
                        }
                        continue
                    }
                    val stateSymbol = state.stack.first()
                    if ((springSymbol == '#' || springSymbol == '?') && stateSymbol == '#') {
                        nextStates.add(State(state.stack.drop(1), true), stateCount)
                    }
                    if (springSymbol == '.' || springSymbol == '?') {
                        when (stateSymbol) {
                            '#' -> {
                                if (!state.isPreviousHashtag) {
                                    nextStates.add(State(state.stack, false), stateCount)
                                }
                            }

                            '.' -> {
                                // Forced space, remove the forced space, so next time it is not forced
                                nextStates.add(State(state.stack.drop(1), false), stateCount)
                            }
                        }
                    }
                }
                states = nextStates
            }
            // Count valid states (= stack of the state is empty)
            var sum = 0L
            for ((state, stateCount) in states) {
                if (state.stack.isEmpty()) {
                    sum += stateCount
                }
            }
            return sum
        }

        private fun expandRow(row: Row): Row {
            return Row(
                (row.springs + "?").repeat(5).dropLast(1),
                (row.stack + ".").repeat(5).dropLast(1)
            )
        }

        private fun createStack(input: String): String {
            val numbers = input.split(",").map { it.toInt() }
            return numbers.joinToString(".") { "#".repeat(it) }
        }

        private fun processInput(input: List<String>): List<Row> {
            return input.map { line ->
                val (spring, numbers) = line.split(" ")
                Row(
                    spring,
                    createStack(numbers)
                )
            }
        }
    }
}