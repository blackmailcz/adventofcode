package net.nooii.adventofcode.aoc2022

import net.nooii.adventofcode.helpers.*
import net.nooii.adventofcode.helpers.PointDirection.*
import java.awt.Point
import java.util.PriorityQueue

class Day24 {

    private class Blizzard(
        val direction: PointDirection,
        var point: Point
    )

    private class Area(
        val w: Int,
        val h: Int,
        val start: Point,
        val end: Point,
        val walls: Set<Point>,
        var blizzard: NonNullMap<Point, MutableSet<Blizzard>>,
        val md: NonNullMap<Point, Int>
    ) {

        fun blow() {
            val nextBlizzard = NonNullMap<Point, MutableSet<Blizzard>>()
            for ((point, blizzards) in blizzard) {
                for (blizzard in blizzards) {
                    val next = nextPoint(point, blizzard.direction)
                    if (next !in nextBlizzard) {
                        nextBlizzard[next] = mutableSetOf()
                    }
                    nextBlizzard[next].add(blizzard)
                }
            }
            blizzard = nextBlizzard
        }

        fun nextPoint(point: Point, direction: PointDirection): Point {
            val rawNext = direction.next(point)
            return if (rawNext in walls) {
                when(direction) {
                    LEFT -> Point(w - 2, point.y)
                    RIGHT -> Point(1, point.y)
                    UP -> Point(point.x, h - 2)
                    DOWN -> Point(point.x, 1)
                }
            } else {
                rawNext
            }
        }

        fun draw(you: Point?) {
            for (y in 0 until h) {
                for (x in 0 until w) {
                    val point = Point(x, y)
                    val symbol = when {
                        point == you -> "E"
                        point in blizzard -> if (blizzard[point].size > 1) blizzard[point].size else toSymbol(blizzard[point].first().direction)
                        point in walls -> "#"
                        else -> "."
                    }
                    print(symbol)
                }
                println()
            }
            println()
            println()
        }

    }

    private class Node(
        val point: Point,
        val md: Int
    ): Comparable<Node> {

        override fun compareTo(other: Node): Int {
            return md.compareTo(other.md)
        }
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2022).loadStrings("Test")
            //val input = InputLoader(AoCYear.AOC_2022).loadStrings("Day24Input")
            val area = parseInput(input)
            println(area.w)
            println(area.h)
            //solution(area)
        }

        private fun solution(area: Area) {
            var time = 0
            var q = listOf(
                Node(area.start, area.md[area.start])
            )
            area.draw(area.start)
            while (q.isNotEmpty()) {
                println("t=$time nodes=${q.size},md=${q.first().md}")
//                println("*******BLOW******")
//                println()
                area.blow()
//                area.draw(null)
//                println("****************")
                // Compute next moves
                val nextQ = mutableListOf<Node>()
                for (node in q.take(1)) {
                    if (node.point == area.end) {
                        println("END IN $time")
                        return
                    }
                    for (dir in PointDirection.values()) {
                        val next = dir.next(node.point)
                        if (next !in area.blizzard && next !in area.walls && next.x > 0 && next.y > 0) {
//                            println("===============")
//                            area.draw(node.point)
//                            area.draw(next)
//                            println("===============")
                            nextQ.add(Node(next, area.md[next]))
                        }
                    }
                    nextQ.add(Node(node.point, area.md[node.point]))
                }
                nextQ.sort()
                q = nextQ
                time++
            }
            println("NOPE")
        }

        private fun parseInput(input: List<String>): Area {
            val startX = input.first().indexOfFirst { it == '.' }
            val endX = input.last().indexOfFirst { it == '.' }
            val walls = mutableSetOf<Point>()
            val blizzard = NonNullMap<Point, MutableSet<Blizzard>>()
            for ((y, line) in input.withIndex()) {
                for ((x, char) in line.withIndex()) {
                    val point = Point(x, y)
                    when(char) {
                        '#' -> walls.add(point)
                        '.' -> continue
                        else -> {
                            if (point !in blizzard) {
                                blizzard[point] = mutableSetOf()
                            }
                            blizzard[point].add(Blizzard(fromSymbol(char), point))
                        }
                    }
                }
            }

            return Area(
                w = input.first().length,
                h = input.size,
                start = Point(startX, 0),
                end = Point(endX, input.size),
                walls = walls,
                blizzard = blizzard,
                md = precomputeMD(input.size, input.first().length, Point(endX, input.size))
            )
        }

        private fun precomputeMD(maxY: Int, maxX: Int, target: Point): NonNullMap<Point, Int> {
            val mds = NonNullMap<Point, Int>()
            for (y in 0..maxY) {
                for (x in 0..maxX) {
                    val point = Point(x, y)
                    mds[point] = point.manhattanDistance(target)
                }
            }
            return mds
        }

        private fun fromSymbol(char: Char): PointDirection {
            return when(char) {
                '>' -> RIGHT
                '<' -> LEFT
                'v' -> DOWN
                '^' -> UP
                else -> error("...")
            }
        }

        private fun toSymbol(dir: PointDirection): String {
            return when(dir) {
                LEFT -> "<"
                RIGHT -> ">"
                DOWN -> "v"
                UP -> "^"
            }
        }
    }

}