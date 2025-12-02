package net.nooii.adventofcode.aoc2016

import net.nooii.adventofcode.aoc2016.Day25.Instruction.*
import net.nooii.adventofcode.helpers.*

object Day25 {

    private sealed interface Value {
        data class Integer(val value: Int) : Value
        data class Register(val register: String) : Value
    }

    private sealed interface Instruction {
        data class Cpy(val from: Value, val to: Value) : Instruction
        data class Inc(val target: Value) : Instruction
        data class Dec(val target: Value) : Instruction
        data class Jnz(val condition: Value, val offset: Value) : Instruction
        data class Out(val target: Value): Instruction
    }

    private const val OUTPUT_CRITERIA = 50

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2016).loadStrings("Day25Input")
        val instructions = processInput(input)
        part1(instructions)
    }

    private fun part1(instructions: List<Instruction>) {
        var a = -1
        do {
            a++
            val isValid = solution(instructions, a)
        } while (!isValid)
        println(a)
    }

    private fun solution(instructions: List<Instruction>, initialA: Int): Boolean {
        var i = 0
        val registers = mutableNNMapOf(
            "a" to initialA,
            "b" to 0,
            "c" to 0,
            "d" to 0
        )
        val output = mutableListOf<Int>()
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
                is Out -> {
                    output += resolve(instruction.target, registers)
                    if (checkOutput(output)) {
                        if (output.size == OUTPUT_CRITERIA) {
                            return true
                        }
                    } else {
                        return false
                    }
                }
            }
            if (instruction !is Jnz) {
                i++
            }
        }
        return false
    }

    private fun checkOutput(output: List<Int>): Boolean {
        for (chunk in output.windowed(2, 2)) {
            if (chunk != listOf(0, 1)) {
                return false
            }
        }
        return true
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
                "out" -> Out(o1!!)
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