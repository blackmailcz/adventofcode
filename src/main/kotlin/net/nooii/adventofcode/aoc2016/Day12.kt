package net.nooii.adventofcode.aoc2016

import net.nooii.adventofcode.aoc2016.Day12.Instruction.*
import net.nooii.adventofcode.helpers.*

object Day12 {

    private sealed interface Value {
        data class Integer(val value: Int) : Value
        data class Register(val register: String) : Value
    }

    private sealed interface Instruction {
        data class Cpy(val from: Value, val to: Value) : Instruction
        data class Inc(val target: Value) : Instruction
        data class Dec(val target: Value) : Instruction
        data class Jnz(val condition: Value, val offset: Value) : Instruction
    }

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