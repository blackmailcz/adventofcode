package net.nooii.adventofcode.aoc2015

import net.nooii.adventofcode.helpers.*

class Day23 {

    private sealed interface Instruction {
        class Hlf(val register: Register) : Instruction
        class Tpl(val register: Register) : Instruction
        class Inc(val register: Register) : Instruction
        class Jmp(val offset: Int) : Instruction
        class Jie(val register: Register, val offset: Int) : Instruction
        class Jio(val register: Register, val offset: Int) : Instruction
    }

    private class Register(var value: Long = 0L)

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val registers = nnMapOf(
                'a' to Register(),
                'b' to Register()
            )
            val input = InputLoader(AoCYear.AOC_2015).loadStrings("Day23Input")
            val instructions = processInput(registers, input)
            part1(registers, instructions)
            part2(registers, instructions)
        }

        private fun part1(registers: NNMap<Char, Register>, instructions: List<Instruction>) {
            registers['a'].value = 0
            registers['b'].value = 0
            solution(registers, instructions)
        }

        private fun part2(registers: NNMap<Char, Register>, instructions: List<Instruction>) {
            registers['a'].value = 1
            registers['b'].value = 0
            solution(registers, instructions)
        }

        private fun solution(registers: NNMap<Char, Register>, instructions: List<Instruction>) {
            var i = 0
            while (i < instructions.size) {
                when (val instruction = instructions[i]) {
                    is Instruction.Hlf -> {
                        instruction.register.value /= 2
                        i++
                    }
                    is Instruction.Tpl -> {
                        instruction.register.value *= 3
                        i++
                    }
                    is Instruction.Inc -> {
                        instruction.register.value++
                        i++
                    }
                    is Instruction.Jmp -> i += instruction.offset
                    is Instruction.Jie -> if (instruction.register.value % 2 == 0L) i += instruction.offset else i++
                    is Instruction.Jio -> if (instruction.register.value == 1L) i += instruction.offset else i++
                }
            }
            println(registers['b'].value)
        }

        private fun processInput(registers: NNMap<Char, Register>, input: List<String>): List<Instruction> {
            return input.map { line ->
                when (line.take(3)) {
                    "hlf" -> Instruction.Hlf(registers[line[4]])
                    "tpl" -> Instruction.Tpl(registers[line[4]])
                    "inc" -> Instruction.Inc(registers[line[4]])
                    "jmp" -> Instruction.Jmp(Regex("jmp \\+?(-?\\d+)").captureFirstMatch(line) { it.toInt() }.first())
                    "jie" -> {
                        val (register, offset) = Regex("jie (a|b), \\+?(-?\\d+)").captureFirstMatch(line)
                        Instruction.Jie(registers[register.first()], offset.toInt())
                    }
                    "jio" -> {
                        val (register, offset) = Regex("jio (a|b), \\+?(-?\\d+)").captureFirstMatch(line)
                        Instruction.Jio(registers[register.first()], offset.toInt())
                    }
                    else -> throw IllegalArgumentException("Invalid instruction: $line")
                }
            }
        }
    }
}