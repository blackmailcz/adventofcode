package net.nooii.adventofcode.aoc2017

import net.nooii.adventofcode.helpers.*

class Day19 {
    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2017).loadStrings("Day19Input")
            val map = processInput(input)
            solution(map)
        }

        private fun solution(map: Map<Point, Char>) {
            var direction = PointDirection.DOWN
            var current = map.filterKeys { it.y == 0 }.keys.first()
            val collectedLetters = mutableListOf<Char>()
            var steps = 0
            do {
                when (val char = map[current]) {
                    '+' -> {
                        val cw = direction.rotateCW()
                        val ccw = direction.rotateCCW()
                        direction = when {
                            cw.axis == Axis.VERTICAL && cw.next(current) in map && map[cw.next(current)] != '-' -> cw
                            cw.axis == Axis.HORIZONTAL && cw.next(current) in map && map[cw.next(current)] != '|' -> cw
                            ccw.axis == Axis.VERTICAL && ccw.next(current) in map && map[ccw.next(current)] != '-' -> ccw
                            ccw.axis == Axis.HORIZONTAL && ccw.next(current) in map && map[ccw.next(current)] != '|' -> ccw
                            else -> error("Invalid junction")
                        }
                    }
                    in 'A'..'Z' -> collectedLetters.add(char!!)
                }
                steps++
                current = direction.next(current)
            } while (current in map)
            println(collectedLetters.joinToString(""))
            println(steps)
        }

        private fun processInput(input: List<String>): Map<Point, Char> {
            val width = input.maxOfOrNull { it.length }!!
            return buildMap {
                forEachPoint(0 until width, input.indices) { point ->
                    input.getOrNull(point.y)?.getOrNull(point.x)?.takeIf { it != ' ' }?.let { put(point, it) }
                }
            }
        }
    }
}