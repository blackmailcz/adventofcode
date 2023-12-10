package net.nooii.adventofcode.aoc2015

import net.nooii.adventofcode.helpers.*

class Day19 {

    private class Calibration(
        val replacements: List<Replacement>,
        val molecule: String
    )

    private data class Replacement(
        val source: String,
        val target: String
    )

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2015).loadStrings("Day19Input")
            val calibration = processInput(input)
            part1(calibration)
            part2(calibration)
        }

        private fun part1(calibration: Calibration) {
            val variants = mutableSetOf<String>()
            for ((source, target) in calibration.replacements) {
                for (match in Regex(source).findAll(calibration.molecule)) {
                    variants += calibration.molecule.replaceRange(match.range, target)
                }
            }
            println(variants.size)
        }

        private fun part2(calibration: Calibration) {
            // As a proper solution would be probably impossible to solve by exploring all the states,
            // do a series of greedy backtracking in randomized order and find the minimum number of steps.
            // For the given input, the probability of not finding the shortest way is very close to zero
            // Runtime ~ 12 seconds (for 100000 iterations)
            val regexCache = NonNullMap(calibration.replacements.associate { it.target to Regex(it.target) }.toMutableMap())
            val solution = IntRange(0, 100000).minOf { backtrack(calibration, regexCache) ?: Int.MAX_VALUE }
            println(solution)
        }

        private fun backtrack(calibration: Calibration, regexCache: NonNullMap<String, Regex>): Int? {
            var molecule = calibration.molecule
            var steps = 0
            do {
                val replacements = calibration.replacements.shuffled()
                var replacedSomething = false
                for ((source, target) in replacements) {
                    do {
                        val match = regexCache[target].find(molecule)
                        if (match != null) {
                            molecule = molecule.replaceRange(match.range, source)
                            replacedSomething = true
                            steps++
                        }
                    } while (match != null)
                }
            } while (replacedSomething && molecule != "e")
            return steps.takeIf { molecule == "e" }
        }

        private fun processInput(input: List<String>): Calibration {
            val (replacementLines, molecule) = input.splitByEmptyLine()
            val replacements = replacementLines.map { line ->
                val (source, target) = Regex("(\\w+) => (\\w+)").captureFirstMatch(line)
                Replacement(source, target)
            }
            return Calibration(replacements, molecule.first())
        }
    }
}