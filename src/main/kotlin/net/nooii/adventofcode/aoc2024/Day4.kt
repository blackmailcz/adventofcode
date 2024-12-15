package net.nooii.adventofcode.aoc2024

import net.nooii.adventofcode.helpers.*
import net.nooii.adventofcode.helpers.PointDirectionDiagonal.*

class Day4 {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2024).loadStrings("Day4Input")
            val map = processInput(input)
            part1(map)
            part2(map)
        }

        private fun part1(map: PointMap<Char>) {
            val result = map.keys.sumOf { point ->
                PointDirectionDiagonal.entries.count { direction ->
                    findKeyword(map, point, direction)
                }
            }
            println(result)
        }

        private fun part2(map: PointMap<Char>) {
            val result = map.keys.count { findCross(map, it) }
            println(result)
        }

        private fun findKeyword(map: PointMap<Char>, point: Point, direction: PointDirectionDiagonal): Boolean {
            var current = point
            for (char in "XMAS") {
                if (map.isInRange(current) && map[current] == char) {
                    current = direction.next(current)
                } else {
                    return false
                }
            }
            return true
        }

        private fun findCross(map: PointMap<Char>, point: Point): Boolean {
            if (map[point] != 'A') {
                return false
            }
            // NW > SE diagonal
            val c1 = checkDiagonal(map, point, NORTH_WEST, 'M') && checkDiagonal(map, point, SOUTH_EAST, 'S')
            val c2 = checkDiagonal(map, point, NORTH_WEST, 'S') && checkDiagonal(map, point, SOUTH_EAST, 'M')
            // NE > SW diagonal
            val c3 = checkDiagonal(map, point, NORTH_EAST, 'M') && checkDiagonal(map, point, SOUTH_WEST, 'S')
            val c4 = checkDiagonal(map, point, NORTH_EAST, 'S') && checkDiagonal(map, point, SOUTH_WEST, 'M')
            // On each diagonal, at least one combination must match
            return (c1 || c2) && (c3 || c4)
        }

        private fun checkDiagonal(
            map: PointMap<Char>,
            point: Point,
            direction: PointDirectionDiagonal,
            char: Char
        ): Boolean {
            val next = direction.next(point)
            return map.isInRange(next) && map[next] == char
        }

        private fun processInput(input: List<String>): PointMap<Char> {
            return PointMap.filled(input.first().length, input.size) { x, y -> input[y][x] }
        }
    }
}