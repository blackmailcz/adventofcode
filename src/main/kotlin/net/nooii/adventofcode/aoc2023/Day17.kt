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
        val directions: List<PointDirection>,
        val score: Int
    ) : Comparable<RouteNode> {

        constructor(start: Point, score: Int = 0) : this(listOf(start), emptyList(), score)

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
            // Runtime ~ 57 seconds
            part2(pointMap)
        }

        private fun part1(pointMap: PointMap<Int>) {
            val startPoint = Point(0, 0)
            val endPoint = Point(pointMap.width - 1, pointMap.height - 1)
            val cache = mutableSetOf<Set<Point>>() // We need to store visited points forming the last 3 edges
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
                    if (node.directions.lastOrNull() == direction.mirror()) {
                        continue
                    }
                    // Cannot visit the same point twice
                    if (connection in node.path) {
                        continue
                    }

                    val nextPath = node.path + connection
                    val nextDirections = node.directions + direction

                    // Check last 4 directions. If the direction has not changed, this move is invalid
                    if (nextDirections.size > 3 && nextDirections.takeLast(4).toSet().size == 1) {
                        continue
                    }

                    // Cache last 4 or 5 points (works with 4, not 100% sure tho)
                    val cachedPoints = nextPath.takeLast(4).toSet()
                    // If the cached key already exists, this move is invalid
                    if (cachedPoints in cache) {
                        continue
                    }
                    cache.add(cachedPoints)

                    val newScore = node.score + pointMap[connection]
                    states.add(RouteNode(nextPath, nextDirections, newScore))
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
                    if (node.directions.lastOrNull() == direction.mirror()) {
                        continue
                    }
                    // Cannot visit the same point twice
                    if (connection in node.path) {
                        continue
                    }

                    val nextPath = node.path + connection
                    val nextDirections = node.directions + direction

                    // Go backwards and analyze directions
                    val lastDirection1 = nextDirections.lastOrNull()
                    if (lastDirection1 != null) {
                        val lastDirections1 = nextDirections.takeLastWhile { it == lastDirection1 }
                        // Up to 10 moves straight
                        if (lastDirections1.size > 10) {
                            continue
                        }
                        val cutDirections = nextDirections.dropLast(lastDirections1.size)
                        val lastDirection2 = cutDirections.lastOrNull()
                        if (lastDirection2 != null) {
                            val lastDirections2 = cutDirections.takeLastWhile { it == lastDirection2 }
                            // Less than 4 moves straight
                            if (lastDirections2.size < 4) {
                                continue
                            }
                            // Not sure how many points to cache. Works like this, might need +1
                            val cachedPoints = nextPath.takeLast(lastDirections1.size + lastDirections2.size).toSet()
                            // If the cached key already exists, this move is invalid
                            if (cachedPoints in cache) {
                                continue
                            }
                            cache.add(cachedPoints)
                        }
                    }

                    val newScore = node.score + pointMap[connection]
                    states.add(RouteNode(nextPath, nextDirections, newScore))
                }
            }
            error("No route found")
        }

        private fun processInput(input: List<String>): PointMap<Int> {
            return PointMap.filled(input.first().length, input.size) { x, y -> input[y][x].digitToInt() }
        }
    }
}