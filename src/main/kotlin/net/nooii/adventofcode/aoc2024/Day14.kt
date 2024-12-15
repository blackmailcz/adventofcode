package net.nooii.adventofcode.aoc2024

import net.nooii.adventofcode.helpers.*

class Day14 {

    private data class Robot(
        val position: Point,
        val velocity: Point
    )

    companion object {

        private const val WIDTH = 101
        private const val HEIGHT = 103

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2024).loadStrings("Day14Input")
            val robots = processInput(input)
            part1(robots)
            // Runtime ~ 1 second
            part2(robots)
        }

        private fun part1(robots: List<Robot>) {
            val nextRobots = move(robots, 100)
            val product = computeQuadrants()
                .map { (qx, qy) ->
                    nextRobots.count { it.position.x in qx && it.position.y in qy }.toLong()
                }
                .product()
            println(product)
        }

        private fun part2(robots: List<Robot>) {
            // There is no deterministic solution to this problem, let's assume how a Christmas tree might look like
            var i = 0
            val limit = WIDTH * HEIGHT
            var nextRobots = robots
            do {
                i++
                nextRobots = move(nextRobots, 1)
                if (findTreeTop(nextRobots)) {
                    print(i)
                    return
                }
            } while (i <= limit)
            error("No solution found")
        }

        /**
         * Searches for a pattern resembling the top of a Christmas tree in the given robot positions.
         *
         * This function analyzes the positions of robots to determine if they form a pattern
         * that looks like the top of a Christmas tree. It does this by grouping robot positions
         * by their y-coordinates and then checking for a specific pattern in consecutive rows.
         */
        private fun findTreeTop(robots: List<Robot>): Boolean {
            val positions = robots.map { it.position }.toSet()
            val positionsByY = buildMap<Int, MutableSet<Int>> {
                for (position in positions) {
                    computeIfAbsent(position.y) { mutableSetOf() }.add(position.x)
                }
            }
            return (0 until HEIGHT)
                .windowed(3, 1)
                .any { yRange ->
                    val rows = yRange.mapNotNull { positionsByY[it] }.takeIf { it.size == 3 }
                    detectTreeTopOfSize3(rows)
                }
        }

        /**
         * Detects if a tree top pattern of size 3 is present in the given rows of integers.
         *
         * The function looks for a pattern resembling the top of a Christmas tree:
         *     #
         *    ###
         *   #####
         */
        private fun detectTreeTopOfSize3(rows: List<Set<Int>>?): Boolean {
            val (first, second, third) = rows ?: return false
            for (x in first.drop(2).dropLast(2)) {
                if ((x - 1..x + 1).all { it in second } && (x - 2..x + 2).all { it in third }) {
                    return true
                }
            }
            return false
        }

        private fun computeQuadrants(): List<Pair<IntRange, IntRange>> {
            val x1 = 0 until WIDTH / 2
            val x2 = (WIDTH / 2) + 1 until WIDTH
            val y1 = 0 until HEIGHT / 2
            val y2 = (HEIGHT / 2) + 1 until HEIGHT
            return listOf(x1 to y1, x2 to y1, x1 to y2, x2 to y2)
        }

        private fun move(robots: List<Robot>, steps: Int): List<Robot> {
            return robots.map { robot ->
                robot.copy(
                    position = Point(
                        (robot.position.x + robot.velocity.x * steps).mod(WIDTH),
                        (robot.position.y + robot.velocity.y * steps).mod(HEIGHT)
                    )
                )
            }
        }

        private fun processInput(input: List<String>): List<Robot> {
            val regex = Regex("p=(-?\\d+),(-?\\d+) v=(-?\\d+).(-?\\d+)")
            return input.map { line ->
                val (x, y, vx, vy) = regex.captureFirstMatch(line) { it.toInt() }
                Robot(Point(x, y), Point(vx, vy))
            }
        }
    }
}