package net.nooii.adventofcode.aoc2017

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.captureFirstMatch

class Day16 {

    private sealed class Instruction {
        data class Spin(val count: Int) : Instruction()
        data class Exchange(val index1: Int, val index2: Int) : Instruction()
        data class Partner(val label1: Char, val label2: Char) : Instruction()
    }

    private data class State(
        val index: Int,
        val dancers: List<Char>
    )

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2017).loadStrings("Day16Input")
            val instructions = processInput(input)
            part1(instructions)
            part2(instructions)
        }

        private fun part1(instructions: List<Instruction>) {
            var dancers = ('a'..'p').toMutableList()
            for (instruction in instructions) {
                dancers = execute(instruction, dancers)
            }
            println(dancers.joinToString(""))
        }

        private fun part2(instructions: List<Instruction>) {
            var dancers = ('a'..'p').toMutableList()
            val seen = mutableSetOf(State(0, dancers))
            val targetSteps = 1_000_000_000L
            var steps = 0L
            val targetState: State
            // Dance until it starts to loop
            outer1@
            while (true) {
                for ((i, instruction) in instructions.withIndex()) {
                    dancers = execute(instruction, dancers)
                    val state = State(i, dancers)
                    if (seen.contains(state)) {
                        targetState = state
                        break@outer1
                    }
                    seen.add(state)
                    steps++
                }
            }
            val preLoopSteps = steps
            // Count the loop size (until we reach the same state again)
            outer2@
            while (true) {
                for ((i, instruction) in instructions.withIndex()) {
                    dancers = execute(instruction, dancers)
                    if (State(i, dancers) == targetState) {
                        break@outer2
                    }
                    steps++
                }
            }
            // Now determine remainder steps after complete loops
            val remainder = (targetSteps - preLoopSteps) % steps
            // Now do remainder of steps
            steps = targetSteps - remainder
            while (steps < targetSteps) {
                for (instruction in instructions) {
                    dancers = execute(instruction, dancers)
                    steps++
                }
            }
            // Print final state
            println(dancers.joinToString(""))
        }

        private fun execute(instruction: Instruction, dancers: MutableList<Char>): MutableList<Char> {
            when (instruction) {
                is Instruction.Spin -> {
                    val part1 = dancers.subList(dancers.size - instruction.count, dancers.size)
                    val part2 = dancers.subList(0, dancers.size - instruction.count)
                    return (part1 + part2).toMutableList()
                }
                is Instruction.Exchange -> {
                    val tmp = dancers[instruction.index1]
                    dancers[instruction.index1] = dancers[instruction.index2]
                    dancers[instruction.index2] = tmp
                }
                is Instruction.Partner -> {
                    val index1 = dancers.indexOf(instruction.label1)
                    val index2 = dancers.indexOf(instruction.label2)
                    val tmp = dancers[index1]
                    dancers[index1] = dancers[index2]
                    dancers[index2] = tmp
                }
            }
            return dancers
        }

        private fun processInput(input: List<String>): List<Instruction> {
            val spinRegex = Regex("s(\\d+)")
            val exchangeRegex = Regex("x(\\d+)/(\\d+)")
            val partnerRegex = Regex("p(\\w)/(\\w)")
            return input.first().split(",").map { move ->
                when (move.first()) {
                    's' -> {
                        val (count) = spinRegex.captureFirstMatch(move) { it.toInt() }
                        Instruction.Spin(count)
                    }
                    'x' -> {
                        val (index1, index2) = exchangeRegex.captureFirstMatch(move) { it.toInt() }
                        Instruction.Exchange(index1, index2)
                    }
                    'p' -> {
                        val (label1, label2) = partnerRegex.captureFirstMatch(move) { it.first() }
                        Instruction.Partner(label1, label2)
                    }
                    else -> error("Invalid instruction: $move")
                }
            }
        }
    }
}