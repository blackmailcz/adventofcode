package net.nooii.adventofcode.aoc2023

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.product
import net.nooii.adventofcode.helpers.size

class Day6 {

    private data class Race(
        val time: Long,
        val record: Long
    )

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2023).loadStrings("Day6Input")
            val races = processInput(input)
            part1(races)
            part2(races)
        }

        private fun part1(races: List<Race>) {
            val bestTimeCount = races.map { race ->
                LongRange(0, race.time).count { beats(race, it) }
            }
            println(bestTimeCount.product())
        }

        private fun part2(races: List<Race>) {
            val time = races.map { it.time }.joinToString("").toLong()
            val record = races.map { it.record }.joinToString("").toLong()
            val race = Race(time, record)
            // We need to find the first and last time to beat - the graph is parabolic
            val timeRange = LongRange(0, race.time)
            val lowEdge = binarySearch(timeRange, findLowest = true) { beats(race, it) }
            val highEdge = binarySearch(timeRange, findLowest = false) { beats(race, it) }
            val range = LongRange(lowEdge, highEdge)
            println(range.size())
        }

        private fun beats(race: Race, holdTime: Long): Boolean {
            return holdTime * (race.time - holdTime) > race.record
        }

        private fun binarySearch(
            searchRange: LongRange,
            findLowest: Boolean,
            predicate: (Long) -> Boolean
        ): Long {
            var range = searchRange
            while (range.size() > 1) {
                val mid = range.first + range.size() / 2
                val isMatching = predicate.invoke(mid)
                range = when {
                    findLowest && isMatching -> LongRange(range.first, mid)
                    findLowest && !isMatching -> LongRange(mid + 1, range.last)
                    !findLowest && isMatching -> LongRange(mid, range.last)
                    else -> LongRange(range.first, mid - 1) // !findLowest && !isMatching
                }
            }
            if (range.size() == 1L) {
                return range.first
            } else {
                throw IllegalStateException("No match found")
            }
        }

        private fun processInput(input: List<String>): List<Race> {
            val times = Regex("(\\d+)").findAll(input[0]).map { it.value.toLong() }.toList()
            val records = Regex("(\\d+)").findAll(input[1]).map { it.value.toLong() }.toList()
            return times.zip(records) { time, record -> Race(time, record) }
        }
    }
}