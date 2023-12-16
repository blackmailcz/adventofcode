package net.nooii.adventofcode.aoc2023

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.Axis
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.PointDirection
import net.nooii.adventofcode.helpers.PointDirection.*
import java.awt.Point

class Day16 {

    private class Area(
        val width: Int,
        val height: Int,
        val mirrors: Map<Point, Char>
    ) {
        fun isInRange(point: Point): Boolean {
            return point.x in 0 until width && point.y in 0 until height
        }
    }

    private data class State(
        val point: Point,
        val direction: PointDirection
    )

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2023).loadStrings("Day16Input")
            val area = processInput(input)
            // Runtime ~ 100 ms
            part1(area)
            // Runtime ~ 11500 ms
            part2(area)
        }

        private fun part1(area: Area) {
            val points = beam(area, State(Point(0, 0), RIGHT))
            println(points.size)
        }

        private fun part2(area: Area) {
            val states = mutableSetOf<State>()
            for (x in 0 until area.width) {
                states.add(State(Point(x, 0), DOWN))
                states.add(State(Point(x, area.height - 1), UP))
            }
            for (y in 0 until area.height) {
                states.add(State(Point(0, y), RIGHT))
                states.add(State(Point(area.width - 1, y), LEFT))
            }
            val max = states.maxOf { beam(area, it).size }
            println(max)
        }

        private fun beam(area: Area, initialState: State) = beam(area, mutableSetOf(), initialState)

        private fun beam(area: Area, visited: MutableSet<State> = mutableSetOf(), initialState: State): Set<Point> {
            var states = setOf(initialState)
            val output = mutableSetOf<Point>()
            while (states.isNotEmpty()) {
                val nextStates = mutableSetOf<State>()
                for (state in states) {
                    if (state in visited) continue
                    visited.add(state)
                    var point = state.point
                    // Find next mirror or edge
                    while (area.isInRange(point) && point !in area.mirrors) {
                        output.add(point)
                        point = state.direction.next(point)
                    }
                    // Mirror found
                    if (point in area.mirrors) {
                        output.add(point)
                        when (area.mirrors[point]!!) {
                            '|' -> {
                                when (state.direction.axis) {
                                    Axis.VERTICAL -> {
                                        output += beam(area, visited, State(state.direction.next(point), state.direction))
                                    }
                                    Axis.HORIZONTAL -> {
                                        output += beam(area, visited, State(UP.next(point), UP))
                                        output += beam(area, visited, State(DOWN.next(point), DOWN))
                                    }
                                }
                            }
                            '-' -> {
                                when (state.direction.axis) {
                                    Axis.VERTICAL -> {
                                        output += beam(area, visited, State(LEFT.next(point), LEFT))
                                        output += beam(area, visited, State(RIGHT.next(point), RIGHT))
                                    }
                                    Axis.HORIZONTAL -> {
                                        output += beam(area, visited, State(state.direction.next(point), state.direction))
                                    }
                                }
                            }
                            '/' -> {
                                output += when (state.direction) {
                                    LEFT -> beam(area, visited, State(DOWN.next(point), DOWN))
                                    RIGHT -> beam(area, visited, State(UP.next(point), UP))
                                    UP -> beam(area, visited, State(RIGHT.next(point), RIGHT))
                                    DOWN -> beam(area, visited, State(LEFT.next(point), LEFT))
                                }
                            }
                            '\\' -> {
                                output += when (state.direction) {
                                    LEFT -> beam(area, visited, State(UP.next(point), UP))
                                    RIGHT -> beam(area, visited, State(DOWN.next(point), DOWN))
                                    UP -> beam(area, visited, State(LEFT.next(point), LEFT))
                                    DOWN -> beam(area, visited, State(RIGHT.next(point), RIGHT))
                                }
                            }
                            else -> error("Invalid mirror")
                        }
                    }
                }
                states = nextStates
            }
            return output
        }

        private fun processInput(input: List<String>): Area {
            val width = input.first().length
            val height = input.size
            val mirrors = mutableMapOf<Point, Char>()
            for (y in 0 until height) {
                for (x in 0 until width) {
                    if (input[y][x] != '.') {
                        mirrors[Point(x, y)] = input[y][x]
                    }
                }
            }
            return Area(width, height, mirrors)
        }
    }
}