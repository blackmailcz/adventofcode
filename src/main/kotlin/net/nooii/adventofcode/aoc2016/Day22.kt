package net.nooii.adventofcode.aoc2016

import com.github.shiguruikai.combinatoricskt.permutations
import net.nooii.adventofcode.helpers.*
import java.awt.Point
import kotlin.math.max
import kotlin.math.min

class Day22 {

    private data class Node(
        val point: Point,
        val size: Int,
        val used: Int,
        val available: Int
    ) {

        fun isWall(maxCapacity: Int): Boolean {
            return used > maxCapacity
        }
    }

    private data class PathNode(
        val point: Point,
        val holePoint: Point,
        val holeCapacity: Int
    )

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2016).loadStrings("Day22Input")
            val pointMap = processInput(input)
            part1(pointMap)
            part2(pointMap)
        }

        private fun part1(pointMap: PointMap<Node>) {
            val combinations = pointMap.values.permutations(2)
            val validPairs = combinations.count { (a, b) ->
                a.used != 0 && a.used <= b.available
            }
            println(validPairs)
        }

        private fun part2(pointMap: PointMap<Node>) {
            val goal = Point(0, 0)
            val start = pointMap.values.filter { it.point.y == 0 }.maxBy { it.point.x }
            // Prepare scenario for each direction of initial movement
            val holes = pointMap.values.filter { it.used == 0 }
            val startingPoints = mutableNNMapOf<PointDirection, Pair<Node?, Long>>()
            for (direction in PointDirection.entries) {
                startingPoints[direction] = Pair(null, Long.MAX_VALUE)
            }
            // Find the best hole for each starting point
            for (hole in holes) {
                for (direction in PointDirection.entries) {
                    val targetHolePoint = direction.next(start.point)
                    if (!isValidMove(pointMap, targetHolePoint, hole.available)) {
                        continue
                    }
                    val steps = countStepsToMoveHole(pointMap, hole, targetHolePoint) ?: Long.MAX_VALUE
                    if (steps < startingPoints[direction].second) {
                        startingPoints[direction] = Pair(hole, steps)
                    }
                }
            }
            var bestCost = Long.MAX_VALUE
            for ((startingDirection, data) in startingPoints.filterValues { it.second < Long.MAX_VALUE }) {
                val (hole, initialHoleCost) = data
                if (hole == null) {
                    continue
                }
                val result = traverse(
                    start = PathNode(
                        point = startingDirection.next(start.point),
                        holePoint = start.point,
                        holeCapacity = hole.available
                    ),
                    traverseMode = TraverseMode.ToEnd { it.point == goal },
                    nextItems = { current ->
                        buildList {
                            for (direction in PointDirection.entries) {
                                val juggleCost = juggleCost(
                                    pointMap = pointMap,
                                    point = current.point,
                                    direction = direction,
                                    hole = current.holePoint,
                                    holeCapacity = current.holeCapacity
                                )
                                if (juggleCost == null) {
                                    continue
                                }
                                val nextPathNode = PathNode(
                                    point = direction.next(current.point),
                                    holePoint = current.point,
                                    holeCapacity = hole.available
                                )
                                add(ItemWithCost(nextPathNode, juggleCost + 1))
                            }
                        }
                    }
                )
                if (result == null) {
                    continue
                }
                // Initial hole cost + 1 (to move from goal to starting position of algorithm) + cost with juggles
                val totalCost = initialHoleCost + 1 + result.cost
                bestCost = min(bestCost, totalCost)
            }
            println(bestCost)
        }

        private fun juggleCost(
            pointMap: PointMap<Node>,
            point: Point,
            direction: PointDirection,
            hole: Point,
            holeCapacity: Int
        ): Long? {
            val result = traverse(
                start = hole,
                traverseMode = TraverseMode.ToEnd { it == direction.next(point) },
                nextItems = { current ->
                    PointDirection.entries
                        .map { it.next(current) }
                        .filter {
                            isValidMove(pointMap, it, holeCapacity) && it != point
                        }
                        .map { ItemWithCost(it) }
                }
            )
            return result?.cost
        }

        private fun countStepsToMoveHole(pointMap: PointMap<Node>, hole: Node, target: Point): Long? {
            val result = traverse(
                start = hole.point,
                traverseMode = TraverseMode.ToEnd { it == target },
                nextItems = { current ->
                    PointDirection.entries
                        .map { it.next(current) }
                        .filter { isValidMove(pointMap, it, hole.available) }
                        .map { ItemWithCost(it) }
                }
            )
            return result?.cost
        }

        private fun isValidMove(pointMap: PointMap<Node>, point: Point, capacity: Int): Boolean {
            return pointMap.contains(point) && !pointMap[point].isWall(capacity)
        }

        private fun processInput(input: List<String>): PointMap<Node> {
            val underlying = mutableNNMapOf<Point, Node>()
            val regex = Regex("node-x(\\d+)-y(\\d+)\\s+(\\d+)T\\s+(\\d+)T\\s+(\\d+)T")
            var maxX = 0
            var maxY = 0
            for (line in input) {
                if (!line.contains(regex)) {
                    continue
                }
                val (x, y, size, used, available) = regex.captureFirstMatch(line)
                val point = Point(x.toInt(), y.toInt())
                underlying[point] = Node(
                    point = point,
                    size = size.toInt(),
                    used = used.toInt(),
                    available = available.toInt()
                )
                maxX = max(maxX, x.toInt())
                maxY = max(maxY, y.toInt())
            }
            return PointMap(maxX + 1, maxY + 1, underlying)
        }
    }
}