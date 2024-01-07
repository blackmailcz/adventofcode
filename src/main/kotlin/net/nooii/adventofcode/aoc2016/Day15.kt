package net.nooii.adventofcode.aoc2016

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.captureFirstMatch

class Day15 {

    private data class Disc(
        val id: Int,
        val positions: Int,
        val initialPosition: Int
    )

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2016).loadStrings("Day15Input")
            val discs = processInput(input)
            part1(discs)
            part2(discs)
        }

        private fun part1(discs: List<Disc>) {
            solution(discs)
        }

        private fun part2(discs: List<Disc>) {
            solution(discs + Disc(discs.size + 1, 11, 0))
        }

        private fun solution(discs: List<Disc>) {
            var i = -1
            do {
                i++
                val positions = discs.map { (it.id + it.initialPosition + i) % it.positions }
            } while (!positions.all { it == 0 })
            println(i)
        }

        private fun processInput(input: List<String>): List<Disc> {
            val regex = Regex("Disc #(\\d+) has (\\d+).*(\\d+).")
            return input.map { line ->
                val (id, positions, initialPosition) = regex.captureFirstMatch(line) { it.toInt() }
                Disc(id, positions, initialPosition)
            }
        }
    }
}