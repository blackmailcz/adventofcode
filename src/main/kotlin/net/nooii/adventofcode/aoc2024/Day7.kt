package net.nooii.adventofcode.aoc2024

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader

class Day7 {

    private data class Equation(
        val result: Long,
        val operands: List<Long>
    )

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2024).loadStrings("Day7Input")
            val equations = processInput(input)
            part1(equations)
            // Runtime ~ 1.5 seconds
            part2(equations)
        }

        private fun part1(equations: List<Equation>) {
            solution(equations, withConcatenation = false)
        }

        private fun part2(equations: List<Equation>) {
            solution(equations, withConcatenation = true)
        }

        private fun solution(equations: List<Equation>, withConcatenation: Boolean) {
            val sum = equations.filter { checkEquation(it, withConcatenation) }.sumOf { it.result }
            println(sum)
        }

        private fun checkEquation(equation: Equation, withConcatenation: Boolean): Boolean {
            var states = setOf(equation.operands.first())
            for (operand in equation.operands.drop(1)) {
                val nextStates = mutableSetOf<Long>()
                for (state in states) {
                    val nextValues = setOfNotNull(
                        state + operand,
                        state * operand,
                        if (withConcatenation) "$state$operand".toLong() else null
                    )
                    // Because the value is never decreased, we can skip values greater than the result
                    for (nextValue in nextValues) {
                        nextValue.takeIf { it <= equation.result }?.let { nextStates.add(it) }
                    }
                }
                states = nextStates
            }
            return equation.result in states
        }

        private fun processInput(input: List<String>): List<Equation> {
            return input.map { line ->
                val parts = line.split(":")
                Equation(
                    result = parts[0].toLong(),
                    operands = parts[1].trim().split(" ").map { it.toLong() }
                )
            }
        }
    }
}