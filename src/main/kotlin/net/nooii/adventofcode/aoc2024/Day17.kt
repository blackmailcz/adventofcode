package net.nooii.adventofcode.aoc2024

import com.github.shiguruikai.combinatoricskt.cartesianProduct
import net.nooii.adventofcode.helpers.*

class Day17 {

    private data class Registers(
        var a: Long = 0,
        var b: Long = 0,
        var c: Long = 0
    )

    private sealed interface ReturnType {
        class Jump(val value: Int) : ReturnType
        class Output(val value: Int) : ReturnType
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2024).loadStrings("Day17Input")
            val (registersInput, instructionsInput) = input.splitByEmptyLine()
            val registers = processRegisters(registersInput)
            val instructions = processInstructions(instructionsInput)
            part1(registers.copy(), instructions)
            // Runtime ~ 1 second
            part2(registers.copy(), instructions)
        }

        private fun part1(registers: Registers, instructions: List<Int>) {
            println(execute(registers, instructions).joinToString(","))
        }

        private fun part2(registers: Registers, instructions: List<Int>) {
            // Not a generic solution. Only works for this specific input pattern.
            // By execution analysis, trial and error and reverse engineering, we can state following:
            // - The number is a 16-digit number with each digit being a binary number 000-111.
            // - We can pad unknown digits with 111 and find out, that only first three digits are affected by changes
            // - We need to iterate over all combination of 4 digits at once, and check the 4th digit.
            // - Any unique 4th digit is a candidate for a checking of a next digit
            // - Walk recursively over all candidates until a solution is found. This solution would be the smallest.
            val prefixes = (0b000..0b111).toList()
            val mask = listOf(1).repeat(instructions.size)

            // Recursively find the next states until a solution is found
            fun find(state: List<Int>): List<Int>? {
                val candidates = mutableListOf<Int>()
                for (prefix in prefixes.cartesianProduct(repeat = 4)) {
                    // We create new number as: [padded with 1s until size of 16] [tested digits] [known digits]
                    val asList = mask.drop(4 + state.size) + prefix + state
                    // Convert to binary with padded zeroes
                    val asBinary = asList.to3bitBinary()
                    // Convert to decimal number
                    val asDecimal = binToDecLong(asBinary)
                    // Execute the program with the number in A register (in decimal form)
                    val execution = execute(registers.copy(a = asDecimal), instructions)
                    // We create a part that will be checked for correctness equal to the size of state
                    val checkedPart = instructions.take(state.size)
                    // Check if we get a result when we have a perfect match with a state of size 12, that means full 16-digit number.
                    if (state.size == 12 && execution == instructions) {
                        return prefix + state // Prepend the tested digits and return the correct result
                    }
                    // Otherwise take the last digit of checked prefix as a candidate to be checked in next round
                    if (execution.take(checkedPart.size) == checkedPart) {
                        candidates.add(prefix[3])
                    }
                }
                // For each unique candidate advance to next level
                // We use recursion here to assure the smallest result will be found first
                for (candidate in candidates.distinct()) {
                    val result = find(listOf(candidate) + state)
                    // If we found a solution in next level, propagate it.
                    if (result != null) {
                        return result
                    }
                }
                return null
            }
            // The result would propagate through the recursion up here. Initial state is empty
            val state = find(emptyList()) ?: error("No solution found")
            val asBinary = state.to3bitBinary()
            // We need to convert it back to decimal number
            val asDecimal = binToDecLong(asBinary)
            println(asDecimal)
        }

        private fun execute(registers: Registers, instructions: List<Int>): List<Int> {
            var i = 0
            val output = mutableListOf<Int>()
            while (i < instructions.size) {
                val code = instructions[i]
                val operand = instructions[i + 1]
                when (val returnType = executeInstruction(code, operand, registers)) {
                    is ReturnType.Jump -> {
                        i = returnType.value; continue
                    }
                    is ReturnType.Output -> {
                        output.add(returnType.value)
                    }
                    else -> {}
                }
                i += 2
            }
            return output
        }

        private fun List<Int>.to3bitBinary(): String {
            return this.joinToString("") { it.toString(2).padStart(3, '0') }
        }

        private fun executeInstruction(code: Int, operand: Int, registers: Registers): ReturnType? {
            when (code) {
                0 -> registers.a /= 2.pow(combo(operand, registers).toInt())
                1 -> registers.b = registers.b xor operand.toLong()
                2 -> registers.b = combo(operand, registers) % 8
                3 -> if (registers.a != 0L) return ReturnType.Jump(operand)
                4 -> registers.b = registers.b xor registers.c
                5 -> return ReturnType.Output((combo(operand, registers) % 8).toInt())
                6 -> registers.b = registers.a / 2.pow(combo(operand, registers).toInt())
                7 -> registers.c = registers.a / 2.pow(combo(operand, registers).toInt())
            }
            return null
        }

        private fun combo(code: Int, registers: Registers): Long {
            return when (code) {
                in 0..3 -> code.toLong()
                4 -> registers.a
                5 -> registers.b
                6 -> registers.c
                else -> error("Unknown operand code: $code")
            }
        }

        private fun processRegisters(input: List<String>): Registers {
            val regex = Regex("(-?\\d+)")
            val (a, b, c) = input.map { regex.captureFirstMatch(it).first().toLong() }
            return Registers(a, b, c)
        }

        private fun processInstructions(input: List<String>): List<Int> {
            return input.first().drop("Program: ".length).split(',').map { it.toInt() }
        }
    }
}