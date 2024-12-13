package net.nooii.adventofcode.aoc2024

import net.nooii.adventofcode.helpers.*

class Day13 {

    private data class Configuration(
        val x1: Long,
        val y1: Long,
        val x2: Long,
        val y2: Long,
        val prizeX: Long,
        val prizeY: Long
    )

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2024).loadStrings("Day13Input")
            val configurations = processInput(input)
            part1(configurations)
            part2(configurations)
        }

        private fun part1(configurations: List<Configuration>) {
            solution(configurations)
        }

        private fun part2(configurations: List<Configuration>) {
            val addition = 10_000_000_000_000
            val newConfigurations = configurations.map {
                it.copy(prizeX = it.prizeX + addition, prizeY = it.prizeY + addition)
            }
            solution(newConfigurations)
        }

        private fun solution(configurations: List<Configuration>) {
            println(configurations.sumOf { computeCost(it) ?: 0 })
        }

        private fun computeCost(configuration: Configuration): Long? {
            with(configuration) {
                val (a, b) = solveTwoLinearEquations(longArrayOf(x1, x2, prizeX), longArrayOf(y1, y2, prizeY))
                    ?.takeIf { (a, b) -> a >= 0 && b >= 0 }
                    ?: return null
                return (3 * a + b)
            }
        }

        private fun processInput(input: List<String>): List<Configuration> {
            val buttonRegex = Regex("X\\+(\\d+), Y\\+(\\d+)")
            val prizeRegex = Regex("X=(\\d+), Y=(\\d+)")
            return input.splitByEmptyLine().map { chunk ->
                val (x1, y1) = buttonRegex.captureFirstMatch(chunk[0]) { it.toLong() }
                val (x2, y2) = buttonRegex.captureFirstMatch(chunk[1]) { it.toLong() }
                val (prizeX, prizeY) = prizeRegex.captureFirstMatch(chunk[2]) { it.toLong() }
                Configuration(x1, y1, x2, y2, prizeX, prizeY)
            }
        }
    }
}