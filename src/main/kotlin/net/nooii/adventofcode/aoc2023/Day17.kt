package net.nooii.adventofcode.aoc2023

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.PointDirection
import net.nooii.adventofcode.helpers.PointMap
import java.awt.Point
import java.util.*

class Day17 {

    private class RouteNode(
        val path: List<Point>,
        val direction: PointDirection? = null,
        val consecutiveDirections: List<Int> = listOf(0),
        val score: Int = 0
    ) : Comparable<RouteNode> {

        constructor(start: Point, score: Int = 0) : this(listOf(start), score = score)

        val current: Point
            get() = path.last()

        override fun compareTo(other: RouteNode): Int {
            return this.score.compareTo(other.score)
        }

        override fun toString(): String {
            return path.toString()
        }
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2023).loadStrings("Day17Input")
            val pointMap = processInput(input)
            // Runtime ~ 2 seconds
            part1(pointMap)
            // Runtime ~ 44 seconds
            part2(pointMap)
        }

        private fun part1(pointMap: PointMap<Int>) {
            val startPoint = Point(0, 0)
            val endPoint = Point(pointMap.width - 1, pointMap.height - 1)
            val cache = mutableSetOf<Set<Point>>()
            val states = PriorityQueue<RouteNode>()
            states.add(RouteNode(startPoint))
            while (states.isNotEmpty()) {
                val node = states.poll()
                if (node.current == endPoint) {
                    println(node.score)
                    return
                }
                for (direction in PointDirection.entries) {
                    val connection = direction.next(node.current)
                    // Cannot go out of map
                    if (!pointMap.isInRange(connection)) {
                        continue
                    }
                    // Cannot go backwards
                    if (node.direction == direction.mirror()) {
                        continue
                    }
                    // Cannot visit the same point twice
                    if (connection in node.path) {
                        continue
                    }

                    val nextConsecutive = if (node.direction == null || node.direction == direction) {
                        node.consecutiveDirections.toMutableList().apply {
                            this[size - 1] = last() + 1
                        }
                    } else {
                        node.consecutiveDirections + listOf(1)
                    }

                    val next = RouteNode(
                        path = node.path + connection,
                        direction = direction,
                        consecutiveDirections = nextConsecutive,
                        score = node.score + pointMap[connection]
                    )

                    // Check last 4 directions. If the direction has not changed, this move is invalid
                    if (nextConsecutive.last() == 4) {
                        continue
                    }

                    // Cache last 4 or 5 points (works with 4, not 100% sure tho)
                    val cachedPoints = next.path.takeLast(4).toSet()
                    // If the cached key already exists, this move is invalid
                    if (cachedPoints in cache) {
                        continue
                    }
                    cache.add(cachedPoints)

                    states.add(next)
                }
            }
            error("No route found")
        }

        private fun part2(pointMap: PointMap<Int>) {
            val startPoint = Point(0, 0)
            val endPoint = Point(pointMap.width - 1, pointMap.height - 1)
            val cache = mutableSetOf<Set<Point>>()
            val states = PriorityQueue<RouteNode>()
            states.add(RouteNode(startPoint))
            while (states.isNotEmpty()) {
                val node = states.poll()
                if (node.current == endPoint) {
                    println(node.score)
                    return
                }
                for (direction in PointDirection.entries) {
                    val connection = direction.next(node.current)
                    // Cannot go out of map
                    if (!pointMap.isInRange(connection)) {
                        continue
                    }
                    // Cannot go backwards
                    if (node.direction == direction.mirror()) {
                        continue
                    }
                    // Cannot visit the same point twice
                    if (connection in node.path) {
                        continue
                    }

                    val nextConsecutive = if (node.direction == null || node.direction == direction) {
                        node.consecutiveDirections.toMutableList().apply {
                            this[size - 1] = last() + 1
                        }
                    } else {
                        node.consecutiveDirections + listOf(1)
                    }

                    val next = RouteNode(
                        path = node.path + connection,
                        direction = direction,
                        consecutiveDirections = nextConsecutive,
                        score = node.score + pointMap[connection]
                    )

                    if (nextConsecutive.last() > 10) {
                        continue
                    }
                    if (nextConsecutive.size >= 2) {
                        val secondLastDirectionCount = nextConsecutive[nextConsecutive.size - 2]
                        if (secondLastDirectionCount < 4) {
                            continue
                        }
                        // Not sure how many points to cache. Works like this, might need +1
                        val cachedPoints = next.path.takeLast(nextConsecutive.last() + secondLastDirectionCount).toSet()
                        // If the cached key already exists, this move is invalid
                        if (cachedPoints in cache) {
                            continue
                        }
                        cache.add(cachedPoints)
                    }
                    states.add(next)
                }
            }
            error("No route found")
        }

        private fun processInput(input: List<String>): PointMap<Int> {
            return PointMap.filled(input.first().length, input.size) { x, y -> input[y][x].digitToInt() }
        }
    }
}