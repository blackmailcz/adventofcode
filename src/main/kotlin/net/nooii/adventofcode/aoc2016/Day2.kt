package net.nooii.adventofcode.aoc2016

import net.nooii.adventofcode.helpers.*
import java.awt.Point

class Day2 {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2016).loadStrings("Day2Input")
            val directionsList = processInput(input)
            part1(directionsList)
            part2(directionsList)
        }

        private fun part1(directionsList: List<List<PointDirection>>) {
            solution(directionsList, createSquarePad())
        }

        private fun part2(directionsList: List<List<PointDirection>>) {
            solution(directionsList, createDiamondPad())
        }

        private fun solution(directionsList: List<List<PointDirection>>, pad: NNMap<Point, Char>) {
            val digits = StringBuilder()
            var point = pad.entries.first { it.value == '5' }.key
            for (directions in directionsList) {
                for (direction in directions) {
                    val next = direction.next(point)
                    if (next in pad.keys) {
                        point = next
                    }
                }
                digits.append(pad[point])
            }
            println(digits.toString())
        }

        private fun createSquarePad(): NNMap<Point, Char> {
            return IntRange(1, 9).associate {
                Point((it - 1) % 3, (it - 1) / 3) to it.digitToChar()
            }.nn()
        }

        private fun createDiamondPad(): NNMap<Point, Char> {
            val chars = "123456789ABCD"
            val pattern = listOf(1, 3, 5, 3, 1)
            val points = mutableListOf<Point>()
            for ((y, n) in pattern.withIndex()) {
                for (i in 0 until n) {
                    points.add(Point((pattern.size - n) / 2 + i, y))
                }
            }
            return chars.toList().zip(points).associate { (char, point) -> point to char }.nn()
        }

        private fun processInput(input: List<String>): List<List<PointDirection>> {
            return input.map { line ->
                line.map {
                    PointDirection.fromLetter(it.toString())
                }
            }
        }
    }
}