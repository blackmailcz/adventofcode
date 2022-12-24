package net.nooii.adventofcode.aoc2022

import net.nooii.adventofcode.helpers.*
import net.nooii.adventofcode.helpers.PointDirection.*
import java.awt.Point

class Day24 {

    private class Blizzard(
        val direction: PointDirection
    )

    private class Area(
        val width: Int,
        val height: Int,
        val start: Point,
        val end: Point,
        val walls: Set<Point>,
        var blizzard: NonNullMap<Point, MutableSet<Blizzard>>
    ) {

        fun blow() {
            val nextBlizzard = NonNullMap<Point, MutableSet<Blizzard>>()
            for ((point, blizzards) in blizzard) {
                for (blizzard in blizzards) {
                    val next = nextBlizzardPoint(point, blizzard.direction)
                    if (next !in nextBlizzard) {
                        nextBlizzard[next] = mutableSetOf()
                    }
                    nextBlizzard[next].add(blizzard)
                }
            }
            blizzard = nextBlizzard
        }

        private fun nextBlizzardPoint(point: Point, direction: PointDirection): Point {
            val rawNext = direction.next(point)
            return if (rawNext in walls) {
                when (direction) {
                    LEFT -> Point(width - 2, point.y)
                    RIGHT -> Point(1, point.y)
                    UP -> Point(point.x, height - 2)
                    DOWN -> Point(point.x, 1)
                }
            } else {
                rawNext
            }
        }

        fun getPhaseX(time: Int): Int {
            return time % (width - 2)
        }

        fun getPhaseY(time: Int): Int {
            return time % (height - 2)
        }
    }

    private data class State(
        val point: Point,
        val phaseX: Int,
        val phaseY: Int
    )

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2022).loadStrings("Day24Input")
            part1(parseInput(input))
            part2(parseInput(input))
        }

        private fun part1(area: Area) {
            val time = solution(area, area.start, area.end, 0)
            println(time)
        }

        private fun part2(area: Area) {
            // We have to stop for 1 time unit in the destination state before going back again.
            val t1 = solution(area, area.start, area.end, 0)
            val t2 = solution(area, area.end, area.start, t1 + 1)
            val t3 = solution(area, area.start, area.end, t2 + 1)
            println(t3)
        }

        private fun solution(area: Area, from: Point, to: Point, t: Int): Int {
            var time = t
            var states = setOf(
                State(from, area.getPhaseX(t), area.getPhaseX(t))
            )
            while (states.isNotEmpty()) {
                val phaseX = area.getPhaseX(time)
                val phaseY = area.getPhaseY(time)
                area.blow()
                val nextStates = mutableSetOf<State>()
                for (state in states) {
                    if (state.point == to) {
                        return time
                    }
                    for (dir in PointDirection.values()) {
                        val next = dir.next(state.point)
                        if (next !in area.blizzard && next !in area.walls && next.x in 0 until area.width && next.y in 0 until area.height) {
                            nextStates.add(State(next, phaseX, phaseY))
                        }
                    }
                    if (state.point !in area.blizzard) {
                        nextStates.add(State(state.point, phaseX, phaseY))
                    }
                }
                states = nextStates
                time++
            }
            error("Can't reach destination")
        }

        private fun parseInput(input: List<String>): Area {
            val startX = input.first().indexOfFirst { it == '.' }
            val endX = input.last().indexOfFirst { it == '.' }
            val walls = mutableSetOf<Point>()
            val blizzard = NonNullMap<Point, MutableSet<Blizzard>>()
            for ((y, line) in input.withIndex()) {
                for ((x, char) in line.withIndex()) {
                    val point = Point(x, y)
                    when (char) {
                        '#' -> walls.add(point)
                        '.' -> continue
                        else -> {
                            if (point !in blizzard) {
                                blizzard[point] = mutableSetOf()
                            }
                            blizzard[point].add(Blizzard(fromSymbol(char)))
                        }
                    }
                }
            }

            return Area(
                width = input.first().length,
                height = input.size,
                start = Point(startX, 0),
                end = Point(endX, input.size - 1),
                walls = walls,
                blizzard = blizzard
            )
        }

        private fun fromSymbol(char: Char): PointDirection {
            return when (char) {
                '>' -> RIGHT
                '<' -> LEFT
                'v' -> DOWN
                '^' -> UP
                else -> error("Invalid symbol")
            }
        }
    }
}