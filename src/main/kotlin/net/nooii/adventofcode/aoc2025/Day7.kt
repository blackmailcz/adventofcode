package net.nooii.adventofcode.aoc2025

import net.nooii.adventofcode.helpers.*

object Day7 {

    private data class Area(
        val start: Point,
        val width: Int,
        val height: Int,
        val splitters: Set<Point>
    )

    private data class WeightedBeam(
        val x: Int,
        val weight: Long
    )

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2025).loadStrings("Day7Input")
        val area = processInput(input)
        part1(area)
        part2(area)
    }

    private fun part1(area: Area) {
        var splitCount = 0
        var beams = setOf(area.start.x)
        for (row in area.start.y + 1 until area.height) {
            val nextBeams = mutableSetOf<Int>()
            for (beam in beams) {
                if (Point(beam, row) in area.splitters) {
                    nextBeams.add(beam - 1)
                    nextBeams.add(beam + 1)
                    splitCount++
                } else {
                    nextBeams.add(beam)
                }
            }
            beams = nextBeams
        }
        println(splitCount)
    }

    private fun part2(area: Area) {
        var weightedBeams = listOf(WeightedBeam(area.start.x, 1L))
        for (row in area.start.y + 1 until area.height) {
            val nextBeams = mutableMapOf<Int, Long>()
            for ((beam, weight) in weightedBeams) {
                if (Point(beam, row) in area.splitters) {
                    nextBeams.add(beam - 1, weight)
                    nextBeams.add(beam + 1, weight)
                } else {
                    nextBeams.add(beam, weight)
                }
            }
            weightedBeams = nextBeams.map { (x, weight) -> WeightedBeam(x, weight) }.toList()
        }
        println(weightedBeams.sumOf { it.weight })
    }

    private fun processInput(input: List<String>): Area {
        val width = input.first().length
        val height = input.size
        var start: Point? = null
        val splitters = mutableSetOf<Point>()
        forEachPoint(0 until width, 0 until height) { point ->
            when (input[point.y][point.x]) {
                'S' -> start = point
                '^' -> splitters.add(point)
            }
        }
        return Area(start!!, width, height, splitters)
    }
}