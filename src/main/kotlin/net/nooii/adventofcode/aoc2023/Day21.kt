package net.nooii.adventofcode.aoc2023

import net.nooii.adventofcode.helpers.*
import kotlin.math.max

class Day21 {

    /**
     * One segment of the area.
     */
    private class Area(
        val start: Point,
        val width: Int,
        val height: Int,
        val blockers: Set<Point>
    ) {
        fun isInRange(point: Point): Boolean {
            return point.x in 0 until width && point.y in 0 until height
        }
    }

    /**
     * Easier recognition of mask type.
     */
    private enum class MaskType {
        O_DOT, // Starts with O in top left corner
        DOT_O // Starts with dot in top left corner
    }

    /**
     * Cut to be performed on a mask based on given [cutType] along provided [diagonals].
     */
    private class Cut(
        val cutType: CutType,
        val diagonals: Set<PointDirectionDiagonal>
    )

    private enum class CutType {
        EXCEPT_DIAGONALS_INCLUSIVE, // Include only points not crossing the diagonals (with diagonals), starting in center
        EXCEPT_DIAGONALS_EXCLUSIVE, // Include only points not crossing the diagonals (without diagonals), starting in center
        ONLY_DIAGONALS_INCLUSIVE, // Include only points that crossed the diagonals (with diagonals), starting in center
        ONLY_DIAGONALS_EXCLUSIVE // Include only points that crossed the diagonals (without diagonals), starting in center
    }

    /**
     * Result for [countSquares].
     */
    private data class SquareCount(
        val outer: Long,
        val inner: Long
    )

    companion object {

        /**
         * Compute a mask to be laid over set of points. This mask covers one full segment of an area.
         * Depending on the mask type, the mask will be created for either even or odd parity.
         */
        private fun computeMask(area: Area, maskType: MaskType): Set<Point> {
            val visited = mutableSetOf<Point>()
            var i = 0
            var points = setOf(Point(0, 0))
            val parity = when (maskType) {
                MaskType.O_DOT -> 1L
                MaskType.DOT_O -> 0L
            }
            while (true) {
                val nextPoints = walk(area, points)
                if (i % 2L == parity) {
                    val lastVisitedCount = visited.size
                    visited.addAll(nextPoints)
                    if (visited.size == lastVisitedCount) {
                        break
                    }
                }
                points = nextPoints
                i++
            }
            return visited
        }

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2023).loadStrings("Day21Input")
            val area = processInput(input)
            part1(area)
            // Runtime ~ 500 ms
            part2(area)
        }

        private fun part1(area: Area) {
            var points: Set<Point> = setOf(area.start)
            repeat(64) {
                points = walk(area, points)
            }
            println(points.size)
        }

        private fun part2(area: Area) {
            /*
             * Assuming the following:
             * - The grid is square
             * - There exists a direct unblocked path from the center to the edge of the grid
             * - The number of steps required to perform must reach edge of the segment
             */

            // Create masks for both parity types
            val oDotMask = computeMask(area, MaskType.O_DOT)
            val dotOMask = computeMask(area, MaskType.DOT_O)

            // Assuming the grid id square
            val gridSize = area.width
            val steps = 26501365L // Given in the task

            // Number of squares (+ the incomplete middle one) to reach edge of the final area
            val squaresToEdge = (steps + (gridSize / 2 + 1)) / gridSize

            val fullSquareCount = countSquares(squaresToEdge - 1)
            val fullOuterSquareMaskedCount = fullSquareCount.outer * countMask(
                area = area,
                mask = oDotMask
            )
            val fullInnerSquareMaskedCount = fullSquareCount.inner * countMask(
                area = area,
                mask = dotOMask
            )

            val sidesMaskedCount = PointDirectionDiagonal.diagonals().sumOf { diagonal ->
                computeSideMaskedCount(
                    area = area,
                    squaresToEdge = squaresToEdge,
                    diagonal = diagonal,
                    oDotMask = oDotMask,
                    dotOMask = dotOMask
                )
            }

            val diagonalsOfCorners = listOf(
                setOf(PointDirectionDiagonal.NORTH_EAST, PointDirectionDiagonal.NORTH_WEST),
                setOf(PointDirectionDiagonal.NORTH_EAST, PointDirectionDiagonal.SOUTH_EAST),
                setOf(PointDirectionDiagonal.SOUTH_EAST, PointDirectionDiagonal.SOUTH_WEST),
                setOf(PointDirectionDiagonal.NORTH_WEST, PointDirectionDiagonal.SOUTH_WEST)
            )
            val cornersMaskedCount = diagonalsOfCorners.sumOf { diagonals ->
                countMask(
                    area = area,
                    mask = dotOMask,
                    cut = Cut(
                        cutType = CutType.EXCEPT_DIAGONALS_INCLUSIVE,
                        diagonals = diagonals
                    )
                )
            }

            val total = fullOuterSquareMaskedCount + fullInnerSquareMaskedCount + sidesMaskedCount + cornersMaskedCount
            println(total)
        }

        private fun computeSideMaskedCount(
            area: Area,
            squaresToEdge: Long,
            diagonal: PointDirectionDiagonal,
            oDotMask: Set<Point>,
            dotOMask: Set<Point>
        ): Long {
            val exceptDiagonalMaskedCount = max(0, squaresToEdge - 2L) * countMask(
                area = area,
                mask = dotOMask,
                cut = Cut(
                    cutType = CutType.EXCEPT_DIAGONALS_INCLUSIVE,
                    diagonals = setOf(diagonal)
                )
            )
            val onlyDiagonalMaskedCount = max(0, squaresToEdge - 1) * countMask(
                area = area,
                mask = oDotMask,
                cut = Cut(
                    cutType = CutType.ONLY_DIAGONALS_EXCLUSIVE,
                    diagonals = setOf(diagonal.mirror())
                )
            )
            return exceptDiagonalMaskedCount + onlyDiagonalMaskedCount
        }

        /**
         * Count the number of inner and outer squares from provided [halfOfDiagonal].
         */
        private fun countSquares(halfOfDiagonal: Long): SquareCount {
            var outer = 0L
            var inner = 0L
            for (i in 1 until 2 * halfOfDiagonal) {
                val m = if (i < halfOfDiagonal) i else 2 * halfOfDiagonal - i
                val squares = 2 * m - 1
                outer += squares / 2 + 1
                inner += squares / 2
            }
            return SquareCount(outer, inner)
        }

        /**
         * Count the number of "O" points that are discovered by provided [mask].
         * Diagonal [cut] can be performed on the mask to limit the number of returned points.
         */
        private fun countMask(
            area: Area,
            mask: Set<Point>,
            cut: Cut? = null
        ): Long {
            val yRange = IntRange(0, area.height - 1)
            val xRange = IntRange(0, area.width - 1)
            val onlyDiagonals = mutableSetOf<Point>()
            val exceptDiagonals = mutableSetOf<Point>()
            val corners = nnMapOf(
                PointDirectionDiagonal.NORTH_EAST to Point(area.width - 1, 0),
                PointDirectionDiagonal.NORTH_WEST to Point(0, 0),
                PointDirectionDiagonal.SOUTH_EAST to Point(area.width - 1, area.height - 1),
                PointDirectionDiagonal.SOUTH_WEST to Point(0, area.height - 1)
            )
            val half = area.width / 2 + 1
            for (y in yRange) {
                for (x in xRange) {
                    val point = Point(x, y)
                    if (cut != null && point in mask) {
                        for ((diagonalDirection, corner) in corners) {
                            if (diagonalDirection in cut.diagonals) {
                                val distance = point.manhattanDistance(corner)
                                if (distance <= half) {
                                    onlyDiagonals.add(point)
                                }
                                if (distance < half - 1) {
                                    exceptDiagonals.add(point)
                                }
                            }
                        }
                    }
                }
            }
            val outputPoints = when (cut?.cutType) {
                null -> mask
                CutType.ONLY_DIAGONALS_INCLUSIVE -> onlyDiagonals
                CutType.EXCEPT_DIAGONALS_INCLUSIVE -> mask - exceptDiagonals
                CutType.ONLY_DIAGONALS_EXCLUSIVE -> exceptDiagonals
                CutType.EXCEPT_DIAGONALS_EXCLUSIVE -> mask - onlyDiagonals
            }
            return outputPoints.size.toLong()
        }

        /**
         * Simulate 1 step.
         */
        private fun walk(area: Area, points: Set<Point>): Set<Point> {
            val nextPoints = mutableSetOf<Point>()
            for (point in points) {
                for (direction in PointDirection.entries) {
                    val next = direction.next(point)
                    if (area.isInRange(next) && next !in area.blockers) {
                        nextPoints.add(next)
                    }
                }
            }
            return nextPoints
        }

        private fun processInput(input: List<String>): Area {
            val blockers = mutableSetOf<Point>()
            var start: Point? = null
            for (y in input.indices) {
                for (x in input[y].indices) {
                    val point = Point(x, y)
                    when (input[y][x]) {
                        '#' -> blockers.add(point)
                        'S' -> start = point
                    }
                }
            }
            return Area(start!!, input.first().length, input.size, blockers)
        }
    }
}