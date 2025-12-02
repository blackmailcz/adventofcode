package net.nooii.adventofcode.aoc2016

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader

object Day18 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2016).loadStrings("Day18Input").first()
        part1(input)
        // Runtime ~ 1 second
        part2(input)
    }

    private fun part1(input: String) {
        solution(input, 40)
    }

    private fun part2(input: String) {
        solution(input, 400000)
    }

    private fun solution(input: String, repeats: Int) {
        var s = input.toList()
        var count = 0
        repeat(repeats) {
            count += s.count { it == '.' }
            s = computeNextRow(s)
        }
        println(count)
    }

    private fun computeNextRow(input: List<Char>): List<Char> {
        return input.indices.map { if (isTrap(input, it)) '^' else '.' }
    }

    private fun isTrap(input: List<Char>, index: Int): Boolean {
        val left = input.getOrNull(index - 1) == '^'
        val center = input[index] == '^'
        val right = input.getOrNull(index + 1) == '^'
        // Detect trap
        val c1 = left && center && !right
        val c2 = !left && center && right
        val c3 = left && !center && !right
        val c4 = !left && !center && right
        return c1 || c2 || c3 || c4
    }
}