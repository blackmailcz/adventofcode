package net.nooii.adventofcode.aoc2022

import net.nooii.adventofcode.helpers.*
import java.util.*

object Day5 {

    private data class Crane(
        val stacks: NNMap<Int, Stack<Char>>, // The map is sorted
        val instructions: List<Instruction>
    ) {

        fun getMessage(): String {
            return stacks.values.map { it.peek() }.joinToString("")
        }
    }

    private data class Instruction(
        val boxCount: Int,
        val from: Int,
        val to: Int
    )

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2022).loadStrings("Day5Input")
        // The original order is destroyed, we need fresh parse for each part.
        part1(parseInput(input))
        part2(parseInput(input))
    }

    private fun part1(crane: Crane) {
        for (instruction in crane.instructions) {
            repeat(instruction.boxCount) {
                val box = crane.stacks[instruction.from].pop()
                crane.stacks[instruction.to].push(box)
            }
        }
        println(crane.getMessage())
    }

    private fun part2(crane: Crane) {
        for (instruction in crane.instructions) {
            val instructionStack = Stack<Char>()
            repeat(instruction.boxCount) {
                val box = crane.stacks[instruction.from].pop()
                instructionStack.push(box)
            }
            repeat(instruction.boxCount) {
                val box = instructionStack.pop()
                crane.stacks[instruction.to].push(box)
            }
        }
        println(crane.getMessage())
    }

    private fun parseInput(input: List<String>): Crane {
        // First, find empty line that splits the boxes from instructions
        val emptyLine = input.indexOfFirst { it.isEmpty() }
        // Parse boxes bottom to top (-1 for skipping boxes numbering that we don't need)
        val stacks = sortedMapOf<Int, Stack<Char>>()
        input.take(emptyLine - 1).reversed().forEach {
            parseBoxLineAndAddBoxes(it, stacks)
        }
        // Parse instructions
        val instructions = input.drop(emptyLine + 1).map {
            parseInstruction(it)
        }
        return Crane(stacks.nn(), instructions)
    }

    private fun parseBoxLineAndAddBoxes(line: String, stacks: MutableMap<Int, Stack<Char>>) {
        // The box names are located at indexes: 1, 5, 9, ...
        var stackIndex = 1 // Stacks are indexed from 1
        for (i in 1 until line.length step 4) {
            if (line[i].isLetter()) {
                // Find corresponding stack or create new one if needed
                val stack = stacks[stackIndex] ?: Stack<Char>().also { stacks[stackIndex] = it }
                stack.push(line[i])
            }
            stackIndex++
        }
    }

    private fun parseInstruction(line: String): Instruction {
        // "move X from Y to Z"
        val matches = Regex("move (\\d+) from (\\d+) to (\\d+)")
            .captureFirstMatch(line) { it.toInt() }
        return Instruction(matches[0], matches[1], matches[2])
    }

}