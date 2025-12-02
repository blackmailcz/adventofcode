package net.nooii.adventofcode.aoc2017

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.captureFirstMatch

object Day15 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2017).loadStrings("Day15Input")
        val (a, b) = processInput(input)
        part1(a, b)
        part2(a, b)
    }

    private fun part1(a: Long, b: Long) {
        solution(a, b, 40_000_000, 1, 1)
    }

    private fun part2(a: Long, b: Long) {
        solution(a, b, 5_000_000, 4, 8)
    }

    private fun solution(initialA: Long, initialB: Long, cycles: Int, aMod: Int, bMod: Int) {
        val mod = 2_147_483_647L
        var a = initialA
        var b = initialB
        var matchCount = 0L
        repeat(cycles) {
            if (a % 65536L == b % 65536L) {
                matchCount++
            }
            do {
                a = (a * 16807L) % mod
            } while (a % aMod != 0L)
            do {
                b = (b * 48271L) % mod
            } while (b % bMod != 0L)
        }
        println(matchCount)
    }

    private fun processInput(input: List<String>): Pair<Long, Long> {
        val regex = Regex("(\\d+)")
        val (a, b) = input.map { regex.captureFirstMatch(it).first().toLong() }
        return Pair(a, b)
    }
}