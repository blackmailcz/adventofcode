package net.nooii.adventofcode.aoc2017

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader

object Day17 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2017).loadInts("Day17Input").first()
        part1(input)
        part2(input)
    }

    private fun part1(stepSize: Int) {
        val rotations = 2017
        val buffer = mutableListOf(0)
        var position = 0
        for (i in 1..rotations) {
            position = (position + stepSize) % buffer.size + 1
            buffer.add(position, i)
        }
        println(buffer[buffer.indexOf(rotations) + 1])
    }

    private fun part2(stepSize: Int) {
        var valueAfterZero = 0
        var position = 0
        for (i in 1..50_000_000) {
            position = (position + stepSize) % i + 1
            if (position == 1) {
                valueAfterZero = i
            }
        }
        println(valueAfterZero)
    }
}