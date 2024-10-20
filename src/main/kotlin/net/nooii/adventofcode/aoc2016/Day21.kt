package net.nooii.adventofcode.aoc2016

import com.github.shiguruikai.combinatoricskt.permutations
import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.captureFirstMatch
import net.nooii.adventofcode.helpers.rotate

class Day21 {

    private sealed interface Instruction {
        fun execute(input: String): String

        class SwapPosition(private val x: Int, private val y: Int) : Instruction {
            override fun execute(input: String): String {
                val charArray = input.toCharArray()
                val xBackup = input[x]
                charArray[x] = input[y]
                charArray[y] = xBackup
                return charArray.joinToString("")
            }
        }

        class SwapLetter(private val x: Char, private val y: Char) : Instruction {
            override fun execute(input: String): String {
                return input
                    .replace(x, '#')
                    .replace(y, x)
                    .replace('#', y)
            }
        }

        class RotateLeft(private val steps: Int) : Instruction {
            override fun execute(input: String): String {
                return input.rotate(-steps)
            }
        }

        class RotateRight(private val steps: Int) : Instruction {
            override fun execute(input: String): String {
                return input.rotate(steps)
            }
        }

        class RotateBasedOnLetter(private val letter: Char) : Instruction {
            override fun execute(input: String): String {
                val index = input.indexOf(letter)
                if (index == -1) error("Cannot rotate based on letter '$letter' in input: $input")
                val rotations = 1 + index + if (index >= 4) 1 else 0
                return input.rotate(rotations)
            }
        }

        class Reverse(private val start: Int, private val end: Int) : Instruction {
            override fun execute(input: String): String {
                val output = StringBuilder()
                if (start > 0) {
                    output.append(input.substring(0, start))
                }
                output.append(input.substring(start, end + 1).reversed())
                if (end + 1 < input.length) {
                    output.append(input.substring(end + 1, input.length))
                }
                return output.toString()
            }
        }

        class Move(private val x: Int, private val y: Int) : Instruction {
            override fun execute(input: String): String {
                val list = input.toCharArray().toMutableList()
                val char = list.removeAt(x)
                list.add(y, char)
                return list.joinToString("")
            }
        }
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2016).loadStrings("Day21Input")
            val instructions = processInput(input)
            part1("abcdefgh", instructions)
            part2("fbgdceah", instructions)
        }

        private fun part1(input: String, instructions: List<Instruction>) {
            var password = input
            for (instruction in instructions) {
                password = instruction.execute(password)
            }
            println(password)
        }

        private fun part2(input: String, instructions: List<Instruction>) {
            for (permutation in listOf("a", "b", "c", "d", "e", "f", "g", "h").permutations()) {
                val initialString = permutation.joinToString("")
                var password = initialString
                for (instruction in instructions) {
                    password = instruction.execute(password)
                }
                if (password == input) {
                    println(initialString)
                    return
                }
            }
        }

        private fun processInput(input: List<String>): List<Instruction> {
            val swapRegex = Regex("swap position (\\d+) with position (\\d+)")
            val swapLetterRegex = Regex("swap letter (\\w) with letter (\\w)")
            val rotateLeftRegex = Regex("rotate left (\\d+) steps?")
            val rotateRightRegex = Regex("rotate right (\\d+) steps?")
            val rotateBasedRegex = Regex("rotate based on position of letter (\\w)")
            val reverseRegex = Regex("reverse positions (\\d+) through (\\d+)")
            val moveRegex = Regex("move position (\\d+) to position (\\d+)")
            val instructions = mutableListOf<Instruction>()
            for (line in input) {
                when {
                    swapRegex.matches(line) -> {
                        val (x, y) = swapRegex.captureFirstMatch(line)
                        instructions += Instruction.SwapPosition(x.toInt(), y.toInt())
                    }
                    swapLetterRegex.matches(line) -> {
                        val (x, y) = swapLetterRegex.captureFirstMatch(line)
                        instructions += Instruction.SwapLetter(x[0], y[0])
                    }
                    rotateLeftRegex.matches(line) -> {
                        val (steps) = rotateLeftRegex.captureFirstMatch(line)
                        instructions += Instruction.RotateLeft(steps.toInt())
                    }
                    rotateRightRegex.matches(line) -> {
                        val (steps) = rotateRightRegex.captureFirstMatch(line)
                        instructions += Instruction.RotateRight(steps.toInt())
                    }
                    rotateBasedRegex.matches(line) -> {
                        val (letter) = rotateBasedRegex.captureFirstMatch(line)
                        instructions += Instruction.RotateBasedOnLetter(letter[0])
                    }
                    reverseRegex.matches(line) -> {
                        val (start, end) = reverseRegex.captureFirstMatch(line)
                        instructions += Instruction.Reverse(start.toInt(), end.toInt())
                    }
                    moveRegex.matches(line) -> {
                        val (x, y) = moveRegex.captureFirstMatch(line)
                        instructions += Instruction.Move(x.toInt(), y.toInt())
                    }
                    else -> continue
                }
            }
            return instructions
        }
    }
}