package net.nooii.adventofcode.aoc2015

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.captureFirstMatch

class Day7 {

    private sealed class Value {

        companion object {

            fun fromString(value: String): Value {
                return value.toIntOrNull()?.let { Number(it) } ?: Variable(value)
            }
        }

        class Number(val value: Int) : Value()
        class Variable(val value: String) : Value()
    }

    private sealed class Instruction(val receiver: String) {
        class Simple(val value: Value, receiver: String) : Instruction(receiver)
        class And(val o1: Value, val o2: Value, receiver: String) : Instruction(receiver)
        class Or(val o1: Value, val o2: Value, receiver: String) : Instruction(receiver)
        class LShift(val o1: Value, val o2: Value, receiver: String) : Instruction(receiver)
        class RShift(val o1: Value, val o2: Value, receiver: String) : Instruction(receiver)
        class Not(val o: Value, receiver: String) : Instruction(receiver)
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2015).loadStrings("Day7Input")
            val instructions = processInput(input)
            part1(instructions)
            part2(instructions)
        }

        private fun part1(instructions: List<Instruction>) {
            println(solution(instructions))
        }

        private fun part2(instructions: List<Instruction>) {
            val a = solution(instructions)
            val newInstructions = instructions.toMutableList()
            newInstructions.removeIf { it.receiver == "b" }
            newInstructions.add(Instruction.Simple(Value.Number(a), "b"))
            println(solution(newInstructions))
        }

        private fun solution(instructions: List<Instruction>): Int {
            val known = mutableMapOf<String, Int>()
            var remaining = instructions
            do {
                val nextRemaining = remaining.toMutableList()
                for (instruction in remaining) {
                    known[instruction.receiver] = compute(instruction, known) ?: continue
                    nextRemaining.remove(instruction)
                }
                if (remaining.size == nextRemaining.size) {
                    break
                }
                remaining = nextRemaining
            } while (remaining.isNotEmpty())
            return known["a"] ?: throw IllegalStateException("Failure")
        }

        private fun compute(instruction: Instruction, known: Map<String, Int>): Int? {
            return when (instruction) {
                is Instruction.And -> {
                    val o1 = resolve(instruction.o1, known) ?: return null
                    val o2 = resolve(instruction.o2, known) ?: return null
                    o1 and o2
                }

                is Instruction.Or -> {
                    val o1 = resolve(instruction.o1, known) ?: return null
                    val o2 = resolve(instruction.o2, known) ?: return null
                    o1 or o2
                }

                is Instruction.LShift -> {
                    val o1 = resolve(instruction.o1, known) ?: return null
                    val o2 = resolve(instruction.o2, known) ?: return null
                    o1 shl o2
                }

                is Instruction.RShift -> {
                    val o1 = resolve(instruction.o1, known) ?: return null
                    val o2 = resolve(instruction.o2, known) ?: return null
                    o1 shr o2
                }

                is Instruction.Not -> {
                    val o1 = resolve(instruction.o, known) ?: return null
                    o1.inv() and 0xFFFF
                }

                is Instruction.Simple -> {
                    when (instruction.value) {
                        is Value.Number -> instruction.value.value
                        is Value.Variable -> known[instruction.value.value] ?: return null
                    }
                }
            }
        }

        private fun resolve(value: Value, known: Map<String, Int>): Int? {
            return when (value) {
                is Value.Number -> value.value
                is Value.Variable -> known[value.value]
            }
        }

        private fun processInput(input: List<String>): List<Instruction> {
            return input.map { parseInstruction(it) }
        }

        private fun parseInstruction(line: String): Instruction {
            val receiver = line.substringAfter("-> ")
            val equation = line.substringBefore(" ->")
            val matches = Regex("(\\S+) ?(\\w+)? ?(\\S+)?")
                .captureFirstMatch(equation)
                .filter { it.isNotEmpty() }
            return when (matches.size) {
                1 -> Instruction.Simple(Value.fromString(matches[0]), receiver)
                2 -> Instruction.Not(Value.fromString(matches[1]), receiver)
                else -> {
                    val o1 = Value.fromString(matches[0])
                    val o2 = Value.fromString(matches[2])
                    when (matches[1]) {
                        "AND" -> Instruction.And(o1, o2, receiver)
                        "OR" -> Instruction.Or(o1, o2, receiver)
                        "LSHIFT" -> Instruction.LShift(o1, o2, receiver)
                        "RSHIFT" -> Instruction.RShift(o1, o2, receiver)
                        else -> throw IllegalArgumentException("Invalid instruction: ${matches[1]}")
                    }
                }
            }
        }
    }
}