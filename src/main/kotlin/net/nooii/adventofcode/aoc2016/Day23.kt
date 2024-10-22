package net.nooii.adventofcode.aoc2016

import net.nooii.adventofcode.aoc2016.Day23.Instruction.*
import net.nooii.adventofcode.helpers.*

class Day23 {

    private sealed interface Value {
        data class Integer(val value: Int) : Value
        data class Register(val register: String) : Value
    }

    private sealed interface Instruction {
        data class Cpy(val from: Value, val to: Value) : Instruction {
            override fun isValid(): Boolean = to is Value.Register
        }

        data class Inc(val target: Value) : Instruction {
            override fun isValid(): Boolean = target is Value.Register
        }

        data class Dec(val target: Value) : Instruction {
            override fun isValid(): Boolean = target is Value.Register
        }

        data class Jnz(val condition: Value, val offset: Value) : Instruction
        data class Tgl(val target: Value) : Instruction

        fun isValid(): Boolean {
            return true
        }
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2016).loadStrings("Day23Input")
            val instructions = processInput(input)
            part1(instructions)
            part2(instructions)
        }

        private fun part1(instructions: List<Instruction>) {
            solution(instructions, 7)
        }

        private fun part2(instructions: List<Instruction>) {
            // Runtime ~ 55 sec
            solution(instructions, 12)
        }

        private fun solution(initialInstructions: List<Instruction>, initialA: Int) {
            var i = 0
            val registers = mutableNNMapOf(
                "a" to initialA,
                "b" to 0,
                "c" to 0,
                "d" to 0
            )
            val instructions = initialInstructions.toMutableList()
            while (i < instructions.size) {
                val instruction = instructions[i]
                when (instruction) {
                    is Cpy -> {
                        if (instruction.to is Value.Register) {
                            registers[instruction.to.register] = resolve(instruction.from, registers)
                        }
                    }
                    is Inc -> {
                        if (instruction.target is Value.Register) {
                            registers[instruction.target.register]++
                        }
                    }
                    is Dec -> {
                        if (instruction.target is Value.Register) {
                            registers[instruction.target.register]--
                        }
                    }
                    is Jnz -> {
                        if (resolve(instruction.condition, registers) != 0) {
                            i += resolve(instruction.offset, registers)
                        } else {
                            i++
                        }
                    }
                    is Tgl -> {
                        var targetInstructionIndex = i + resolve(instruction.target, registers)
                        var first = true
                        var newInstruction: Instruction?
                        do {
                            if (!first) {
                                targetInstructionIndex++
                            }
                            first = false
                            newInstruction =
                                when (val targetInstruction = instructions.getOrNull(targetInstructionIndex)) {
                                    null -> null
                                    is Inc -> Dec(targetInstruction.target)
                                    is Dec -> Inc(targetInstruction.target)
                                    is Tgl -> Inc(targetInstruction.target)
                                    is Cpy -> Jnz(targetInstruction.from, targetInstruction.to)
                                    is Jnz -> Cpy(targetInstruction.condition, targetInstruction.offset)
                                }
                        } while (newInstruction != null && !newInstruction.isValid())

                        if (newInstruction != null && newInstruction.isValid()) {
                            instructions[targetInstructionIndex] = newInstruction
                        }
                    }
                }
                if (instruction !is Jnz) {
                    i++
                }
            }
            println(registers["a"])
        }

        private fun resolve(value: Value, registers: NNMap<String, Int>): Int {
            return when (value) {
                is Value.Integer -> value.value
                is Value.Register -> registers[value.register]
            }
        }

        private fun processInput(input: List<String>): List<Instruction> {
            val regex = Regex("(\\w{3}) ([abcd]|-?\\d+) ?([abcd]|-?\\d+)?")
            return input.map { line ->
                val captured = regex.captureFirstMatch(line)
                val (instruction, o1raw) = captured
                val o2raw = captured.getOrNull(2)
                val o1 = parseValue(o1raw)
                val o2 = parseValue(o2raw)
                when (instruction) {
                    "cpy" -> Cpy(o1!!, o2!!)
                    "inc" -> Inc(o1!!)
                    "dec" -> Dec(o1!!)
                    "jnz" -> Jnz(o1!!, o2!!)
                    "tgl" -> Tgl(o1!!)
                    else -> error("Invalid instruction")
                }
            }
        }

        private fun parseValue(value: String?): Value? {
            val asInt = value?.toIntOrNull()
            return when {
                asInt != null -> Value.Integer(asInt)
                value != null -> Value.Register(value)
                else -> null
            }
        }
    }
}