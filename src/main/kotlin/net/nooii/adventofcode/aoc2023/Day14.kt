package net.nooii.adventofcode.aoc2023

import net.nooii.adventofcode.helpers.*
import net.nooii.adventofcode.helpers.PointDirection.*
import java.awt.Point

class Day14 {

    private class Area(
        val width: Int,
        val height: Int,
        var rocks: Set<Point>,
        val blockers: Set<Point>
    ) {
        fun isInRange(point: Point): Boolean {
            return point.x in 0 until width && point.y in 0 until height
        }

        fun copy(): Area {
            return Area(width, height, rocks.toMutableSet(), blockers)
        }
    }

    private data class State(
        val direction: PointDirection,
        val rocks: Set<Point>
    )

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2023).loadStrings("Day14Input")
            val area = processInput(input)
            val blockersMap = computeBlockersMap(area)
            part1(area.copy(), blockersMap)
            // Runtime ~ 450 ms
            part2(area.copy(), blockersMap)
        }

        private fun part1(area: Area, blockersMap: NNMap<PointDirection, Set<Point>>) {
            moveRocks(area, UP, blockersMap[UP])
            println(computeLoad(area))
        }

        private fun part2(area: Area, blockersMap: NNMap<PointDirection, Set<Point>>) {
            val spin = listOf(UP, LEFT, DOWN, RIGHT)
            val states = mutableNNMapOf<State, Long>()
            var i = 0L
            val max = 1_000_000_000L * spin.size
            var repetitionFound = false
            while (i < max) {
                val direction = spin[(i % 4).toInt()]
                moveRocks(area, direction, blockersMap[direction])
                val state = State(direction, area.rocks)
                if (!repetitionFound && states.containsKey(state)) {
                    repetitionFound = true
                    val cycleSize = i - states[state]
                    i = max - (max - i) % cycleSize
                } else {
                    states[state] = i
                    i++
                }
            }
            println(computeLoad(area))
        }

        private fun computeLoad(area: Area): Int {
            return area.rocks.sumOf { area.height - it.y }
        }

        private fun moveRocks(area: Area, direction: PointDirection, blockers: Set<Point>) {
            val nextRocks = mutableSetOf<Point>()
            for (blocker in blockers) {
                var i = 1
                val scanDirection = direction.mirror()
                var point = blocker
                while (true) {
                    val candidate = scanDirection.next(point)
                    if (candidate in blockers || !area.isInRange(candidate)) {
                        break
                    }
                    if (candidate in area.rocks) {
                        nextRocks.add(scanDirection.next(blocker, i))
                        i++
                    }
                    point = candidate
                }
            }
            area.rocks = nextRocks
        }

        private fun computeBlockers(area: Area, direction: PointDirection): Set<Point> {
            val edgeBlockers = when (direction) {
                UP -> IntRange(0, area.width - 1).map { Point(it, -1) }
                LEFT -> IntRange(0, area.height - 1).map { Point(-1, it) }
                RIGHT -> IntRange(0, area.height - 1).map { Point(area.width, it) }
                DOWN -> IntRange(0, area.width - 1).map { Point(it, area.height) }
            }
            return area.blockers + edgeBlockers
        }

        private fun computeBlockersMap(area: Area): NNMap<PointDirection, Set<Point>> {
            return PointDirection.entries.associateWith { computeBlockers(area, it) }.nn()
        }

        private fun processInput(input: List<String>): Area {
            val width = input.first().length
            val height = input.size
            val rocks = mutableSetOf<Point>()
            val blockers = mutableSetOf<Point>()
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val point = Point(x, y)
                    when (input[y][x]) {
                        'O' -> rocks.add(point)
                        '#' -> blockers.add(point)
                    }
                }
            }
            return Area(width, height, rocks, blockers)
        }
    }
}