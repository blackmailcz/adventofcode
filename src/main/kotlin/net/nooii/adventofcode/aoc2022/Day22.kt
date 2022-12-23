package net.nooii.adventofcode.aoc2022

import net.nooii.adventofcode.helpers.*
import net.nooii.adventofcode.helpers.PointDirection.*
import java.awt.Point

class Day22 {

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
        private val cubeNet: CubeNet,
        private val sides: NonNullMap<Int, CubeSide>,
        private val instructions: List<Instruction>,
        private var side: Int,
        private var position: Point,
        private var rotation: PointDirection
    ) {

        fun execute() {
            for (instruction in instructions) {
                when (instruction) {
                    is Instruction.Move -> {
                        var moves = 0
                        var moved: Boolean
                        do {
                            val wrap = getNextWrap()
                            if (wrap.side == side && wrap.point == position) {
                                moved = false
                            } else {
                                side = wrap.side
                                position = wrap.point
                                rotation = wrap.rotation
                                moved = true
                            }
                            moves++
                        } while (moved && moves < instruction.by)
                    }
                    is Instruction.CCW -> rotation = rotation.rotateCCW()
                    is Instruction.CW -> rotation = rotation.rotateCW()
                }
            }
        }

        private fun getNextWrap(): Wrap {
            val rawNext = rotation.next(position)
            return if (rawNext in sides[side].map) {
                if (sides[side].map[rawNext]) {
                    Wrap(side, position, rotation)
                } else {
                    Wrap(side, rawNext, rotation)
                }
            } else {
                val wrap = sides[side].nextWrap(position, rotation)
                if (sides[wrap.side].map[wrap.point]) {
                    // Cant move there. Return Wrap representing the current situation
                    Wrap(side, position, rotation)
                } else {
                    wrap
                }
            }
        }

        fun getPassword(): Int {
            val pattern = cubeNet.pattern[side]
            val x = pattern.x * cubeNet.sideSize + position.x
            val y = pattern.y * cubeNet.sideSize + position.y
            val rotationScore = when (rotation) {
                RIGHT -> 0
                DOWN -> 1
                LEFT -> 2
                UP -> 3
            }
            return 1000 * (y + 1) + 4 * (x + 1) + rotationScore
        }

    }

    companion object {

        private const val SIDE_SIZE = 50
        private const val START_SIDE = 3
        /**
         *      .36
         *      .5.
         *      14.
         *      2..
         */
        private val CUBE_PATTERN = NonNullMap(
            mutableMapOf(
                3 to Point(1, 0),
                6 to Point(2, 0),
                5 to Point(1, 1),
                1 to Point(0, 2),
                4 to Point(1, 2),
                2 to Point(0, 3)
            )
        )

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2022).loadStrings("Day22Input")
            part1(input)
            part2(input)
        }

        private fun part1(input: List<String>) {
            val cube = CubeNet(
                sideSize = SIDE_SIZE,
                startSide = START_SIDE,
                pattern = CUBE_PATTERN,
                connectionMap = NonNullMap(
                    mutableMapOf(
                        1 to NonNullMap(
                            mutableMapOf(
                                LEFT to CubeConnection(4, RIGHT),
                                DOWN to CubeConnection(2, UP),
                                RIGHT to CubeConnection(4, LEFT),
                                UP to CubeConnection(2, DOWN)
                            )
                        ),
                        2 to NonNullMap(
                            mutableMapOf(
                                LEFT to CubeConnection(2, RIGHT),
                                DOWN to CubeConnection(1, UP),
                                RIGHT to CubeConnection(2, LEFT),
                                UP to CubeConnection(1, DOWN)
                            )
                        ),
                        3 to NonNullMap(
                            mutableMapOf(
                                LEFT to CubeConnection(6, RIGHT),
                                DOWN to CubeConnection(5, UP),
                                RIGHT to CubeConnection(6, LEFT),
                                UP to CubeConnection(4, DOWN)
                            )
                        ),
                        4 to NonNullMap(
                            mutableMapOf(
                                LEFT to CubeConnection(1, RIGHT),
                                DOWN to CubeConnection(3, UP),
                                RIGHT to CubeConnection(1, LEFT),
                                UP to CubeConnection(5, DOWN)
                            )
                        ),
                        5 to NonNullMap(
                            mutableMapOf(
                                LEFT to CubeConnection(5, RIGHT),
                                DOWN to CubeConnection(4, UP),
                                RIGHT to CubeConnection(5, LEFT),
                                UP to CubeConnection(3, DOWN)
                            )
                        ),
                        6 to NonNullMap(
                            mutableMapOf(
                                LEFT to CubeConnection(3, RIGHT),
                                DOWN to CubeConnection(6, UP),
                                RIGHT to CubeConnection(3, LEFT),
                                UP to CubeConnection(6, DOWN)
                            )
                        )
                    )
                )
            )
            solution(cube, input)
        }

        private fun part2(input: List<String>) {
            val cube = CubeNet(
                sideSize = SIDE_SIZE,
                startSide = START_SIDE,
                pattern = CUBE_PATTERN,
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
            solution(cube, input)
        }

        private fun solution(cube: CubeNet, input: List<String>) {
            val maze = parseInput(cube, input)
            maze.execute()
            // Result computation
            println(maze.getPassword())
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