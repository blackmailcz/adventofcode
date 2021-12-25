package net.nooii.adventofcode2021

import net.nooii.adventofcode2021.helpers.InputLoader
import java.awt.Point

/**
 * Created by Nooii on 25.12.2021
 */
class Day25 {

    private class Cucumbers(
        val w : Int,
        val h : Int,
        val east : MutableSet<Point>,
        val south : MutableSet<Point>
    ) {
        fun isFree(point : Point) = point !in east && point !in south

        fun nextEast(cucumber : Point) = Point((cucumber.x + 1) % w, cucumber.y)
        fun nextSouth(cucumber : Point) = Point(cucumber.x, (cucumber.y + 1) % h)
    }

    companion object {

        @JvmStatic
        fun main(args : Array<String>) {
            val input = InputLoader().loadStrings("Day25Input")
            val cucumbers = processInput(input)
            part1(cucumbers)
        }

        private fun part1(cucumber : Cucumbers) {
            var steps = 0
            do { steps++ } while (step(cucumber) > 0)
            println(steps)
        }

        private fun step(cucumbers : Cucumbers) : Long {
            // Move east first
            var moves = 0L
            with(cucumbers) {
                val nextEast = east.map { c ->
                    val next = nextEast(c)
                    if (isFree(next)) {
                        moves++
                        next
                    } else {
                        c
                    }
                }
                east.clear()
                east.addAll(nextEast)
                val nextSouth = south.map { c ->
                    val next = nextSouth(c)
                    if (isFree(next)) {
                        moves++
                        next
                    } else {
                        c
                    }
                }
                south.clear()
                south.addAll(nextSouth)
                return moves
            }
        }

        private fun processInput(input : List<String>) : Cucumbers {
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