package net.nooii.adventofcode.aoc2022

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.NonNullMap
import net.nooii.adventofcode.helpers.PointDirectionDiagonal
import net.nooii.adventofcode.helpers.PointDirectionDiagonal.*
import java.awt.Point
import kotlin.math.abs

class Day23 {

    private class Game(
        var elves: Set<Point>,
        val movementDirs: List<PointDirectionDiagonal>,
        val allDirs: List<PointDirectionDiagonal>,
        var firstMovementDir: Int,
    )

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2022).loadStrings("Day23Input")
            val elves = parseInput(input)
            val movementDirs = mutableListOf(
                NORTH, SOUTH, WEST, EAST
            )
            val allDirs = mutableListOf(
                NORTH_WEST, NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST
            )
            part1(Game(elves, movementDirs, allDirs, 0))
            // Runtime ~ 1250 ms
            part2(Game(elves, movementDirs, allDirs, 0))
        }

        private fun part1(game: Game) {
            repeat(10) {
                round(game)
            }
            val minX: Int = game.elves.minOf { it.x }
            val maxX: Int = game.elves.maxOf { it.x }
            val minY: Int = game.elves.minOf { it.y }
            val maxY: Int = game.elves.maxOf { it.y }
            val result = (abs(maxY - minY) + 1) * (abs(maxX - minX) + 1) - game.elves.size
            println(result)
        }

        private fun part2(game: Game) {
            // The key of speeding up is to use only hash sets / maps for elves ... each contains operation is O(1)
            // Could've run even faster by optimizing getNextPoint() and by caching dirsToCheck() lists..
            var rounds = 0
            while (true) {
                rounds++
                val oldElves = game.elves
                round(game)
                if (oldElves == game.elves) {
                    break
                }
            }
            println(rounds)
        }

        private fun round(game: Game) {
            val proposed = NonNullMap<Point, Point>()
            val forbidden = mutableSetOf<Point>()
            val nextPoints = mutableSetOf<Point>()
            for (elf in game.elves) {
                when (val next = getNextPoint(game, elf)) {
                    elf, in forbidden -> nextPoints.add(elf)
                    in proposed -> {
                        nextPoints.add(elf)
                        nextPoints.add(proposed[next])
                        proposed.remove(next)
                        forbidden.add(next)
                    }
                    else -> proposed[next] = elf
                }
            }
            game.elves = nextPoints + proposed.keys
            game.firstMovementDir = (game.firstMovementDir + 1) % game.movementDirs.size
        }

        private fun getNextPoint(game: Game, elf: Point): Point {
            if (game.allDirs.none { it.next(elf) in game.elves }) {
                return elf
            }
            for (i in game.movementDirs.indices) {
                val dir = (game.firstMovementDir + i) % game.movementDirs.size
                if (game.movementDirs[dir].dirsToCheck().none { it.next(elf) in game.elves }) {
                    return game.movementDirs[dir].next(elf)
                }
            }
            return elf
        }

        private fun PointDirectionDiagonal.dirsToCheck(): List<PointDirectionDiagonal> {
            return when (this) {
                NORTH -> listOf(NORTH, NORTH_EAST, NORTH_WEST)
                SOUTH -> listOf(SOUTH, SOUTH_EAST, SOUTH_WEST)
                WEST -> listOf(WEST, NORTH_WEST, SOUTH_WEST)
                EAST -> listOf(EAST, NORTH_EAST, SOUTH_EAST)
                else -> error("...")
            }
        }

        private fun parseInput(input: List<String>): Set<Point> {
            val points = mutableSetOf<Point>()
            for ((y, line) in input.withIndex()) {
                for ((x, char) in line.withIndex()) {
                    if (char == '#') {
                        points.add(Point(x, y))
                    }
                }
            }
            return points
        }
    }
}