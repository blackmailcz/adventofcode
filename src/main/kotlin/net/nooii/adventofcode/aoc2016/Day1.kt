package net.nooii.adventofcode.aoc2016

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.Point
import net.nooii.adventofcode.helpers.PointDirection

object Day1 {

    private class Step(
        val rotation: String,
        val steps: Int
    )

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2016).loadStrings("Day1Input")
        val steps = processInput(input)
        part1(steps)
        part2(steps)
    }

    private fun part1(steps: List<Step>) {
        val start = Point(0, 0)
        var point = start
        var direction = PointDirection.UP
        for (step in steps) {
            direction = when (step.rotation) {
                "L" -> direction.rotateCCW()
                "R" -> direction.rotateCW()
                else -> direction
            }
            point = direction.next(point, step.steps)
        }
        println(start.manhattanDistance(point))
    }

    private fun part2(steps: List<Step>) {
        val start = Point(0, 0)
        var point = start
        var direction = PointDirection.UP
        val visited = mutableSetOf(start)
        for (step in steps) {
            direction = when (step.rotation) {
                "L" -> direction.rotateCCW()
                "R" -> direction.rotateCW()
                else -> error("Unknown rotation")
            }
            repeat(step.steps) {
                point = direction.next(point)
                if (visited.contains(point)) {
                    println(start.manhattanDistance(point))
                    return
                }
                visited.add(point)
            }
        }
        error("No solution found")
    }

    private fun processInput(input: List<String>): List<Step> {
        return input.first().split(", ").map { Step(it.take(1), it.drop(1).toInt()) }
    }
}