package net.nooii.adventofcode.aoc2023

import net.nooii.adventofcode.aoc2023.Day23.TileType.*
import net.nooii.adventofcode.helpers.*
import java.awt.Point
import java.util.*
import kotlin.math.max

class Day23 {

    private sealed interface TileType {
        data object Empty : TileType
        data object Wall : TileType
        data class Slide(val direction: PointDirection) : TileType
    }

    private data class Poi(
        val point: Point,
    ) {
        val connections: MutableMap<Poi, Int> = mutableMapOf()
    }

    private data class State(
        val visited: Set<Point>,
        val from: Poi,
        val distance: Int
    ) : Comparable<State> {
        override fun compareTo(other: State): Int {
            return other.distance.compareTo(this.distance)
        }
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2023).loadStrings("Day23Input")
            val pointMap = processInput(input)
            val start = Point(1, 0)
            val end = Point(pointMap.width - 2, pointMap.height - 1)
            val part1output = mutableSetOf<Int>()
            // Runtime ~ 5 seconds
            part1(pointMap, start, end, output = part1output)
            println(part1output.max())
            // Runtime ~ 42 seconds
            part2(pointMap, start, end)
        }

        private fun part1(
            pointMap: PointMap<TileType>,
            start: Point,
            end: Point,
            output: MutableSet<Int>,
            visited: MutableSet<Point> = mutableSetOf(),
            initialDistance: Int = 0,
        ) {
            var points = setOf(start)
            var distance = initialDistance
            while (points.isNotEmpty()) {
                val nextPoints = mutableSetOf<Point>()
                for (point in points) {
                    if (point in visited) {
                        continue
                    }
                    visited.add(point)
                    if (point == end) {
                        output.add(distance)
                    }
                    val nextValid = mutableListOf<Point>()
                    for (direction in PointDirection.entries) {
                        val next = direction.next(point)
                        val filter = when {
                            !pointMap.isInRange(next) -> false
                            pointMap[next] is Empty -> true
                            pointMap[next] is Wall -> false
                            pointMap[next] is Slide -> (pointMap[next] as Slide).direction == direction
                            else -> false
                        }
                        if (filter) {
                            nextValid.add(next)
                        }
                    }
                    if (nextValid.size >= 1) {
                        nextPoints.add(nextValid.first())
                        for (nextStart in nextValid.drop(1)) {
                            part1(pointMap, nextStart, end, output, visited.toMutableSet(), distance + 1)
                        }
                    }
                }
                points = nextPoints
                distance++
            }
        }

        private fun part2(pointMap: PointMap<TileType>, start: Point, end: Point) {
            val poiMap = discoverPoi(pointMap, start, end)
            val queue = PriorityQueue<State>()
            queue.add(
                State(
                    visited = emptySet(),
                    from = poiMap[start],
                    distance = 0
                )
            )
            var maxDistance = -1
            while (queue.isNotEmpty()) {
                val state = queue.poll()
                if (state.from.point in state.visited) {
                    continue // Bad fork
                }
                if (state.from.point == end) {
                    maxDistance = max(maxDistance, state.distance)
                    continue
                }
                for ((neighbor, distance) in state.from.connections) {
                    queue.add(
                        State(
                            visited = state.visited + state.from.point,
                            from = neighbor,
                            distance = state.distance + distance
                        )
                    )
                }
            }
            println(maxDistance)
        }

        private fun discoverPoi(pointMap: PointMap<TileType>, start: Point, end: Point): NNMap<Point, Poi> {
            val visited = mutableSetOf<Point>()
            var pois = setOf(start)
            val poiMap = mutableMapOf(
                start to Poi(start)
            )
            while (pois.isNotEmpty()) {
                val nextPois = mutableSetOf<Point>()
                for (poi in pois) {
                    if (poi in visited) {
                        continue
                    }
                    visited.add(poi)
                    for (neighbor in getNeighbors(poi, pointMap)) {
                        if (neighbor in visited) {
                            continue
                        }
                        var distance = 1
                        var nextPoi = neighbor
                        while (true) {
                            val subNeighbors = getNeighbors(nextPoi, pointMap)
                            if (nextPoi == end || subNeighbors.size >= 3) {
                                // POI found
                                nextPois.add(nextPoi)
                                val poiNode = poiMap.computeIfAbsent(poi) { Poi(poi) }
                                val node = poiMap.computeIfAbsent(nextPoi) { Poi(nextPoi) }
                                node.connections[poiNode] = distance
                                poiNode.connections[node] = distance
                                break
                            } else {
                                visited.add(nextPoi)
                                val nextPoint = subNeighbors.find { it !in visited }
                                if (nextPoint != null) {
                                    nextPoi = nextPoint
                                } else {
                                    // Dead end
                                    break
                                }
                            }
                            distance++
                        }
                    }
                }
                pois = nextPois
            }
            return poiMap.nn()
        }

        private fun getNeighbors(point: Point, pointMap: PointMap<TileType>): Set<Point> {
            return PointDirection.entries
                .asSequence()
                .map { it.next(point) }
                .filter { pointMap.isInRange(it) && pointMap[it] !is Wall }
                .toSet()
        }

        private fun processInput(input: List<String>): PointMap<TileType> {
            return PointMap.filled(
                width = input.first().length,
                height = input.size,
                value = { x, y ->
                    when (val symbol = input[y][x]) {
                        '.' -> Empty
                        '#' -> Wall
                        else -> Slide(PointDirection.fromArrow(symbol.toString()))
                    }
                }
            )
        }
    }
}