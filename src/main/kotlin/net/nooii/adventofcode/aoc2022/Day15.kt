package net.nooii.adventofcode.aoc2022

import net.nooii.adventofcode.helpers.*
import java.awt.Point
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Day15 {

    private class Sensor(
        val sensor: Point,
        val beacon: Point
    ) {
        private val distanceToBeacon: Int by lazy {
            sensor.manhattanDistance(beacon)
        }

        fun isInRange(point: Point): Boolean {
            return sensor.manhattanDistance(point) <= distanceToBeacon
        }

        fun computeBeaconRange(y: Int): IntRange? {
            // Get straight line distance first
            val a = sensor.manhattanDistance(Point(sensor.x, y))
            // Check if y close enough to the beacon
            if (a > distanceToBeacon) {
                return null
            }
            // Find x1 and x2
            val b = abs(distanceToBeacon - a)
            return IntRange(sensor.x - b, sensor.x + b)
        }
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2022).loadStrings("Day15Input")
            val sensors = parseInput(input)
            part1(sensors, 2_000_000)
            part2(sensors, IntRange(0, 4_000_000))
        }

        private fun part1(sensors: List<Sensor>, targetY: Int) {
            val ranges = mergeRanges(targetY, sensors)
            val beacons = sensors.map { it.beacon }.toSet().filter { it.y == targetY }
            val sum = ranges.sumOf { range ->
                (range.size() - beacons.count { it.y in range }).toLong()
            }
            println(sum)
        }

        private fun part2(sensors: List<Sensor>, limit: IntRange) {
            for (y in limit) {
                val mergedRanges = mergeRanges(y, sensors)
                val gaps = countGaps(limit, mergedRanges)
                if (gaps == 1) {
                    val x = findGapX(limit, mergedRanges) ?: continue
                    val point = Point(x, y)
                    if (sensors.none { it.isInRange(point) }) {
                        println(x * 4_000_000L + y)
                        return
                    }
                }
            }
            println("No beacon found")
        }

        private fun countGaps(target: IntRange, ranges: List<IntRange>): Int {
            val occupied = ranges.sumOf {
                if (it.overlaps(target)) {
                    IntRange(max(target.first, it.first), min(target.last, it.last)).size()
                } else {
                    0
                }
            }
            return target.size() - occupied
        }

        private fun findGapX(target: IntRange, mergedRanges: List<IntRange>): Int? {
            // To find X, it will be either start, end, or one point somewhere in between.
            val ranges = mergedRanges.filter { it.overlaps(target) }
            return when (ranges.size) {
                1 -> {
                    val range = ranges.first()
                    when {
                        range.first != target.first && range.last == target.last -> range.first
                        range.first == target.first && range.last != target.last -> range.last
                        else -> null
                    }
                }
                2 -> {
                    val (r1, r2) = ranges
                    if (r1.last == r2.first - 2) r1.last + 1 else null
                }
                else -> null
            }
        }

        private fun mergeRanges(y: Int, sensors: List<Sensor>): List<IntRange> {
            val ranges = sensors.mapNotNull { it.computeBeaconRange(y) }.sortedBy { it.first }
            val merged = mutableListOf<IntRange>()
            var current: IntRange? = null
            for ((i, range) in ranges.withIndex()) {
                when {
                    current == null -> current = range
                    i < ranges.size - 1 && range.first in current || current.last == range.first -> {
                        current = IntRange(min(current.first, range.first), max(current.last, range.last))
                    }
                    else -> {
                        merged.add(current)
                        current = range
                    }
                }
            }
            return merged
        }

        private fun parseInput(input: List<String>): List<Sensor> {
            return input.map { line ->
                val data = Regex("Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)")
                    .captureFirstMatch(line) { it.toInt() }
                Sensor(Point(data[0], data[1]), Point(data[2], data[3]))
            }
        }
    }
}