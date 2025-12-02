package net.nooii.adventofcode.aoc2022

import net.nooii.adventofcode.helpers.*
import net.nooii.adventofcode.helpers.PointDirectionDiagonal.*
import kotlin.math.abs

object Day23 {

    private class Game(
        var elves: Set<Point>,
        val movementDirs: List<PointDirectionDiagonal>,
        var firstMovementDir: Int,
    )

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2022).loadStrings("Day23Input")
        val elves = parseInput(input)
        val movementDirs = mutableListOf(
            NORTH, SOUTH, WEST, EAST
        )
        part1(Game(elves, movementDirs, 0))
        // Runtime ~ 1250 ms
        part2(Game(elves, movementDirs, 0))
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
        val proposed = MutableNNMap<Point, Point>()
        val forbidden = mutableSetOf<Point>()
        val notMoving = mutableSetOf<Point>()
        for (elf in game.elves) {
            when (val next = getNextPoint(game, elf)) {
                elf, in forbidden -> notMoving.add(elf)
                in proposed -> {
                    notMoving.add(elf)
                    notMoving.add(proposed[next])
                    proposed.remove(next)
                    forbidden.add(next)
                }
                else -> proposed[next] = elf
            }
        }
        game.elves = notMoving + proposed.keys
        game.firstMovementDir = (game.firstMovementDir + 1) % game.movementDirs.size
    }

    private fun getNextPoint(game: Game, elf: Point): Point {
        if (PointDirectionDiagonal.entries.none { it.next(elf) in game.elves }) {
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
            else -> error("Unsupported movement direction")
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