package net.nooii.adventofcode.aoc2022

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader

class Day6 {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2022).loadStrings("Day6Input")
            solution(input.first(), 4)
            solution(input.first(), 14)
        }

        private fun solution(input: String, sequenceLength: Int) {
            for ((index, sequence) in input.windowed(sequenceLength, 1).withIndex()) {
                if (sequence.toList().distinct().size == sequenceLength) {
                    println(index + sequenceLength)
                    return
                }
            }
            println("No marker found")
        }
    }
}