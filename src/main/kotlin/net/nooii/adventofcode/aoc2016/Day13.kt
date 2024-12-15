package net.nooii.adventofcode.aoc2016

import net.nooii.adventofcode.helpers.*

class Day13 {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val favoriteNumber = InputLoader(AoCYear.AOC_2016).loadInts("Day13Input").first()
            part1(favoriteNumber)
            part2(favoriteNumber)
        }

        private fun part1(favoriteNumber: Int) {
            val finish = Point(31, 39)
            val result = traverse(
                start = Point(1, 1),
                traverseMode = TraverseMode.ToEnd { it == finish },
                heuristic = { it.manhattanDistance(finish).toLong() },
                nextItems = { current ->
                    PointDirection.entries
                        .map { it.next(current) }
                        .filter { it.x >= 0 && it.y >= 0 && !isWall(it, favoriteNumber) }
                        .map { ItemWithCost(it) }
                },
            )
            println(result!!.cost)
        }

        private fun part2(favoriteNumber: Int) {
            val start = Point(1, 1)
            val visited = mutableSetOf(start)
            var points = setOf(start)
            repeat(50) {
                val nextPoints = mutableSetOf<Point>()
                for (point in points) {
                    val n = PointDirection.entries
                        .map { it.next(point) }
                        .filter { it.x >= 0 && it.y >= 0 && !isWall(it, favoriteNumber) }
                    nextPoints.addAll(n)
                    visited.addAll(n)
                }
                points = nextPoints
            }
            println(visited.size)
        }

        private fun isWall(point: Point, favoriteNumber: Int): Boolean {
            // x*x + 3*x + 2*x*y + y + y*y
            val x = point.x.toLong()
            val y = point.y.toLong()
            val n = x.pow(2) + 3 * x + 2 * x * y + y + y.pow(2) + favoriteNumber
            val binary = n.toBigInteger().toString(2)
            return binary.count { it == '1' } % 2 == 1
        }
    }
}