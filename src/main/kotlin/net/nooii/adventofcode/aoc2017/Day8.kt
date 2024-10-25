package net.nooii.adventofcode.aoc2017

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.captureFirstMatch
import net.nooii.adventofcode.helpers.component6
import kotlin.math.max

class Day8 {

    private enum class Operator(val symbol: String) {
        GREATER(">"),
        LESS("<"),
        GREATER_OR_EQUALS(">="),
        LESS_OR_EQUALS("<="),
        EQUALS("=="),
        NOT_EQUALS("!=");

        companion object {

            fun from(symbol: String) = entries.find { it.symbol == symbol }!!
        }
    }

    private class Instruction(
        val instruction: String,
        val targetRegister: String,
        val targetValue: Int,
        val conditionRegister: String,
        val conditionOperator: Operator,
        val conditionValue: Int
    ) {

        fun execute(registers: MutableMap<String, Int>) {
            val sign = if (instruction == "inc") 1 else -1
            registers[targetRegister] = (registers[targetRegister] ?: 0) + sign * targetValue
        }

        fun checkCondition(registers: MutableMap<String, Int>): Boolean {
            val registerValue = registers[conditionRegister] ?: 0
            return when (conditionOperator) {
                Operator.GREATER -> registerValue > conditionValue
                Operator.LESS -> registerValue < conditionValue
                Operator.GREATER_OR_EQUALS -> registerValue >= conditionValue
                Operator.LESS_OR_EQUALS -> registerValue <= conditionValue
                Operator.EQUALS -> registerValue == conditionValue
                Operator.NOT_EQUALS -> registerValue != conditionValue
            }
        }
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2017).loadStrings("Day8Input")
            val instructions = processInput(input)
            solution(instructions)
        }

        private fun solution(instructions: List<Instruction>) {
            val registers = mutableMapOf<String, Int>()
            var absoluteMax = Int.MIN_VALUE
            for (instruction in instructions) {
                if (instruction.checkCondition(registers)) {
                    instruction.execute(registers)
                    absoluteMax = max(absoluteMax, registers.maxOf { it.value })
                }
            }
            println(registers.maxOf { it.value })
            println(absoluteMax)
        }

        private fun processInput(input: List<String>): List<Instruction> {
            val regex = Regex("(\\w+) (inc|dec) (-?\\d+) if (\\w+) (\\S+) (-?\\d+)")
            return input.map { line ->
                val (
                    targetRegister,
                    instruction,
                    targetValue,
                    conditionRegister,
                    conditionOperator,
                    conditionValue
                ) = regex.captureFirstMatch(line)
                Instruction(
                    instruction,
                    targetRegister,
                    targetValue.toInt(),
                    conditionRegister,
                    Operator.from(conditionOperator),
                    conditionValue.toInt()
                )
            }
        }
    }
}