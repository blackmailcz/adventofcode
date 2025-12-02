package net.nooii.adventofcode.aoc2017

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import java.util.TreeMap

object Day13 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2017).loadStrings("Day13Input")
        val layers = processInput(input)
        part1(layers)
        part2(layers)
    }

    private fun part1(layers: Map<Int, Int>) {
        var severity = 0
        for ((time, range) in layers) {
            if (willBeCaught(time, range)) {
                severity += time * range
            }
        }
        println(severity)
    }

    private fun part2(layers: Map<Int, Int>) {
        var delay = 0
        while (true) {
            var gotCaught = false
            for ((time, range) in layers) {
                if (willBeCaught(time + delay, range)) {
                    gotCaught = true
                    break
                }
            }
            if (!gotCaught) {
                break
            }
            delay++
        }
        println(delay)
    }

    private fun willBeCaught(time: Int, range: Int): Boolean {
        return time % computeCycleLength(range) == 0
    }

    private fun computeCycleLength(range: Int): Int {
        return if (range == 1) 1 else 2 * (range - 1)
    }

    private fun processInput(input: List<String>): Map<Int, Int> {
        return TreeMap(input.associate { line ->
            val (depth, range) = line.split(": ")
            depth.toInt() to range.toInt()
        })
    }
}