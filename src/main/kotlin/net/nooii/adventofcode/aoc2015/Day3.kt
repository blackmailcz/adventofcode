package net.nooii.adventofcode.aoc2015

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.Point
import net.nooii.adventofcode.helpers.PointDirection

class Day3 {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2015).loadStrings("Day3Input")
            val directions = processInput(input.first())
            part1(directions)
            part2(directions)
        }

        private fun part1(directions: List<PointDirection>) {
            var point = Point(0, 0)
            val visited = mutableSetOf(point)
            for (direction in directions) {
                point = direction.next(point)
                visited.add(point)
            }
            println(visited.size)
        }

        private fun part2(directions: List<PointDirection>) {
            var santa = Point(0, 0)
            var robot = Point(0, 0)
            val visited = mutableSetOf(santa, robot)
            var isSantasTurn = true
            for (direction in directions) {
                if (isSantasTurn) {
                    santa = direction.next(santa)
                    visited.add(santa)
                } else {
                    robot = direction.next(robot)
                    visited.add(robot)
                }
                isSantasTurn = !isSantasTurn
            }
            println(visited.size)
        }

        private fun processInput(line: String): List<PointDirection> {
            return line.map { PointDirection.fromArrow(it.toString()) }
        }
    }
}