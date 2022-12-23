package net.nooii.adventofcode.aoc2022

import net.nooii.adventofcode.helpers.*
import net.nooii.adventofcode.helpers.PointDirection.*
import java.awt.Point

class Day22Part2 {

    private class CubeConnection(
        val side: Int,
        val direction: PointDirection
    )

    private class Wrap(
        val side: Int,
        val point: Point,
        val rotation: PointDirection
    )

    private class CubeNet(
        val sideSize: Int,
        val startSide: Int,
        val pattern: NonNullMap<Int, Point>,
        val connectionMap: NonNullMap<Int, NonNullMap<PointDirection, CubeConnection>>
    )

    private sealed class Instruction {
        class Move(val by: Int) : Instruction()
        object CCW : Instruction()
        object CW : Instruction()
    }

    private class CubeSide(
        val id: Int,
        val sideSize: Int,
        val map: PointMap<Boolean>,
        private val connections: NonNullMap<PointDirection, CubeConnection>
    ) {

        fun nextWrap(point: Point, direction: PointDirection): Wrap {
            val connection = connections[direction]
            val max = sideSize - 1

            return when (direction) {
                LEFT -> {
                    when (connection.direction) {
                        LEFT -> Wrap(connection.side, Point(0, max - point.y), RIGHT)
                        RIGHT -> Wrap(connection.side, Point(max, point.y), LEFT)
                        DOWN -> Wrap(connection.side, Point(max - point.y, max), UP)
                        UP -> Wrap(connection.side, Point(point.y, 0), DOWN)
                    }
                }
                RIGHT -> {
                    when (connection.direction) {
                        LEFT -> Wrap(connection.side, Point(0, point.y), RIGHT)
                        RIGHT -> Wrap(connection.side, Point(max, max - point.y), LEFT)
                        UP -> Wrap(connection.side, Point(max - point.y, 0), DOWN)
                        DOWN -> Wrap(connection.side, Point(point.y, max), UP)
                    }
                }
                DOWN -> {
                    when (connection.direction) {
                        LEFT -> Wrap(connection.side, Point(0, max - point.x), RIGHT)
                        RIGHT -> Wrap(connection.side, Point(max, point.x), LEFT)
                        UP -> Wrap(connection.side, Point(point.x, 0), DOWN)
                        DOWN -> Wrap(connection.side, Point(max - point.x, max), UP)
                    }
                }
                UP -> {
                    when (connection.direction) {
                        LEFT -> Wrap(connection.side, Point(0, point.x), RIGHT)
                        RIGHT -> Wrap(connection.side, Point(max, max - point.x), LEFT)
                        UP -> Wrap(connection.side, Point(max - point.x, 0), DOWN)
                        DOWN -> Wrap(connection.side, Point(point.x, max), UP)
                    }
                }
            }
        }
    }

    private class Maze(
        val cubeNet: CubeNet,
        val sides: NonNullMap<Int, CubeSide>,
        val instructions: List<Instruction>,
        var side: Int,
        var position: Point,
        var rotation: PointDirection
    )

    companion object {

        private val CUBE = CubeNet(
            /**
             *      .36
             *      .5.
             *      14.
             *      2..
             */
            sideSize = 50,
            startSide = 3,
            pattern = NonNullMap(
                mutableMapOf(
                    3 to Point(1, 0),
                    6 to Point(2, 0),
                    5 to Point(1, 1),
                    1 to Point(0, 2),
                    4 to Point(1, 2),
                    2 to Point(0, 3)
                )
            ),
            connectionMap = NonNullMap(
                mutableMapOf(
                    1 to NonNullMap(
                        mutableMapOf(
                            LEFT to CubeConnection(3, LEFT),
                            DOWN to CubeConnection(2, UP),
                            RIGHT to CubeConnection(4, LEFT),
                            UP to CubeConnection(5, LEFT)
                        )
                    ),
                    2 to NonNullMap(
                        mutableMapOf(
                            LEFT to CubeConnection(3, UP),
                            DOWN to CubeConnection(6, UP),
                            RIGHT to CubeConnection(4, DOWN),
                            UP to CubeConnection(1, DOWN)
                        )
                    ),
                    3 to NonNullMap(
                        mutableMapOf(
                            LEFT to CubeConnection(1, LEFT),
                            DOWN to CubeConnection(5, UP),
                            RIGHT to CubeConnection(6, LEFT),
                            UP to CubeConnection(2, LEFT)
                        )
                    ),
                    4 to NonNullMap(
                        mutableMapOf(
                            LEFT to CubeConnection(1, RIGHT),
                            DOWN to CubeConnection(2, RIGHT),
                            RIGHT to CubeConnection(6, RIGHT),
                            UP to CubeConnection(5, DOWN)
                        )
                    ),
                    5 to NonNullMap(
                        mutableMapOf(
                            LEFT to CubeConnection(1, UP),
                            DOWN to CubeConnection(4, UP),
                            RIGHT to CubeConnection(6, DOWN),
                            UP to CubeConnection(3, DOWN)
                        )
                    ),
                    6 to NonNullMap(
                        mutableMapOf(
                            LEFT to CubeConnection(3, RIGHT),
                            DOWN to CubeConnection(5, RIGHT),
                            RIGHT to CubeConnection(4, RIGHT),
                            UP to CubeConnection(2, DOWN)
                        )
                    )
                )
            )
        )

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2022).loadStrings("Day22Input")
            val maze = parseInput(CUBE, input)
            execute(maze)
        }

        private fun execute(maze: Maze) {
            for (instruction in maze.instructions) {
                when (instruction) {
                    is Instruction.Move -> {
                        var moves = 0
                        var moved: Boolean
                        do {
                            val wrap = getNextPoint(maze)
                            if (wrap.side == maze.side && wrap.point == maze.position) {
                                moved = false
                            } else {
                                maze.side = wrap.side
                                maze.position = wrap.point
                                maze.rotation = wrap.rotation
                                moved = true
                            }
                            moves++
                        } while (moved && moves < instruction.by)
                    }
                    is Instruction.CCW -> maze.rotation = maze.rotation.rotateCCW()
                    is Instruction.CW -> maze.rotation = maze.rotation.rotateCW()
                }
            }

            // Result computation
            val pattern = maze.cubeNet.pattern[maze.side]
            val x = pattern.x * maze.cubeNet.sideSize + maze.position.x
            val y = pattern.y * maze.cubeNet.sideSize + maze.position.y
            val rotationScore = when (maze.rotation) {
                RIGHT -> 0
                DOWN -> 1
                LEFT -> 2
                UP -> 3
            }
            println(1000 * (y + 1) + 4 * (x + 1) + rotationScore)
        }

        private fun getNextPoint(maze: Maze): Wrap {
            val rawNext = maze.rotation.next(maze.position)
            return if (rawNext in maze.sides[maze.side].map) {
                if (maze.sides[maze.side].map[rawNext]) {
                    Wrap(maze.side, maze.position, maze.rotation)
                } else {
                    Wrap(maze.side, rawNext, maze.rotation)
                }
            } else {
                val wrap = maze.sides[maze.side].nextWrap(maze.position, maze.rotation)
                if (maze.sides[wrap.side].map[wrap.point]) {
                    // Cant move there. Return Wrap representing the current situation
                    Wrap(maze.side, maze.position, maze.rotation)
                } else {
                    wrap
                }
            }
        }

        private fun parseInput(cubeNet: CubeNet, input: List<String>): Maze {
            val (mapLines, instructionsLines) = input.splitByEmptyLine()
            val instructions = parseInstructions(instructionsLines.first())
            return parseMaze(cubeNet, mapLines, instructions)
        }

        private fun parseMaze(cubeNet: CubeNet, lines: List<String>, instructions: List<Instruction>): Maze {
            return Maze(
                cubeNet = cubeNet,
                sides = NonNullMap.fromMap(
                    IntRange(1, 6).associateWith { parseSide(cubeNet, lines, it) }
                ),
                instructions = instructions,
                position = Point(0, 0),
                side = cubeNet.startSide,
                rotation = RIGHT
            )
        }

        private fun parseSide(cubeNet: CubeNet, lines: List<String>, sideId: Int): CubeSide {
            val pattern = cubeNet.pattern[sideId]
            val map = PointMap<Boolean>(cubeNet.sideSize, cubeNet.sideSize)
            val chunkLines = lines.subList(pattern.y * cubeNet.sideSize, (pattern.y + 1) * cubeNet.sideSize)
            for ((y, chunkLine) in chunkLines.withIndex()) {
                val chunkChars = chunkLine.substring(pattern.x * cubeNet.sideSize, (pattern.x + 1) * cubeNet.sideSize)
                for ((x, char) in chunkChars.withIndex()) {
                    val value = when (char) {
                        '.' -> false
                        '#' -> true
                        else -> continue
                    }
                    map[Point(x, y)] = value
                }
            }
            return CubeSide(sideId, cubeNet.sideSize, map, cubeNet.connectionMap[sideId])
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