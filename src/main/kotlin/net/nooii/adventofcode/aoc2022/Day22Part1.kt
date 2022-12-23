package net.nooii.adventofcode.aoc2022

import net.nooii.adventofcode.helpers.*
import java.awt.Point

class Day22Part1 {

    private sealed class Instruction {
        class Move(val by: Int) : Instruction()
        object CCW : Instruction()
        object CW : Instruction()
    }

    private class Maze(
        val map: PointMap<Boolean>,
        val instructions: List<Instruction>,
        var position: Point,
        var rotation: PointDirection
    )

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2022).loadStrings("Day22Input")
            val maze = parseInput(input)
            part1(maze)
        }

        private fun part1(maze: Maze) {
            for (instruction in maze.instructions) {
                when (instruction) {
                    is Instruction.Move -> {
                        var moves = 0
                        var moved: Boolean
                        do {
                            val next = getNextPoint(maze)
                            if (maze.map[next]) {
                                moved = false
                            } else {
                                maze.position = next
                                moved = true
                            }
                            moves++
                        } while (moved && moves < instruction.by)
                    }
                    is Instruction.CCW -> maze.rotation = maze.rotation.rotateCCW()
                    is Instruction.CW -> maze.rotation = maze.rotation.rotateCW()
                }
            }
            val rotationScore = when (maze.rotation) {
                PointDirection.RIGHT -> 0
                PointDirection.DOWN -> 1
                PointDirection.LEFT -> 2
                PointDirection.UP -> 3
            }
            println(
                1000 * (maze.position.y + 1) + 4 * (maze.position.x + 1) + rotationScore
            )
        }

        private fun getNextPoint(maze: Maze): Point {
            val rawNext = maze.rotation.next(maze.position)
            return if (rawNext in maze.map) {
                rawNext
            } else when (maze.rotation) {
                PointDirection.UP -> maze.map.keys.filter { it.x == maze.position.x }.maxByOrNull { it.y }!!
                PointDirection.DOWN -> maze.map.keys.filter { it.x == maze.position.x }.minByOrNull { it.y }!!
                PointDirection.LEFT -> maze.map.keys.filter { it.y == maze.position.y }.maxByOrNull { it.x }!!
                PointDirection.RIGHT -> maze.map.keys.filter { it.y == maze.position.y }.minByOrNull { it.x }!!
            }
        }

        private fun parseInput(input: List<String>): Maze {
            val (mapLines, instructionsLines) = input.splitByEmptyLine()
            val map = parseMap(mapLines)
            parseInstructions(instructionsLines.first())
            return Maze(
                map = map,
                instructions = parseInstructions(instructionsLines.first()),
                position = map.keys.filter { it.y == 0 }.minByOrNull { it.x }!!,
                rotation = PointDirection.RIGHT
            )
        }

        private fun parseMap(lines: List<String>): PointMap<Boolean> {
            val width = lines.maxOf { it.length }
            val height = lines.size
            val map = PointMap<Boolean>(width, height)
            for ((y, line) in lines.withIndex()) {
                for ((x, char) in line.withIndex()) {
                    val value = when (char) {
                        '.' -> false
                        '#' -> true
                        else -> continue
                    }
                    map[Point(x, y)] = value
                }
            }
            return map
        }

        private fun parseInstructions(line: String): List<Instruction> {
            return Regex("([LR])|(\\d+)").findAll(line).map {
                when (it.value) {
                    "L" -> Instruction.CCW
                    "R" -> Instruction.CW
                    else -> Instruction.Move(it.value.toInt())
                }
            }.toList()
        }
    }
}