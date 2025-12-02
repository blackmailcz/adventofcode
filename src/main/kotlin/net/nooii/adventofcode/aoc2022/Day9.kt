package net.nooii.adventofcode.aoc2022

import net.nooii.adventofcode.helpers.*
import kotlin.math.abs
import kotlin.math.sign

object Day9 {

    private class Rope(numberOfTails: Int) {

        private var head = Point(0, 0)
        private val tails = MutableList(numberOfTails) { Point(0, 0) }

        fun move(direction: PointDirection) {
            head = direction.next(head)
            for ((index, tail) in tails.withIndex()) {
                val previous = tails.getOrNull(index - 1) ?: head
                val diff = tail.diff(previous)
                if (abs(diff.x) > 1 || abs(diff.y) > 1) {
                    tails[index] = Point(tail.x + diff.x.sign, tail.y + diff.y.sign)
                }
            }
        }

        fun getLastTail() = tails.last()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2022).loadStrings("Day9Input")
        val directions = parseInput(input)
        solution(directions, 1)
        solution(directions, 9)
    }

    private fun solution(directions: List<PointDirection>, numberOfTails: Int) {
        val rope = Rope(numberOfTails)
        val lastTailPoints = mutableSetOf<Point>()
        directions.forEach {
            rope.move(it)
            lastTailPoints += rope.getLastTail()
        }
        println(lastTailPoints.size)
    }

    private fun parseInput(input: List<String>): List<PointDirection> {
        val directions = mutableListOf<PointDirection>()
        for (line in input) {
            val (direction, step) = line.split(" ")
            repeat(step.toInt()) {
                directions += PointDirection.fromLetter(direction)
            }
        }
        return directions
    }
}