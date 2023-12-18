package net.nooii.adventofcode.aoc2023

import net.nooii.adventofcode.helpers.*
import java.awt.Point

class Day18 {

    private class InputData(
        val instructions: List<Instruction>,
        val hexColors: List<String>
    )

    private class Edge(
        val from: Point,
        val to: Point
    ) {
        fun length() = from.manhattanDistance(to)
    }

    private class Polygon(
        val points: List<Point>,
        val edges: List<Edge>
    ) {

        fun getPerimeter() = edges.sumOf { it.length().toLong() }

        fun getArea(): Long {
            // Shoelace Formula
            return (points + points.first()).windowed(2, 1).sumOf { (p1, p2) ->
                p1.x.toLong() * p2.y.toLong() - p1.y.toLong() * p2.x.toLong()
            } / 2
        }

        fun getNumberOfPoints(): Long {
            // Pick's Theorem

            // Area = InnerVertices + OuterVertices / 2 - 1
            // InnerVertices = Area - OuterVertices / 2 + 1
            val area = getArea()
            val perimeter = getPerimeter()

            val innerPointCount = area - perimeter / 2 + 1
            return innerPointCount + perimeter
        }
    }

    private data class Instruction(
        val direction: PointDirection,
        val steps: Int,
    )

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2023).loadStrings("Day18Input")
            val inputData = processInput(input)
            part1(inputData)
            part2(inputData)
        }

        private fun part1(inputData: InputData) {
            solution(inputData.instructions)
        }

        private fun part2(inputData: InputData) {
            val instructions = inputData.hexColors.map { parseHexToInstruction(it) }
            solution(instructions)
        }

        private fun solution(instructions: List<Instruction>) {
            val polygon = createPolygon(instructions)
            val points = polygon.getNumberOfPoints()
            println(points)
        }

        private fun parseHexToInstruction(hex: String): Instruction {
            val direction = when (hex.last().digitToInt()) {
                0 -> PointDirection.RIGHT
                1 -> PointDirection.DOWN
                2 -> PointDirection.LEFT
                3 -> PointDirection.UP
                else -> error("Invalid direction: $hex")
            }
            val intString = hex.take(5).toInt(16)
            return Instruction(direction, intString)
        }

        private fun createPolygon(instructions: List<Instruction>): Polygon {
            var current = Point(0, 0)
            val points = mutableListOf<Point>()
            val edges = mutableListOf<Edge>()
            for (instruction in instructions) {
                val next = instruction.direction.next(current, instruction.steps)
                edges.add(Edge(Point(current.x, current.y), Point(next.x, next.y)))
                points.add(next)
                current = next
            }
            return Polygon(points, edges)
        }

        private fun processInput(input: List<String>): InputData {
            val regex = Regex("(\\w) (\\d+) \\(#([0-9a-f]{6})\\)")
            val instructions = mutableListOf<Instruction>()
            val hexColors = mutableListOf<String>()
            for (line in input) {
                val (direction, steps, hexColor) = regex.captureFirstMatch(line)
                instructions.add(Instruction(PointDirection.fromLetter(direction), steps.toInt()))
                hexColors.add(hexColor)
            }
            return InputData(instructions, hexColors)
        }
    }
}