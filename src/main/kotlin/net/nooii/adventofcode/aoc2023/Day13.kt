package net.nooii.adventofcode.aoc2023

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.splitByEmptyLine

class Day13 {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2023).loadStrings("Day13Input")
            val areas = processInput(input)
            part1(areas)
            part2(areas)
        }

        private fun part1(areas: List<List<String>>) {
            solution(areas, 0)
        }

        private fun part2(areas: List<List<String>>) {
            solution(areas, 1)
        }

        private fun solution(areas: List<List<String>>, smudges: Int) {
            val sum = areas.sumOf { area ->
                findReflection(area, smudges)?.let { it * 100 }
                    ?: findReflection(rotateArea(area), smudges)
                    ?: error("No reflection found")
            }
            println(sum)
        }

        private fun findReflection(area: List<String>, smudges: Int): Int? {
            outer@
            for (i in 1 until area.size) {
                var smudgesFound = 0
                for (j in area.indices) {
                    val side1 = area.getOrNull(i - j - 1)
                    val side2 = area.getOrNull(i + j)
                    smudgesFound += diff(side1, side2)
                    if (smudgesFound > smudges) {
                        continue@outer
                    }
                }
                if (smudgesFound == smudges) {
                    return i
                }
            }
            return null
        }

        private fun diff(s1: String?, s2: String?): Int {
            if (s1 == null || s2 == null) return 0
            check(s1.length == s2.length)
            return s1.zip(s2).count { (c1, c2) -> c1 != c2 }
        }

        private fun rotateArea(input: List<String>): List<String> {
            return IntRange(0, input.first().length - 1).map { x ->
                input.mapNotNull { line -> line.getOrNull(x) }.toString()
            }
        }

        private fun processInput(input: List<String>): List<List<String>> {
            return input.splitByEmptyLine()
        }
    }
}