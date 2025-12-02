package net.nooii.adventofcode.aoc2022

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader

object Day10 {

    private class Instruction(val ticks: Int, val increment: Int)

    private class CPU {

        private val strengthOffset = 20
        private val width = 40
        private var x = 1
        private var cycle = 0
        var strength = 0
            private set
        private val pixels = mutableListOf<Char>()

        private fun tick() {
            // Cycle
            cycle++
            // Strength
            if ((cycle + strengthOffset) % width == 0) {
                strength += cycle * x
            }
            // Pixel
            val symbol = if (pixels.size % width in x - 1..x + 1) '*' else ' '
            pixels.add(symbol)
        }

        fun execute(instructions: List<Instruction>) {
            for (instruction in instructions) {
                repeat(instruction.ticks) {
                    tick()
                }
                x += instruction.increment
            }
        }

        fun draw() {
            for (line in pixels.windowed(width, width)) {
                println(line.joinToString(""))
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2022).loadStrings("Day10Input")
        val instructions = parseInput(input)
        val cpu = CPU()
        cpu.execute(instructions)
        part1(cpu)
        part2(cpu)
    }

    private fun part1(cpu: CPU) {
        println(cpu.strength)
    }

    private fun part2(cpu: CPU) {
        cpu.draw()
    }

    private fun parseInput(input: List<String>): List<Instruction> {
        return input.map { line ->
            when {
                line == "noop" -> Instruction(1, 0)
                line.startsWith("addx") -> Instruction(2, line.drop(5).toInt()) // drop "addx "
                else -> throw IllegalStateException("Unknown instruction")
            }
        }
    }

}