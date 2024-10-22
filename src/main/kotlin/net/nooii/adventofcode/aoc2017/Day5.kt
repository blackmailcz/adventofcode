package net.nooii.adventofcode.aoc2017

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader

class Day5 {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2017).loadInts("Day5Input")
            part1(input)
            part2(input)
        }

        private fun part1(input: List<Int>) {
            solution(input) { it + 1 }
        }

        private fun part2(input: List<Int>) {
            solution(input) { if (it >= 3) it - 1 else it + 1 }
        }

        private fun solution(input: List<Int>, transform: (Int) -> Int) {
            val offsets = input.toMutableList()
            var steps = 0
            var index = 0
            while (index in offsets.indices) {
                steps++
                val nextIndex = index + offsets[index]
                offsets[index] = transform(offsets[index])
                index = nextIndex
            }
            println(steps)
        }
    }
}