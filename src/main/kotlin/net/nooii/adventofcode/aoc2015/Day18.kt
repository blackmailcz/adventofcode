package net.nooii.adventofcode.aoc2015

import net.nooii.adventofcode.helpers.*

object Day18 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2015).loadStrings("Day18Input")
        val points = processInput(input)
        part1(points)
        part2(points)
    }

    private fun part1(initialPoints: PointMap<Boolean>) {
        var points = initialPoints
        repeat(100) {
            val nextPoints = PointMap<Boolean>(points.width, points.height)
            for (point in points.keys) {
                val onNeighbors = PointDirectionDiagonal.entries.count {
                    val neighbor = it.next(point)
                    points.containsKey(neighbor) && points[neighbor]
                }
                nextPoints[point] = if (points[point]) {
                    onNeighbors == 2 || onNeighbors == 3
                } else {
                    onNeighbors == 3
                }
            }
            points = nextPoints
        }
        println(points.values.count { it })
    }

    private fun part2(initialPoints: PointMap<Boolean>) {
        var points = initialPoints.copy().apply {
            this[Point(0, 0)] = true
            this[Point(width - 1, 0)] = true
            this[Point(0, height - 1)] = true
            this[Point(width - 1, height - 1)] = true
        }
        repeat(100) {
            val nextPoints = PointMap<Boolean>(points.width, points.height)
            for (point in points.keys) {
                if (points.isCorner(point)) {
                    nextPoints[point] = true
                    continue
                }
                val onNeighbors = PointDirectionDiagonal.entries.count {
                    val neighbor = it.next(point)
                    points.containsKey(neighbor) && points[neighbor]
                }
                nextPoints[point] = if (points[point]) {
                    onNeighbors == 2 || onNeighbors == 3
                } else {
                    onNeighbors == 3
                }
            }
            points = nextPoints
        }
        println(points.values.count { it })
    }

    private fun <T : Any> PointMap<T>.isCorner(point: Point): Boolean {
        return (point.x == 0 || point.x == this.width - 1) && (point.y == 0 || point.y == this.height - 1)
    }

    private fun processInput(input: List<String>): PointMap<Boolean> {
        return PointMap.filled(input.first().length, input.size) { x, y -> input[y][x] == '#' }
    }
}
