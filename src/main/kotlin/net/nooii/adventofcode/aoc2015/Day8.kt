package net.nooii.adventofcode.aoc2015

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader

object Day8 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2015).loadStrings("Day8Input")
        part1(input)
        part2(input)
    }

    private fun part1(input: List<String>) {
        println(input.sumOf { it.length - unescape(it).length })
    }

    private fun part2(input: List<String>) {
        println(input.sumOf { escape(it).length - it.length })
    }

    private fun unescape(string: String): String {
        return string
            .trim('"')
            .replace(Regex("\\\\(\\\\|\"|x[0-9A-FAa-f]{2})"), "?")
    }

    private fun escape(string: String): String {
        return string
            .replace(Regex("([\\\\\"])"), "\\$1")
            .let { "\"$it\"" }
    }
}