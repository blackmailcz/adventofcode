package net.nooii.adventofcode.aoc2024

import net.nooii.adventofcode.helpers.*

object Day6 {

    private data class State(
        val point: Point,
        val direction: PointDirection
    )

    private data class Area(
        val width: Int,
        val height: Int,
        val walls: Set<Point>,
        val start: Point
    ) {

        fun isInside(point: Point): Boolean {
            return point.x in 0 until width && point.y in 0 until height
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2024).loadStrings("Day6Input")
        val area = processInput(input)
        part1(area)
        // Runtime ~ 6.5 seconds
        part2(area)
    }

    private fun part1(area: Area) {
        var current: State? = State(area.start, PointDirection.UP)
        val visitedPoints = mutableSetOf<Point>()
        while (current != null) {
            visitedPoints.add(current.point)
            current = getNextState(area, current)
        }
        println(visitedPoints.size)
    }

    private fun part2(area: Area) {
        val sum = pointRange(xRange = 0 until area.width, yRange = 0 until area.height).count { point ->
            point != area.start && !area.walls.contains(point) && isLoop(area.copy(walls = area.walls + point))
        }
        println(sum)
    }

    private fun getNextState(area: Area, state: State): State? {
        var current = State(state.point, state.direction)
        val visitedDirections = mutableSetOf<PointDirection>()
        // The guard may rotate multiple times before finding a valid direction
        while (current.direction !in visitedDirections) {
            visitedDirections.add(current.direction)
            val nextPoint = current.direction.next(current.point)
            when {
                !area.isInside(nextPoint) -> return null
                nextPoint !in area.walls -> return State(nextPoint, current.direction)
                else -> current = current.copy(direction = current.direction.rotateCW())
            }
        }
        return null
    }

    private fun isLoop(area: Area): Boolean {
        var current = State(area.start, PointDirection.UP)
        val visited = mutableSetOf<State>()
        while (current !in visited) {
            visited.add(current)
            current = getNextState(area, current) ?: return false
        }
        return true
    }

    private fun processInput(input: List<String>): Area {
        val width = input.first().length
        val height = input.size
        var start = Point(-1, -1)
        val walls = mutableSetOf<Point>()
        forEachPoint(xRange = 0 until width, yRange = 0 until height) { point ->
            when (input[point.y][point.x]) {
                '^' -> start = point
                '#' -> walls.add(point)
            }
        }
        return Area(width, height, walls, start)
    }
}
