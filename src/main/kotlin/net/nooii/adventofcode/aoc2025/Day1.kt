package net.nooii.adventofcode.aoc2025

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.captureFirstMatch

object Day1 {

    private enum class Direction(val sign: Int) {
        LEFT(-1), RIGHT(1);

        companion object {

            fun fromLetter(letter: String): Direction {
                return when (letter) {
                    "L" -> LEFT
                    "R" -> RIGHT
                    else -> throw IllegalArgumentException("Invalid direction: $letter")
                }
            }
        }
    }

    private data class Instruction(
        val direction: Direction,
        val amount: Int
    )

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2025).loadStrings("Day1Input")
        val instructions = processInput(input)
        solution(instructions)
    }

    private fun solution(instructions: List<Instruction>) {
        var position = 50
        var password1 = 0
        var password2 = 0
        instructions.forEach { (direction, amount) ->
            val reducedAmount = amount % 100
            password2 += amount / 100
            val next = position + reducedAmount * direction.sign
            position = when {
                next in 0..99 -> next
                next > 99 -> (next - 100).also { if (position != 0 && it != 0) password2++ }
                else -> (next + 100).also { if (position != 0 && it != 0) password2++ }
            }
            if (reducedAmount != 0 && position == 0) {
                password1++
                password2++
            }
        }
        println(password1)
        println(password2)
    }

    private fun processInput(input: List<String>): List<Instruction> {
        val regex = Regex("(\\w)(\\d+)")
        return input.map { line ->
            val (direction, amount) = regex.captureFirstMatch(line)
            Instruction(Direction.fromLetter(direction), amount.toInt())
        }
    }
}