package net.nooii.adventofcode.aoc2017

import net.nooii.adventofcode.helpers.*
import kotlin.math.abs
import kotlin.math.min

object Day3 {

    private data class OrderInfo(
        val order: Int,
        val lowestNumber: Int,
        val numbers: Int,
    ) {
        val sideSize = order * 2 - 1
        val highestNumber = lowestNumber + numbers - 1
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2017).loadInts("Day3Input").first()
        part1(input)
        part2(input)
    }

    private fun part1(input: Int) {
        val orderInfo = analyze(input)
        with(orderInfo) {
            val distanceFromCorner = when (input) {
                in IntRange(lowestNumber, cornerNumber(1)) -> {
                    distanceFromCorner(lowestNumber - 1, cornerNumber(1), input)
                }
                in IntRange(cornerNumber(1), cornerNumber(2)) -> {
                    distanceFromCorner(cornerNumber(1), cornerNumber(2), input)
                }
                in IntRange(cornerNumber(2), cornerNumber(3)) -> {
                    distanceFromCorner(cornerNumber(2), cornerNumber(3), input)
                }
                in IntRange(cornerNumber(3), highestNumber) -> {
                    distanceFromCorner(cornerNumber(3), highestNumber, input)
                }
                else -> error("Should not happen")
            }
            val distance = sideSize / 2 - distanceFromCorner + order - 1
            println(distance)
        }
    }

    private fun part2(input: Int) {
        // Just fill the spiral
        val points = mutableMapOf(
            Point(0, 0) to 1
        )
        // Going CCW
        var direction = PointDirection.DOWN
        var currentPoint = Point(0, 0)
        var lastSum: Int
        do {
            val rotatedDirection = direction.rotateCCW()
            val nextCCW = rotatedDirection.next(currentPoint)
            val nextPoint: Point
            if (nextCCW !in points) {
                nextPoint = nextCCW
                direction = rotatedDirection
            } else {
                nextPoint = direction.next(currentPoint)
            }
            lastSum = collectAdjacent(nextPoint, points)
            points[nextPoint] = lastSum
            currentPoint = nextPoint
        } while (lastSum <= input)
        println(lastSum)
    }

    private fun collectAdjacent(point: Point, points: Map<Point, Int>): Int {
        return PointDirectionDiagonal.entries.sumOf { direction ->
            points[direction.next(point)] ?: 0
        }
    }

    private fun distanceFromCorner(c1: Int, c2: Int, input: Int): Int {
        return min(abs(c1 - input), abs(c2 - input))
    }

    private fun OrderInfo.cornerNumber(multiplier: Int): Int {
        return lowestNumber - 1 + multiplier * (sideSize - 1)
    }

    private fun analyze(input: Int): OrderInfo {
        if (input < 1) {
            error("Only positive integers are allowed")
        }
        if (input == 1) {
            return OrderInfo(1, 1, 1)
        }
        var order = 2
        var i = 1
        var numbers: Int
        var orderInfo: OrderInfo? = null
        while (i < input) {
            numbers = 4 * (order * 2 - 1) - 4
            orderInfo = OrderInfo(order, i + 1, numbers)
            i += numbers
            order++
        }
        return orderInfo!!
    }
}