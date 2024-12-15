package net.nooii.adventofcode.aoc2016

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.Point
import net.nooii.adventofcode.helpers.captureFirstMatch
import kotlin.math.min

class Day8 {

    private sealed interface Instruction {

        class Rect(val x: Int, val y: Int) : Instruction
        class RotateRow(val y: Int, val by: Int) : Instruction
        class RotateColumn(val x: Int, val by: Int) : Instruction
    }

    companion object {

        private const val WIDTH = 50
        private const val HEIGHT = 6

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2016).loadStrings("Day8Input")
            val instructions = processInput(input)
            val solution = solution(instructions)
            part1(solution)
            part2(solution)
        }

        private fun part1(points: Set<Point>) {
            println(points.size)
        }

        private fun part2(points: Set<Point>) {
            for (y in 0 until HEIGHT) {
                for (x in 0 until WIDTH) {
                    if (points.contains(Point(x, y))) {
                        print('#')
                    } else {
                        print(' ')
                    }
                }
                println()
            }
        }

        private fun solution(instructions: List<Instruction>): Set<Point> {
            val points = mutableSetOf<Point>()
            for (instruction in instructions) {
                when (instruction) {
                    is Instruction.Rect -> {
                        for (y in 0 until min(instruction.y, HEIGHT)) {
                            for (x in 0 until min(instruction.x, WIDTH)) {
                                points.add(Point(x, y))
                            }
                        }
                    }
                    is Instruction.RotateRow -> {
                        val rowPoints = points.filter { it.y == instruction.y }.toSet()
                        points.removeAll(rowPoints)
                        for (rowPoint in rowPoints) {
                            points.add(Point((rowPoint.x + instruction.by) % WIDTH, instruction.y))
                        }
                    }
                    is Instruction.RotateColumn -> {
                        val columnPoints = points.filter { it.x == instruction.x }.toSet()
                        points.removeAll(columnPoints)
                        for (columnPoint in columnPoints) {
                            points.add(Point(instruction.x, (columnPoint.y + instruction.by) % HEIGHT))
                        }
                    }
                }
            }
            return points
        }

        private fun processInput(input: List<String>): List<Instruction> {
            val regex = Regex("(rotate|rect) (row|column|\\d+).*?(\\d+)(?: by )?(\\d+)?")
            return input.map { line ->
                val (operation, p1, p2, p3) = regex.captureFirstMatch(line)
                when (operation) {
                    "rect" -> Instruction.Rect(p1.toInt(), p2.toInt())
                    "rotate" -> when (p1) {
                        "row" -> Instruction.RotateRow(p2.toInt(), p3.toInt())
                        "column" -> Instruction.RotateColumn(p2.toInt(), p3.toInt())
                        else -> error("Invalid rotation: $p1")
                    }
                    else -> error("Invalid operation: $operation")
                }
            }
        }
    }
}