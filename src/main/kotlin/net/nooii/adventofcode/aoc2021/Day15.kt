package net.nooii.adventofcode.aoc2021

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import java.awt.Point
import java.util.*

/**
 * Created by Nooii on 15.12.2021
 */
class Day15 {

    private class Input(
        val sizeX: Int,
        val sizeY: Int,
        val map: List<List<Int>>
    )

    private class DistancePoint(
        x: Int,
        y: Int,
        val distance: Int
    ) : Point(x, y), Comparable<DistancePoint> {

        override fun compareTo(other: DistancePoint) = distance.compareTo(other.distance)

    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2021).loadStrings("Day15Input")
            val input1 = processInput1(input)
            println(dijkstra(input1))
            println(dijkstra(processInput2(input1)))
        }

        private fun dijkstra(input: Input): Int? {
            val queue = PriorityQueue<DistancePoint>()
            queue.add(DistancePoint(0, 0, 0))
            val directions = listOf(Pair(0, 1), Pair(0, -1), Pair(1, 0), Pair(-1, 0))
            val visited = mutableSetOf<DistancePoint>()
            while (queue.isNotEmpty()) {
                val point = queue.poll()
                if (visited.contains(point)) {
                    continue
                }
                visited.add(point)
                if (isEndPoint(point, input)) {
                    return point.distance
                }
                for ((dx, dy) in directions) {
                    if (isValidPoint(point.x + dx, point.y + dy, input)) {
                        queue.add(
                            DistancePoint(
                                x = point.x + dx,
                                y = point.y + dy,
                                distance = point.distance + input.map[point.y + dy][point.x + dx]
                            )
                        )
                    }
                }
            }
            return null
        }

        private fun isValidPoint(x: Int, y: Int, input: Input): Boolean {
            return x in 0 until input.sizeX && y in 0 until input.sizeY
        }

        private fun isEndPoint(point: Point, input: Input): Boolean {
            return point.x == input.sizeX - 1 && point.y == input.sizeY - 1
        }

        private fun processInput1(input: List<String>): Input {
            val map = input.map { line -> line.map { it.digitToInt() } }
            return Input(map.first().size, map.size, map)
        }

        private fun processInput2(input1: Input): Input {
            val map = mutableListOf<MutableList<Int>>()
            for (y in 0 until input1.sizeY * 5) {
                val cY = y / input1.sizeY
                map.add(mutableListOf())
                for (x in 0 until input1.sizeX * 5) {
                    val cX = x / input1.sizeX
                    map[y].add(shift(input1.map[y % input1.sizeY][x % input1.sizeX], (cX + cY).mod(10)))
                }
            }
            return Input(input1.sizeX * 5, input1.sizeY * 5, map)
        }

        private fun shift(v: Int, c: Int) = if (v + c > 9) v + c - 9 else v + c

    }

}