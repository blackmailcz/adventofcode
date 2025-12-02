package net.nooii.adventofcode.aoc2022

import net.nooii.adventofcode.helpers.*
import kotlin.math.max
import kotlin.math.min

object Day14 {

    private enum class Tile(val symbol: String) {
        ROCK("#"), SAND("o");

        override fun toString() = symbol
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2022).loadStrings("Day14Input")
        val start = Point(500, 0)
        part1(start, parseInput(input))
        part2(start, parseInput(input))
    }

    private fun part1(start: Point, map: MutableMap<Point, Tile>) {
        val rounds = run(start, map)
        println(rounds)
    }

    private fun part2(start: Point, map: MutableMap<Point, Tile>) {
        // The sand along with the obstacles forms a triangle
        val floorY = map.keys.maxOf { it.y } + 2
        val floorX1 = start.x - floorY
        val floorX2 = start.x + floorY
        addPath(map, "$floorX1,$floorY -> $floorX2,$floorY")
        val rounds = run(start, map)
        println(rounds)
    }

    private fun run(start: Point, map: MutableMap<Point, Tile>): Int {
        val abyss: Int = map.keys.maxOf { it.y } + 1
        var rounds = 0
        while (!tick(start, map, abyss)) {
            rounds++
        }
        return rounds
    }

    private fun tick(start: Point, map: MutableMap<Point, Tile>, abyss: Int): Boolean {
        // Check if the source is not blocked
        if (map.containsKey(start)) {
            return true
        }
        // Spawn new sand at start
        var point = start
        while (true) {
            if (point.y >= abyss) {
                return true
            }
            // Priority: Down, Down-Left, Down-Right
            val possibleNextPoints = listOf(
                PointDirection.DOWN.next(point),
                PointDirection.LEFT.next(PointDirection.DOWN.next(point)),
                PointDirection.RIGHT.next(PointDirection.DOWN.next(point)),
            )
            val nextPoint = possibleNextPoints.find { !map.containsKey(it) }
            if (nextPoint != null) {
                point = nextPoint
            } else {
                map[point] = Tile.SAND
                return false
            }
        }
    }

    private fun parseInput(input: List<String>): MutableMap<Point, Tile> {
        val map = mutableMapOf<Point, Tile>()
        for (line in input) {
            addPath(map, line)
        }
        return map
    }

    private fun addPath(map: MutableMap<Point, Tile>, line: String) {
        val path = line.split(" -> ").map { coordinates ->
            val (x, y) = coordinates.split(",").map { it.toInt() }
            Point(x, y)
        }
        path.windowed(2, 1).forEach { (from, to) ->
            val diff = from.diff(to)
            when {
                diff.x != 0 && diff.y == 0 -> {
                    for (x in min(from.x, to.x)..max(from.x, to.x)) {
                        map[Point(x, from.y)] = Tile.ROCK
                    }
                }
                diff.x == 0 && diff.y != 0 -> {
                    for (y in min(from.y, to.y)..max(from.y, to.y)) {
                        map[Point(from.x, y)] = Tile.ROCK
                    }
                }
                else -> error("Only straight paths are supported!")
            }
        }
    }

    private fun draw(start: Point, map: MutableMap<Point, Tile>) {
        val minX: Int = map.keys.minOf { it.x }
        val maxX: Int = map.keys.maxOf { it.x }
        val minY: Int = map.keys.minOf { it.y }
        val maxY: Int = map.keys.maxOf { it.y }
        for (y in minY..maxY) {
            for (x in minX..maxX) {
                val point = Point(x, y)
                print(if (point == start) "+" else map[point] ?: ".")
            }
            println()
        }
    }
}