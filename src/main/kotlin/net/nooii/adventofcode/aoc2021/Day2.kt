package net.nooii.adventofcode.aoc2021

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader

/**
 * Created by Nooii on 02.12.2021
 */
object Day2 {

    private data class Submarine(
        var depth: Int = 0,
        var distance: Int = 0,
        var aim: Int = 0
    ) {
        override fun toString(): String {
            return (depth * distance).toString()
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2021).loadStrings("Day2Input")
        part1(input)
        part2(input)
    }

    private fun part1(inputs: List<String>) {
        val submarine = Submarine()
        inputs.forEach { input ->
            val parts = input.split(" ")
            val diff = parts[1].toInt()
            when (parts[0]) {
                "forward" -> submarine.distance += diff
                "down" -> submarine.depth += diff
                "up" -> submarine.depth -= diff
            }
        }
        println(submarine)
    }

    private fun part2(inputs: List<String>) {
        val submarine = Submarine()
        inputs.forEach { input ->
            val parts = input.split(" ")
            val diff = parts[1].toInt()
            when (parts[0]) {
                "forward" -> {
                    submarine.distance += diff
                    submarine.depth += submarine.aim * diff
                }
                "down" -> submarine.aim += diff
                "up" -> submarine.aim -= diff
            }
        }
        println(submarine)
    }

}