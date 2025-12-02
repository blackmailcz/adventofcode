package net.nooii.adventofcode.aoc2021

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader

/**
 * Created by Nooii on 01.12.2021
 */
object Day1 {

    @JvmStatic
    fun main(args: Array<String>) {
        val measurements = InputLoader(AoCYear.AOC_2021).loadInts("Day1Input")
        part1(measurements)
        part2(measurements)
    }

    private fun part1(measurements: List<Int>) {
        var increments = 0
        var previous: Int? = null
        for (measurement in measurements) {
            if (previous != null && measurement > previous) {
                increments++
            }
            previous = measurement
        }
        println(increments)
    }

    private fun part2(measurements: List<Int>) {
        val queue = mutableListOf<Int>()
        var increments = 0
        var previous: Int? = null
        for (measurement in measurements) {
            if (queue.size == 3) {
                queue.removeFirst()
            }
            queue.add(measurement)
            if (queue.size == 3) {
                val current = queue.sum()
                if (previous != null && current > previous) {
                    increments++
                }
                previous = current
            }
        }
        println(increments)
    }

}