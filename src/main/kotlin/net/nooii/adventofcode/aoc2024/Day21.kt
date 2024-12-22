package net.nooii.adventofcode.aoc2024

import com.github.shiguruikai.combinatoricskt.permutations
import net.nooii.adventofcode.helpers.*
import kotlin.math.abs

class Day21 {

    private enum class NumericKey(val symbol: Char, val point: Point) {
        ONE('1', Point(0, 2)),
        TWO('2', Point(1, 2)),
        THREE('3', Point(2, 2)),
        FOUR('4', Point(0, 1)),
        FIVE('5', Point(1, 1)),
        SIX('6', Point(2, 1)),
        SEVEN('7', Point(0, 0)),
        EIGHT('8', Point(1, 0)),
        NINE('9', Point(2, 0)),
        ZERO('0', Point(1, 3)),
        A('A', Point(2, 3));

        companion object {

            fun fromSymbol(char: Char) = entries.find { it.symbol == char }!!
        }
    }

    private enum class ArrowKey(val arrow: Char, val point: Point) {
        LEFT('<', Point(0, 1)),
        RIGHT('>', Point(2, 1)),
        UP('^', Point(1, 0)),
        DOWN('v', Point(1, 1)),
        A('A', Point(2, 0));

        companion object {

            fun fromSymbol(char: Char) = entries.find { it.arrow == char } ?: error("Unknown symbol $char")
        }
    }

    private object NumericKeypad {

        private const val WIDTH = 3
        private const val HEIGHT = 4
        private val INVALID_POINT = Point(0, 3)

        fun isValid(point: Point) = point != INVALID_POINT && point.x in 0 until WIDTH && point.y in 0 until HEIGHT
    }

    private object ArrowKeypad {

        const val WIDTH = 3
        const val HEIGHT = 2
        val INVALID_POINT = Point(0, 0)

        fun isValid(point: Point) = point != INVALID_POINT && point.x in 0 until WIDTH && point.y in 0 until HEIGHT
    }

    private data class PathCacheKey(
        val from: Point,
        val to: Point,
        val isNumeric: Boolean
    ) {
        constructor(from: NumericKey, to: NumericKey) : this(from.point, to.point, true)
        constructor(from: ArrowKey, to: ArrowKey) : this(from.point, to.point, false)
    }

    private data class CostCacheKey(
        val chunk: String,
        val depth: Int
    )

    companion object {
        private val pathCache = mutableMapOf<PathCacheKey, List<List<ArrowKey>>>()

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2024).loadStrings("Day21Input")
            part1(input)
            part2(input)
        }

        private fun part1(input: List<String>) {
            solution(input, 2)
        }

        private fun part2(input: List<String>) {
            solution(input, 25)
        }

        private fun solution(input: List<String>, depth: Int) {
            val complexity = input.sumOf { code ->
                findMinimumCost(code, depth) * code.dropLast(1).toLong()
            }
            println(complexity)
        }

        private fun findMinimumCost(code: String, depth: Int): Long {
            val cache: MutableMap<CostCacheKey, Long> = mutableMapOf()

            fun rec(chunk: String, depth: Int, isNumeric: Boolean): Long {
                // Cannot use "computeIfAbsent" because it is throwing ConcurrentModificationException
                return cache.getOrPut(CostCacheKey(chunk, depth)) {
                    "A$chunk".windowed(2, 1).sumOf { (c1, c2) ->
                        val paths = if (isNumeric) {
                            val key = PathCacheKey(NumericKey.fromSymbol(c1), NumericKey.fromSymbol(c2))
                            pathCache.getOrPut(key) {
                                getShortestPaths(key.from, key.to) { NumericKeypad.isValid(it) }
                            }
                        } else {
                            val key = PathCacheKey(ArrowKey.fromSymbol(c1), ArrowKey.fromSymbol(c2))
                            pathCache.getOrPut(key) {
                                getShortestPaths(key.from, key.to) { ArrowKeypad.isValid(it) }
                            }
                        }
                        if (depth == 0) {
                            paths.minOf { it.size.toLong() }
                        } else {
                            paths.minOf { path -> rec(path.toCacheKey(), depth - 1, false) }
                        }
                    }
                }
            }
            return rec(code, depth, true)
        }

        private fun List<ArrowKey>.toCacheKey() = joinToString("") { it.arrow.toString() }

        private fun getShortestPaths(start: Point, end: Point, validation: ((Point) -> Boolean)): List<List<ArrowKey>> {
            val diff = start.diff(end)
            val parts = mutableListOf<ArrowKey>()
            when {
                diff.x > 0 -> ArrowKey.RIGHT
                diff.x < 0 -> ArrowKey.LEFT
                else -> null
            }?.let { parts.addAll(listOf(it).repeat(abs(diff.x))) }
            when {
                diff.y > 0 -> ArrowKey.DOWN
                diff.y < 0 -> ArrowKey.UP
                else -> null
            }?.let { parts.addAll(listOf(it).repeat(abs(diff.y))) }
            return parts
                .permutations()
                .toSet()
                .filter { path ->
                    var point = start
                    var isValid = true
                    for (arrowKey in path) {
                        val next = PointDirection.fromArrow(arrowKey.arrow).next(point)
                        if (!validation.invoke(next)) {
                            isValid = false
                            break
                        }
                        point = next
                    }
                    isValid
                }
                .map { it + ArrowKey.A }
        }
    }
}
