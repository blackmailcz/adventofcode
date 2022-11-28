package net.nooii.adventofcode.aoc2021

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import java.awt.Point

/**
 * Created by Nooii on 25.12.2021
 */
class Day25 {

    private class Cucumbers(
        val w: Int,
        val h: Int,
        val east: MutableSet<Point>,
        val south: MutableSet<Point>
    ) {
        private fun isFree(point: Point) = point !in east && point !in south
        private fun nextEast(cucumber: Point) = Point((cucumber.x + 1) % w, cucumber.y)
        private fun nextSouth(cucumber: Point) = Point(cucumber.x, (cucumber.y + 1) % h)

        private fun move(horde: MutableSet<Point>, nextMove: (Point) -> Point): Boolean {
            var moved = false
            val nextHorde = horde.map {
                val next = nextMove.invoke(it)
                if (isFree(next)) {
                    moved = true
                    next
                } else {
                    it
                }
            }
            horde.clear()
            horde.addAll(nextHorde)
            return moved
        }

        fun move(): Boolean {
            val e = move(east) { nextEast(it) }
            val s = move(south) { nextSouth(it) }
            return e || s
        }
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2021).loadStrings("Day25Input")
            val cucumbers = processInput(input)
            part1(cucumbers)
        }

        private fun part1(cucumber: Cucumbers) {
            var steps = 0
            do {
                steps++
            } while (cucumber.move())
            println(steps)
        }

        private fun processInput(input: List<String>): Cucumbers {
            val east = mutableSetOf<Point>()
            val south = mutableSetOf<Point>()
            val w = input.first().length
            val h = input.size
            for ((y, line) in input.withIndex()) {
                for ((x, char) in line.withIndex()) {
                    when (char) {
                        '>' -> east.add(Point(x, y))
                        'v' -> south.add(Point(x, y))
                    }
                }
            }
            return Cucumbers(w, h, east, south)
        }

    }

}