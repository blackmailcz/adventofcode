package net.nooii.adventofcode.aoc2017

import net.nooii.adventofcode.aoc2017.cross.AoC2017_Day10_KnotHash
import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader

class Day10 {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2017).loadStrings("Day10Input")
            part1(processInput1(input))
            part2(input.first())
        }

        private fun part1(lengths: List<Int>) {
            var position = 0
            var data = IntRange(0, 255).toList()
            for ((skipSize, length) in lengths.withIndex()) {
                data = AoC2017_Day10_KnotHash.reverseWrapped(data, position, length)
                position = (position + length + skipSize) % data.size
            }
            println(data[0] * data[1])
        }

        private fun part2(input: String) {
            println(AoC2017_Day10_KnotHash().hash(input))
        }

        private fun processInput1(input: List<String>): List<Int> {
            return input.first().split(',').map { it.toInt() }
        }
    }
}