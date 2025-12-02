package net.nooii.adventofcode.aoc2016

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.fastCut
import net.nooii.adventofcode.helpers.size

object Day20 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2016).loadStrings("Day20Input")
        val ranges = processInput(input)
        val whitelist = createWhitelist(ranges)
        part1(whitelist)
        part2(whitelist)
    }

    private fun part1(whitelist: List<LongRange>) {
        println(whitelist.first().first)
    }

    private fun part2(whitelist: List<LongRange>) {
        println(whitelist.sumOf { it.size() })
    }

    private fun createWhitelist(ranges: List<LongRange>): List<LongRange> {
        var whitelist = mutableListOf(LongRange(0, 4294967295))
        for (blacklistRange in ranges) {
            val nextWhitelist = mutableListOf<LongRange>()
            for (whitelistRange in whitelist) {
                nextWhitelist.addAll(whitelistRange.fastCut(blacklistRange))
            }
            whitelist = nextWhitelist
        }
        return whitelist
    }

    private fun processInput(input: List<String>): List<LongRange> {
        return input.map { line ->
            val (start, end) = line.split("-").map { it.toLong() }
            LongRange(start, end)
        }
    }
}