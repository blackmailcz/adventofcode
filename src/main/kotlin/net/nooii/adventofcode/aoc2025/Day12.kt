package net.nooii.adventofcode.aoc2025

import net.nooii.adventofcode.helpers.*

object Day12 {

    private class Problem(
        val gridWidth: Int,
        val gridHeight: Int,
        val shapes: NNMap<Shape, Int>
    ) {

        override fun toString(): String {
            return "Grid $gridWidth x $gridHeight\nShapes:\n$shapes"
        }
    }

    private data class Shape(
        val width: Int,
        val height: Int,
        val rows: List<Long>
    ) {

        val area by lazy {
            rows.sumOf { row -> row.toBinString(width).count { it == '1' } }
        }

        private fun copyRotatedCW(): Shape {
            val newRows = MutableList(width) { 0L }
            for (y in 0 until height) {
                val row = rows[y]
                for (x in 0 until width) {
                    if ((row ushr x) and 1L == 1L) {
                        newRows[x] = newRows[x] or (1L shl (height - 1 - y))
                    }
                }
            }
            // Note that height and widths are swapped because of the rotation
            return copy(rows = newRows)
        }

        private fun copyVerticallyFlipped(): Shape {
            return copy(rows = rows.reversed())
        }

        private fun copyHorizontallyFlipped(): Shape {
            return copy(rows = rows.map { it.toBinString(width).reversed().toLong(2) })
        }

        fun createAllVariants(): Set<Shape> {
            val rotations = listOf(
                copy(),
                copyRotatedCW(),
                copyRotatedCW().copyRotatedCW(),
                copyRotatedCW().copyRotatedCW().copyRotatedCW()
            )
            return rotations
                .flatMap { rotation ->
                    setOf(
                        rotation,
                        rotation.copyVerticallyFlipped(),
                        rotation.copyHorizontallyFlipped()
                    )
                }
                .toSet()
        }

        override fun toString(): String {
            return rows.joinToString("\n") { row -> row.toBinString(width) }
        }
    }

    private class Grid(
        val gridWidth: Int,
        val gridHeight: Int
    ) {

        val rows = MutableList(gridHeight) { 0L }

        fun canPlace(shape: Shape, x: Int, y: Int): Boolean {
            // Check grid limit
            if (x + shape.width > gridWidth || y + shape.height > gridHeight) {
                return false
            }
            // Check collision
            for (sy in 0 until shape.height) {
                // Shift bits to the left by X and also make sure we don't exceed the grid horizontal limits
                if ((rows[y + sy] and ((shape.rows[sy] shl x) and (1L shl gridWidth) - 1L)) != 0L) {
                    return false
                }
            }
            return true
        }

        fun place(shape: Shape, x: Int, y: Int) {
            // Placement is a simple OR
            for (sy in 0 until shape.height) {
                rows[y + sy] = rows[y + sy] or (shape.rows[sy] shl x)
            }
        }

        fun unplace(shape: Shape, x: Int, y: Int) {
            // XOR the previously placed bits to return to original state
            for (sy in 0 until shape.height) {
                rows[y + sy] = rows[y + sy] xor (shape.rows[sy] shl x)
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2025).loadStrings("Day12Input")
        val problems = processInput(input)
        part1(problems)
    }

    private fun part1(problems: List<Problem>) {
        val sum = problems.count { isSolvable(it) }
        println(sum)
    }

    private fun isSolvable(problem: Problem): Boolean {
        // Check, if the area sum is enough to fit in the grid
        // This helps to eliminate obviously wrong problems
        val areaSum = problem.shapes.entries.sumOf { (shape, count) ->
            shape.area * count
        }
        if (areaSum > problem.gridWidth * problem.gridHeight) {
            return false
        }

        // Construct grid
        val grid = Grid(problem.gridWidth, problem.gridHeight)

        // Precompute variants
        val variants = problem.shapes.keys.associateWith { it.createAllVariants() }.nn()

        // Prepare all shapes
        val shapes = problem.shapes.flatMap { (shape, count) ->
            listOf(shape).repeat(count)
        }

        // Recursive solution
        // Start with placing first variant, if it fits, place first variant of another shape, and so on
        // If something cannot be placed, proceed to next variant
        fun rec(shapeIndex: Int = 0): Boolean {
            if (shapeIndex == shapes.size) {
                // No more shapes left, we have a solution
                return true
            }
            val shape = shapes[shapeIndex]
            for (variant in variants[shape]) {
                val yRange = 0..(grid.gridHeight - variant.height)
                val xRange = 0..(grid.gridWidth - variant.width)
                for (y in yRange) {
                    for (x in xRange) {
                        if (grid.canPlace(variant, x, y)) {
                            grid.place(variant, x, y)
                            if (rec(shapeIndex + 1)) {
                                return true
                            }
                            grid.unplace(variant, x, y)
                        }
                    }
                }
            }
            return false
        }

        return rec()
    }

    private fun processInput(input: List<String>): List<Problem> {
        val problemRows = input.splitByEmptyLine().last()
        val shapeRows = input.dropLast(problemRows.size + 1).splitByEmptyLine().map { it.drop(1) }
        val shapes = shapeRows.map { shapeRow ->
            val width = shapeRow.first().length
            val height = shapeRow.size
            val bitRows = mutableListOf<Long>()
            for (y in 0 until height) {
                val bits = mutableListOf<String>()
                for (x in 0 until width) {
                    if (shapeRow[y][x] == '#') bits.add("1") else bits.add("0")
                }
                bitRows.add(bits.joinToString("").toLong(2))
            }
            Shape(width, height, bitRows)
        }
        val problems = problemRows.map { problemRow ->
            val (gridDimensions, shapeCountsString) = problemRow.split(":")
            val (gridWidth, gridHeight) = gridDimensions.split("x").map { it.toInt() }
            val counts = shapeCountsString.trim().split(" ").map { it.toInt() }
            val map = shapes.zip(counts).toMap().filter { it.value > 0 }
            Problem(gridWidth, gridHeight, map.nn())
        }
        return problems
    }
}