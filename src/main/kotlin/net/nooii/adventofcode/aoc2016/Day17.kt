package net.nooii.adventofcode.aoc2016

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.CryptoTool
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.PointDirection
import java.awt.Point

class Day17 {

    private data class State(
        val point: Point,
        val path: String
    )

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2016).loadStrings("Day17Input").first()
            val paths = solution(input)
            part1(paths)
            part2(paths)
        }

        private fun part1(paths: List<String>) {
            println(paths.first())
        }

        private fun part2(paths: List<String>) {
            println(paths.last().length)
        }

        private fun solution(input: String): List<String> {
            val max = 3
            val open = "bcdef"
            var states = setOf(State(Point(0, 0), ""))
            val paths = mutableListOf<String>()
            while (states.isNotEmpty()) {
                val nextStates = mutableSetOf<State>()
                for (state in states) {
                    if (state.point.x == max && state.point.y == max) {
                        paths.add(state.path)
                        continue
                    }
                    val hash = CryptoTool.md5hash(input + state.path).take(4)
                    for ((index, letter) in hash.withIndex()) {
                        if (letter in open) {
                            val direction = when (index) {
                                0 -> PointDirection.UP
                                1 -> PointDirection.DOWN
                                2 -> PointDirection.LEFT
                                3 -> PointDirection.RIGHT
                                else -> continue
                            }
                            val nextPoint = direction.next(state.point)
                            if (nextPoint.x < 0 || nextPoint.y < 0 || nextPoint.x > max || nextPoint.y > max) {
                                continue
                            }
                            nextStates.add(State(nextPoint, state.path + direction.letter()))
                        }
                    }
                }
                states = nextStates
            }
            return paths
        }
    }
}