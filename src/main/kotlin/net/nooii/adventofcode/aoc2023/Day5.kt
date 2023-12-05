package net.nooii.adventofcode.aoc2023

import net.nooii.adventofcode.helpers.*

class Day5 {

    private class Shift(
        val dst: Long,
        val src: Long,
        val len: Long,
    ) {
        val srcRange = LongRange(src, src + len - 1)
    }

    private class Almanac(
        val seeds: List<Long>,
        val levels: List<List<Shift>> // "Level" is a mapping of one production step to another
    )

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2023).loadStrings("Day5Input")
            val almanac = processInput(input)
            part1(almanac)
            part2(almanac)
        }

        private fun part1(almanac: Almanac) {
            // The trivial way
            val current = almanac.seeds.toMutableList()
            for (level in almanac.levels) {
                for ((i, item) in current.withIndex()) {
                    for (shift in level) {
                        if (item in shift.src until shift.src + shift.len) {
                            current[i] = shift.dst + (item - shift.src)
                        }
                    }
                }
            }
            println(current.min())
        }

        private fun part2(almanac: Almanac) {
            // Convert seed pairs to ranges
            var seedRanges = almanac.seeds.windowed(2, 2).map { LongRange(it[0], it[0] + it[1] - 1) }
            for (level in almanac.levels) {
                // Prepare ranges for next level (named as "seed" for easier understanding, in other levels it's not actually seed anymore)
                val nextSeedRanges = mutableListOf<LongRange>()
                for (seedRange in seedRanges) {
                    // The remaining range we need to find intersections for. Initially full range
                    var remaining = seedRange
                    // Browse all the shifts and find intersections
                    for (shift in level) {
                        if (remaining.overlaps(shift.srcRange)) {
                            // Intersection found
                            val intersection = remaining.fastIntersect(shift.srcRange)
                            val from = shift.dst + (intersection.first - shift.src)
                            // We only map ${intersection.size()} elements (that's why -1, because ranges are inclusive)
                            val to = from + intersection.size() - 1
                            // Shift the intersection to the destination range
                            nextSeedRanges.add(LongRange(from, to))
                            // Cut the intersection from the remaining range
                            remaining = if (shift.srcRange.first < remaining.first) {
                                LongRange(shift.srcRange.last + 1, remaining.last)
                            } else {
                                LongRange(remaining.first, shift.srcRange.first - 1)
                            }
                            // If everything was cut out already, we can skip
                            if (remaining.size() <= 0) {
                                break
                            }
                        }
                    }
                    // Something was not fully cut out, so add the remaining range to the next level
                    if (remaining.size() > 0) {
                        nextSeedRanges.add(remaining)
                    }
                }
                // Set new ranges for next level
                seedRanges = nextSeedRanges
            }
            println(seedRanges.minOf { it.first })
        }

        private fun processInput(input: List<String>): Almanac {
            val parts = input.splitByEmptyLine()
            val seeds = parts[0].first().substringAfter(": ").split(" ").map { it.toLong() }
            val levels = mutableListOf<List<Shift>>()
            for (data in parts.drop(1)) {
                val shifts = mutableListOf<Shift>()
                for (line in data.drop(1)) {
                    val (dst, src, len) = line.split(" ").map { it.toLong() }
                    shifts.add(Shift(dst, src, len))
                }
                levels.add(shifts)
            }
            return Almanac(seeds, levels)
        }
    }
}