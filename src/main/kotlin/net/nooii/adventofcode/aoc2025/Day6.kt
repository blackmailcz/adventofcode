package net.nooii.adventofcode.aoc2025

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader

object Day6 {

    private sealed class Operation(val length: Int) {

        class Add(length: Int) : Operation(length)
        class Multiply(length: Int) : Operation(length)

        companion object {
            fun fromSymbol(symbol: Char, length: Int): Operation {
                return when (symbol) {
                    '+' -> Add(length)
                    '*' -> Multiply(length)
                    else -> throw IllegalArgumentException("Invalid operation symbol: $symbol")
                }
            }
        }
    }

    private data class Problem(
        val operands: List<Long>,
        val operation: Operation
    )

    private data class Key(
        val problem: Int,
        val column: Int,
    )

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2025).loadStrings("Day6Input")
        part1(input)
        part2(input)
    }

    private fun part1(input: List<String>) {
        val operations = parseOperations(input.last(), input.maxOf { it.length })
        val operands = mutableMapOf<Int, MutableList<Long>>()
        for (line in input.dropLast(1)) {
            for ((columnIndex, operand) in line.trim().split(Regex("\\s+")).withIndex()) {
                operands.getOrPut(columnIndex) { mutableListOf() }.add(operand.toLong())
            }
        }
        val problems = operations.mapIndexed { index, operation ->
            Problem(operands[index]!!, operation)
        }
        evaluate(problems)
    }

    private fun part2(input: List<String>) {
        val operations = parseOperations(input.last(), input.maxOf { it.length })
        val operands = mutableMapOf<Key, String>()
        for (line in input.dropLast(1)) {
            var i = 0
            for ((problemIndex, operation) in operations.withIndex()) {
                val digits = line.drop(i).take(operation.length)
                for ((columnIndex, digit) in digits.withIndex()) {
                    val key = Key(problemIndex, columnIndex)
                    operands[key] = (operands[key] ?: "") + digit
                }
                i += operation.length + 1
            }
        }
        val problems = operations.mapIndexed { index, operation ->
            Problem(
                operands = operands.filter { it.key.problem == index }.map { it.value.trim().toLong() },
                operation = operation
            )
        }
        evaluate(problems)
    }

    private fun evaluate(problems: List<Problem>) {
        val sum = problems.sumOf { problem ->
            problem.operands.reduce { a, b ->
                when (problem.operation) {
                    is Operation.Add -> a + b
                    is Operation.Multiply -> a * b
                }
            }
        }
        println(sum)
    }

    private fun parseOperations(line: String, maxLineLength: Int): List<Operation> {
        var i = 0
        var length = 1
        val operations = mutableListOf<Operation>()
        // maxLineLength is needed here because IDE strips the trailing spaces from the input
        while (i < maxLineLength) {
            // Look ahead until next operation sign or end of line
            // We must go until <= maxLineLength, as last operation does not have an ending separator
            while (i + length <= maxLineLength && line.getOrElse(i + length) { ' ' } == ' ') {
                length++
            }
            // - 1 for blank separator between operations
            operations.add(Operation.fromSymbol(line[i], length - 1))
            // Shift to next operation
            i += length
            // Reset length for next operation
            length = 1
        }
        return operations
    }
}