package net.nooii.adventofcode.aoc2024

import com.github.shiguruikai.combinatoricskt.cartesianProduct
import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.nn
import net.nooii.adventofcode.helpers.splitByEmptyLine

object Day25 {

    private enum class Type {
        LOCK, KEY
    }

    private class Item(
        val type: Type,
        val heights: IntArray
    )

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2024).loadStrings("Day25Input")
        val width = input.first().length
        val patterns = input.splitByEmptyLine().map { processPattern(width, it) }
        part1(width, patterns)
    }

    private fun part1(width: Int, patterns: List<Item>) {
        val grouped = patterns.groupBy { it.type }.nn()
        val locks = grouped[Type.LOCK]
        val keys = grouped[Type.KEY]
        val sum = locks.cartesianProduct(keys).count { (lock, key) ->
            (0 until width).all { lock.heights[it] + key.heights[it] <= width }
        }
        println(sum)
    }

    private fun processPattern(width: Int, pattern: List<String>): Item {
        val type = if (pattern.first().all { it == '#' }) Type.LOCK else Type.KEY
        val heights = IntArray(width) { 0 }
        for (row in pattern.drop(1).dropLast(1)) {
            for (i in row.indices) {
                if (row[i] == '#') {
                    heights[i]++
                }
            }
        }
        return Item(type, heights)
    }
}