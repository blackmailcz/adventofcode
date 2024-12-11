package net.nooii.adventofcode.aoc2024

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.add
import net.nooii.adventofcode.helpers.halve

class Day11 {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2024).loadStrings("Day11Input")
            val stones = processInput(input)
            part1(stones)
            part2(stones)
        }

        private fun part1(stones: Map<Long, Long>) {
            solution(stones, 25)
        }

        private fun part2(stones: Map<Long, Long>) {
            solution(stones, 75)
        }

        private fun solution(stones: Map<Long, Long>, repeats: Int) {
            var currentStones = stones
            repeat(repeats) {
                currentStones = process(currentStones)
            }
            println(currentStones.values.sum())
        }

        private fun process(stones: Map<Long, Long>): Map<Long, Long> {
            return buildMap {
                for ((stone, count) in stones) {
                    val newStones = when {
                        stone == 0L -> listOf(1L)
                        stone.toString().length % 2 == 0 -> stone.toString().halve { it.toLong() }
                        else -> listOf(stone * 2024L)
                    }
                    for (newStone in newStones) {
                        add(newStone, count)
                    }
                }
            }
        }

        private fun processInput(input: List<String>): Map<Long, Long> {
            return buildMap {
                for (stone in input.first().split(" ").map { it.toLong() }) {
                    add(stone, 1)
                }
            }
        }
    }
}