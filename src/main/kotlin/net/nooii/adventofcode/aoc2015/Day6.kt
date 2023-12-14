package net.nooii.adventofcode.aoc2015

import net.nooii.adventofcode.helpers.*
import java.awt.Point

class Day6 {

    private enum class Action {
        ON, OFF, TOGGLE
    }

    private class Rectangle(
        val from: Point,
        val to: Point,
        val action: Action
    ) {

        fun generatePoints(): Set<Point> {
            val points = mutableSetOf<Point>()
            for (y in from.y..to.y) {
                for (x in from.x..to.x) {
                    points.add(Point(x, y))
                }
            }
            return points
        }
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2015).loadStrings("Day6Input")
            val rectangles = processInput(input)
            // Run time ~ 18 sec
            part1(rectangles)
            // Run time ~ 9 sec
            part2(rectangles)
        }

        private fun part1(rectangles: List<Rectangle>) {
            val points = mutableSetOf<Point>()
            for (rectangle in rectangles) {
                when (rectangle.action) {
                    Action.ON -> points.addAll(rectangle.generatePoints())
                    Action.OFF -> points.removeAll(rectangle.generatePoints())
                    Action.TOGGLE -> {
                        val rectanglePoints = rectangle.generatePoints()
                        val newOnPoints = rectanglePoints - points.intersect(rectanglePoints)
                        points.removeAll(rectanglePoints)
                        points.addAll(newOnPoints)
                    }
                }
            }
            println(points.size)
        }

        private fun part2(rectangles: List<Rectangle>) {
            val brightness = mutableNNMapOf<Point, Long>()
            for (rectangle in rectangles) {
                for (point in rectangle.generatePoints()) {
                    when (rectangle.action) {
                        Action.ON -> brightness.add(point, 1L)
                        Action.OFF -> {
                            brightness.add(point, -1L)
                            if (brightness[point] < 0) {
                                brightness[point] = 0
                            }
                        }
                        Action.TOGGLE -> brightness.add(point, 2L)
                    }
                }
            }
            println(brightness.values.sum())
        }

        private fun processInput(input: List<String>): List<Rectangle> {
            return input.map { parseRectangle(it) }
        }

        private fun parseRectangle(line: String): Rectangle {
            val regex = Regex("(turn on|turn off|toggle) (\\d+),(\\d+) through (\\d+),(\\d+)")
            val matches = regex.captureFirstMatch(line)
            val action = when (matches[0]) {
                "turn on" -> Action.ON
                "turn off" -> Action.OFF
                "toggle" -> Action.TOGGLE
                else -> throw IllegalArgumentException("Invalid action: ${matches[0]}")
            }
            return Rectangle(
                from = Point(matches[1].toInt(), matches[2].toInt()),
                to = Point(matches[3].toInt(), matches[4].toInt()),
                action = action
            )
        }
    }
}