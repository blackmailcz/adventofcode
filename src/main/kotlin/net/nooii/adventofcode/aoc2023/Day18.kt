package net.nooii.adventofcode.aoc2023

import net.nooii.adventofcode.helpers.*

class Day18 {

    private class InputData(
        val instructions: List<Instruction>,
        val hexColors: List<String>
    )

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
            println(polygon.numberOfPoints)
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
            for (instruction in instructions) {
                val next = instruction.direction.next(current, instruction.steps)
                points.add(next)
                current = next
            }
            return Polygon(points)
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