package net.nooii.adventofcode.aoc2017

import net.nooii.adventofcode.helpers.*

object Day18 {

    private sealed interface Value {

        fun resolve(registers: NNMap<String, Long>): Long

        data class Number(val value: Long) : Value {
            override fun resolve(registers: NNMap<String, Long>): Long = value
        }

        data class Register(val register: String) : Value {
            override fun resolve(registers: NNMap<String, Long>): Long = registers.getOrDefault(register, 0)
        }
    }

    private sealed interface Instruction {
        data class Snd(val target: Value) : Instruction
        data class Set(val register: Value.Register, val value: Value) : Instruction
        data class Add(val register: Value.Register, val value: Value) : Instruction
        data class Mul(val register: Value.Register, val value: Value) : Instruction
        data class Mod(val register: Value.Register, val value: Value) : Instruction
        data class Rcv(val target: Value) : Instruction
        data class Jgz(val condition: Value, val offset: Value) : Instruction
    }

    private class Computer(
        private val instructions: List<Instruction>,
        id: Long = 0,
        private val onSnd: (Long) -> Unit,
        private val onRcv: (Value, MutableNNMap<String, Long>) -> Boolean,
    ) {
        private val registers = mutableNNMapOf("p" to id)
        private var i = 0

        fun step(): Boolean {
            if (i >= instructions.size) {
                return false
            }
            with(instructions[i]) {
                when (this) {
                    is Instruction.Set -> {
                        registers[register.register] = value.resolve(registers)
                    }
                    is Instruction.Add -> {
                        registers[register.register] = register.resolve(registers) + value.resolve(registers)
                    }
                    is Instruction.Mul -> {
                        registers[register.register] = register.resolve(registers) * value.resolve(registers)
                    }
                    is Instruction.Mod -> {
                        registers[register.register] = register.resolve(registers).mod(value.resolve(registers))
                    }
                    is Instruction.Snd -> {
                        onSnd(target.resolve(registers))
                    }
                    is Instruction.Rcv -> {
                        if (!onRcv(target, registers)) {
                            return false
                        }
                    }
                    is Instruction.Jgz -> {
                        if (condition.resolve(registers) > 0L) {
                            i += offset.resolve(registers).toInt() - 1
                        }
                    }
                }
            }
            i++
            return true
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2017).loadStrings("Day18Input")
        val instructions = processInput(input)
        part1(instructions)
        part2(instructions)
    }

    private fun part1(instructions: List<Instruction>) {
        var sound = 0L
        var isTerminated = false
        val computer = Computer(
            instructions = instructions,
            onSnd = { sound = it },
            onRcv = { value, registers ->
                (value.resolve(registers) != 0L).also {
                    println(sound)
                    isTerminated = true
                }
            },
        )
        while (!isTerminated) {
            computer.step()
        }
    }

    private fun part2(instructions: List<Instruction>) {
        val queue0 = ArrayDeque<Long>()
        val queue1 = ArrayDeque<Long>()
        var computer1SendCount = 0
        val computer0 = Computer(
            instructions = instructions,
            id = 0,
            onSnd = {
                queue1.addLast(it)
            },
            onRcv = { value, registers ->
                queue0.isNotEmpty().also { canExecute ->
                    if (canExecute) {
                        val register = (value as Value.Register).register
                        registers[register] = queue0.removeFirst()
                    }
                }
            },
        )
        val computer1 = Computer(
            instructions = instructions,
            id = 1,
            onSnd = {
                computer1SendCount++
                queue0.addLast(it)
            },
            onRcv = { value, registers ->
                queue1.isNotEmpty().also { canExecute ->
                    if (canExecute) {
                        val register = (value as Value.Register).register
                        registers[register] = queue1.removeFirst()
                    }
                }
            },
        )
        do {
            val computer0Running = computer0.step()
            val computer1Running = computer1.step()
        } while (computer0Running || computer1Running)
        println(computer1SendCount)
    }

    private fun parseValue(value: String?): Value? {
        val asLong = value?.toLongOrNull()
        return when {
            asLong != null -> Value.Number(asLong)
            value != null -> Value.Register(value)
            else -> null
        }
    }

    private fun processInput(input: List<String>): List<Instruction> {
        val regex = Regex("\\s+")
        return input.map { line ->
            val parts = line.split(regex)
            val (instruction, o1raw) = parts
            val o2raw = parts.getOrNull(2)
            val o1 = parseValue(o1raw)!!
            val o2 = parseValue(o2raw)
            when (instruction) {
                "snd" -> Instruction.Snd(o1)
                "set" -> Instruction.Set(o1 as Value.Register, o2!!)
                "add" -> Instruction.Add(o1 as Value.Register, o2!!)
                "mul" -> Instruction.Mul(o1 as Value.Register, o2!!)
                "mod" -> Instruction.Mod(o1 as Value.Register, o2!!)
                "rcv" -> Instruction.Rcv(o1)
                "jgz" -> Instruction.Jgz(o1, o2!!)
                else -> error("Invalid instruction")
            }
        }
    }
}