package net.nooii.adventofcode.aoc2023

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.PointDirection
import net.nooii.adventofcode.helpers.PointDirection.*
import net.nooii.adventofcode.helpers.PointDirectionDiagonal
import java.awt.Point
import kotlin.math.floor

class Day10 {

    private data class State(
        val position: Point,
        val direction: PointDirection
    )

    private class Area(
        val start: Point,
        val pipes: Map<Point, Pipe>
    ) {
        lateinit var startPipe: Pipe
    }

    private data class Pipe(
        val point: Point,
        val pipeType: PipeType
    )

    private enum class PipeType(val symbol: Char, val entranceDirections: List<PointDirection>) {
        VERTICAL('|', listOf(UP, DOWN)),
        HORIZONTAL('-', listOf(LEFT, RIGHT)),
        NE('L', listOf(UP, RIGHT)),
        NW('J', listOf(UP, LEFT)),
        SW('7', listOf(DOWN, LEFT)),
        SE('F', listOf(DOWN, RIGHT));

        companion object {

            fun fromSymbol(symbol: Char): PipeType? {
                return entries.find { it.symbol == symbol }
            }
        }
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2023).loadStrings("Day10Input")
            val area = processInput(input)
            part1(area)
            // Runtime ~ 700 ms
            part2(area)
        }

        private fun part1(area: Area) {
            val mainLoop = findMainLoop(area)
            println(floor(mainLoop.size / 2.0).toInt())
        }

        private data class Entrance(val points: Set<Point>) {
            lateinit var direction: PointDirection // Need equality on points in any order

            constructor(points: Set<Point>, direction: PointDirection): this(points) {
                this.direction = direction
            }
        }

        private fun part2(area: Area) {
            // The solution is to explore from top-left to bottom-right, find and process "tunnel entrances" and
            // "diagonal squeezes" along the way.
            val mainLoop = findMainLoop(area)
            val pipeMap = mapLoopToPipes(area, mainLoop)
            // Measure the area (add 1 extra line on each side to properly detect outside points)
            val yRange = IntRange(mainLoop.minOf { it.y } - 1, mainLoop.maxOf { it.y } + 1)
            val xRange = IntRange(mainLoop.minOf { it.x } - 1, mainLoop.maxOf { it.x } + 1)
            var points = mutableSetOf(Point(xRange.first, yRange.first))
            // Save visited points
            val visited = mutableSetOf<Point>()
            // Save visited tunnel entrances
            val closedEntrances = mutableSetOf<Set<Point>>()
            while (points.isNotEmpty()) {
                val nextPoints = mutableSetOf<Point>()
                for (point in points) {
                    // Visited check
                    if (point in visited) continue
                    visited.add(point)
                    // Range check
                    if (point.x !in xRange || point.y !in yRange) continue
                    // Check for diagonal squeezes
                    for (diagonal in checkDiagonalSqueezes(point, pipeMap)) {
                        nextPoints.add(diagonal)
                    }
                    for (direction in PointDirection.entries) {
                        when (val nextPoint = direction.next(point)) {
                            in visited -> continue
                            !in mainLoop -> nextPoints.add(nextPoint)
                            else -> {
                                val pipe = pipeMap[nextPoint]!!
                                if (direction in pipe.pipeType.entranceDirections) {
                                    var entrances = mutableSetOf<Entrance>()
                                    // Check if CW point in the given direction contains entrance
                                    val cwPoint = direction.rotateCW().next(pipe.point)
                                    if (pipeMap.containsKey(cwPoint) && isTunnelAccessible(listOf(pipe, pipeMap[cwPoint]!!), direction)) {
                                        entrances.add(Entrance(setOf(nextPoint, cwPoint), direction))
                                    }
                                    // Check if CCW point in the given direction contains entrance
                                    val ccwPoint = direction.rotateCCW().next(pipe.point)
                                    if (pipeMap.containsKey(ccwPoint) && isTunnelAccessible(listOf(pipeMap[ccwPoint]!!, pipe), direction)) {
                                        entrances.add(Entrance(setOf(nextPoint, ccwPoint), direction))
                                    }
                                    // Iterate discovered entrances
                                    while (entrances.isNotEmpty()) {
                                        val nextEntrances = mutableSetOf<Entrance>()
                                        for (entrance in entrances) {
                                            if (entrance.points in closedEntrances) {
                                                // Entrance is closed
                                                continue
                                            }
                                            // Move to a next "tunnel" point
                                            val nextEntrancePoints = entrance.points.map { entrance.direction.next(it) }
                                            // Check if we exit the tunnel into free point
                                            if (nextEntrancePoints.any { !pipeMap.containsKey(it) }) {
                                                // Collect all free points at the end of the tunnel
                                                for (freePoint in nextEntrancePoints.filter { !pipeMap.containsKey(it) }) {
                                                    nextPoints.add(freePoint)
                                                }
                                                // If there are any other entrances created by leaving the tunnel, they will be discovered later
                                            } else {
                                                // Move in the tunnel
                                                val straightEntrancePipes = entrance.points.map { pipeMap[entrance.direction.next(it)]!! }
                                                // Straight
                                                if (isTunnelAccessible(straightEntrancePipes, entrance.direction)) {
                                                    nextEntrances.add(Entrance(nextEntrancePoints.toSet(), entrance.direction))
                                                }
                                                // Move to sides
                                                val (ep1, ep2) = entrance.points.map { pipeMap[it]!! }
                                                // Move to side #1
                                                val sideDir1 = ep1.pipeType.entranceDirections.find { it.axis != entrance.direction.axis }
                                                if (sideDir1 != null) {
                                                    val nep1 = pipeMap[entrance.direction.next(ep1.point)]!!
                                                    if (isTunnelAccessible(setOf(ep1, nep1), sideDir1)) {
                                                        nextEntrances.add(Entrance(setOf(ep1.point, nep1.point), sideDir1))
                                                    }
                                                }
                                                // Move to side #2
                                                val sideDir2 = ep2.pipeType.entranceDirections.find { it.axis != entrance.direction.axis }
                                                if (sideDir2 != null) {
                                                    val nep2 = pipeMap[entrance.direction.next(ep2.point)]!!
                                                    if (isTunnelAccessible(setOf(ep2, nep2), sideDir2)) {
                                                        nextEntrances.add(Entrance(setOf(ep2.point, nep2.point), sideDir2))
                                                    }
                                                }
                                            }
                                            // Close entrance
                                            closedEntrances.add(entrance.points)
                                        }
                                        // Update entrances
                                        entrances = nextEntrances
                                    }
                                }
                            }
                        }
                    }
                }
                points = nextPoints
            }
            var sum = 0
            for (y in yRange) {
                for (x in xRange) {
                    val point = Point(x, y)
                    if (point !in mainLoop && point !in visited) {
                        sum++
                    }
                }
            }
            println(sum)
        }

        private fun isTunnelAccessible(pipes: Collection<Pipe>, direction: PointDirection): Boolean {
            // Determine left and right pipes
            val (pipeA, pipeB) = pipes.toList()
            // "Tunnel" should be between pipe1 and pipe2 -> p1 | tunnel | p2
            val (pipe1, pipe2) = if (pipeA.point.x == pipeB.point.x) {
                Pair(pipes.minBy { it.point.y }, pipes.maxBy { it.point.y })
            } else {
                Pair(pipes.minBy { it.point.x }, pipes.maxBy { it.point.x })
            }
            val directionAxis = direction.axis
            // Both pipes must contain a part in the given direction axis
            val bothDirectionAxes = pipes.all { pipe -> pipe.pipeType.entranceDirections.any { it.axis == directionAxis } }
            // Tunnel is not accessible if the pipes are connected
            val pipe1outputs = pipe1.pipeType.entranceDirections.map { it.next(pipe1.point) }
            val pipe2outputs = pipe2.pipeType.entranceDirections.map { it.next(pipe2.point) }
            return bothDirectionAxes && pipe1.point !in pipe2outputs && pipe2.point !in pipe1outputs
        }

        private fun checkDiagonalSqueezes(point: Point, pipeMap: Map<Point, Pipe>): Set<Point> {
            // Diagonal squeezes are possible between two free points ("x") in these configurations:
            // xL  Jx
            // 7x  xF
            if (point in pipeMap) {
                return emptySet()
            }
            val squeezes = mutableSetOf<Point>()
            // NW
            val diagNW = PointDirectionDiagonal.NORTH_WEST.next(point)
            if (diagNW !in pipeMap &&
                pipeMap[UP.next(point)]?.pipeType == PipeType.NE &&
                pipeMap[LEFT.next(point)]?.pipeType == PipeType.SW &&
                pipeMap[RIGHT.next(diagNW)]?.pipeType == PipeType.NE &&
                pipeMap[DOWN.next(diagNW)]?.pipeType == PipeType.SW) {
                squeezes.add(diagNW)
            }
            // NE
            val diagNE = PointDirectionDiagonal.NORTH_EAST.next(point)
            if (diagNE !in pipeMap &&
                pipeMap[UP.next(point)]?.pipeType == PipeType.NW &&
                pipeMap[RIGHT.next(point)]?.pipeType == PipeType.SE &&
                pipeMap[LEFT.next(diagNE)]?.pipeType == PipeType.NW &&
                pipeMap[DOWN.next(diagNE)]?.pipeType == PipeType.SE) {
                squeezes.add(diagNE)
            }
            // SE
            val diagSE = PointDirectionDiagonal.SOUTH_EAST.next(point)
            if (diagSE !in pipeMap &&
                pipeMap[DOWN.next(point)]?.pipeType == PipeType.SW &&
                pipeMap[RIGHT.next(point)]?.pipeType == PipeType.NE &&
                pipeMap[LEFT.next(diagSE)]?.pipeType == PipeType.SW &&
                pipeMap[UP.next(diagSE)]?.pipeType == PipeType.NE) {
                squeezes.add(diagSE)
            }
            // SW
            val diagSW = PointDirectionDiagonal.SOUTH_WEST.next(point)
            if (diagSW !in pipeMap &&
                pipeMap[DOWN.next(point)]?.pipeType == PipeType.SE &&
                pipeMap[LEFT.next(point)]?.pipeType == PipeType.NW &&
                pipeMap[RIGHT.next(diagSW)]?.pipeType == PipeType.SE &&
                pipeMap[UP.next(diagSW)]?.pipeType == PipeType.NW) {
                squeezes.add(diagSW)
            }
            return squeezes
        }

        private fun mapLoopToPipes(area: Area, loop: Set<Point>): Map<Point, Pipe> {
            // Eliminate "S" symbol and create a map of all pipes in the loop
            val pipes = loop.mapNotNull { area.pipes[it] }.toSet() + area.startPipe
            return pipes.associateBy { it.point }
        }

        private fun findMainLoop(area: Area): Set<Point> {
            val mainLoops = mutableSetOf<Set<Point>>()
            outer@
            for (startDirection in PointDirection.entries) {
                val mainLoop = mutableListOf(area.start)
                var state = State(area.start, startDirection)
                while (state.direction.next(state.position) != area.start) {
                    state = tryEnterPipe(area, state)
                        ?: continue@outer // Cannot enter the pipe or the pipe is not connected to any other pipe
                    mainLoop.add(state.position)
                }
                mainLoops.add(mainLoop.toSet())
                // Save start pipe
                val secondStartPipeDirection = PointDirection.entries.first { it.next(area.start) == mainLoop.last() }
                val pipeType = PipeType.entries.first { startDirection in it.entranceDirections && secondStartPipeDirection in it.entranceDirections }
                area.startPipe = Pipe(area.start, pipeType)
            }
            check(mainLoops.size == 1)
            return mainLoops.first()
        }

        private fun tryEnterPipe(area: Area, state: State): State? {
            val pipe = area.pipes[state.direction.next(state.position)]
                ?: return null // No pipe ahead
            val (first, second) = pipe.pipeType.entranceDirections
            return when (state.position) {
                first.next(pipe.point) -> State(pipe.point, second)
                second.next(pipe.point) -> State(pipe.point, first)
                else -> null // Cannot enter the pipe it is facing
            }
        }

        private fun processInput(input: List<String>): Area {
            var start: Point? = null
            val pipes = mutableMapOf<Point, Pipe>()
            for (y in input.indices) {
                for (x in input[y].indices) {
                    val point = Point(x, y)
                    val pipeType = PipeType.fromSymbol(input[y][x])
                    when {
                        pipeType != null -> pipes[point] = Pipe(point, pipeType)
                        input[y][x] == 'S' -> start = point
                    }
                }
            }
            check(start != null)
            return Area(start, pipes)
        }
    }
}
