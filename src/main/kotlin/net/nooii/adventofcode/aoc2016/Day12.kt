package net.nooii.adventofcode.aoc2016

import net.nooii.adventofcode.aoc2016.Day12.Instruction.*
import net.nooii.adventofcode.helpers.*

class Day12 {

    private sealed interface Instruction {
        class CpyValue(val value: Int, val to: String) : Instruction
        class CpyRegister(val from: String, val to: String) : Instruction
        class Inc(val register: String) : Instruction
        class Dec(val register: String) : Instruction
        class JnzValue(val value: Int, val offset: Int) : Instruction
        class JnzRegister(val register: String, val offset: Int) : Instruction
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2016).loadStrings("Day12Input")
            val instructions = processInput(input)
            part1(instructions)
            part2(instructions)
        }

        private fun part1(instructions: List<Instruction>) {
            solution(instructions, 0)
        }

        private fun part2(instructions: List<Instruction>) {
            solution(instructions, 1)
        }

        private fun solution(instructions: List<Instruction>, initialC: Int) {
            var i = 0
            val registers = mutableNNMapOf(
                "a" to 0,
                "b" to 0,
                "c" to initialC,
                "d" to 0
            )
            while (i < instructions.size) {
                val instruction = instructions[i]
                when (instruction) {
                    is CpyValue -> registers[instruction.to] = instruction.value
                    is CpyRegister -> registers[instruction.to] = registers[instruction.from]
                    is Inc -> registers[instruction.register]++
                    is Dec -> registers[instruction.register]--
                    is JnzValue -> {
                        if (instruction.value != 0) {
                            i += instruction.offset
                        } else {
                            i++
                        }
                    }
                    is JnzRegister -> {
                        if (registers[instruction.register] != 0) {
                            i += instruction.offset
                        } else {
                            i++
                        }
                    }
                }
                if (instruction !is JnzRegister && instruction !is JnzValue) {
                    i++
                }
            }
            println(registers["a"])
        }

        private fun processInput(input: List<String>): List<Instruction> {
            val regex = Regex("(\\w{3}) ([abcd]|-?\\d+) ?([abcd]|-?\\d+)?")
            return input.map { line ->
                val captured = regex.captureFirstMatch(line)
                val (instruction, o1) = captured
                val o1asInt = o1.toIntOrNull()
                val o2 = captured.getOrNull(2)
                when (instruction) {
                    "cpy" -> if (o1asInt != null) CpyValue(o1asInt, o2!!) else CpyRegister(o1, o2!!)
                    "inc" -> Inc(o1)
                    "dec" -> Dec(o1)
                    "jnz" -> if (o1asInt != null) JnzValue(o1asInt, o2!!.toInt()) else JnzRegister(o1, o2!!.toInt())
                    else -> error("Invalid instruction")
                }
            }
        }
    }
}