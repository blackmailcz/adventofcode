package net.nooii.adventofcode.aoc2021

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.Point

/**
 * Created by Nooii on 13.12.2021
 */
class Day13 {

    private class Manual(
        val points: Set<Point>,
        val folds: List<Fold>
    )

    private class Fold(
        val isVertical: Boolean,
        val coordinate: Int
    )

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2021).loadStrings("Day13Input")
            val manual = processInput(input)
            part1(manual)
            part2(manual)
        }

        private fun part1(manual: Manual) {
            println(performFold(manual.points, manual.folds.first()).size)
        }

        private fun part2(manual: Manual) {
            var points = manual.points
            for (fold in manual.folds) {
                points = performFold(points, fold)
            }
            printPoints(points)
        }

        private fun printPoints(points: Set<Point>) {
            val maxX = points.maxOf { it.x }
            val maxY = points.maxOf { it.y }
            for (y in 0..maxY) {
                for (x in 0..maxX) {
                    print(if (points.contains(Point(x, y))) "#" else " ")
                }
                println()
            }
        }

        private fun performFold(points: Set<Point>, fold: Fold): Set<Point> {
            return points.map { point ->
                when {
                    fold.isVertical && point.y > fold.coordinate -> {
                        val distanceToFold = point.y - fold.coordinate
                        Point(point.x, point.y - distanceToFold * 2)
                    }
                    !fold.isVertical && point.x > fold.coordinate -> {
                        val distanceToFold = point.x - fold.coordinate
                        Point(point.x - distanceToFold * 2, point.y)
                    }
                    else -> point
                }
            }.toSet()
        }

        private fun processInput(input: List<String>): Manual {
            val points = mutableSetOf<Point>()
            val folds = mutableListOf<Fold>()
            val foldPrefix = "fold along "
            for (line in input) {
                when {
                    line.startsWith(foldPrefix) -> {
                        val parts = line.drop(foldPrefix.length).split("=")
                        folds.add(Fold(parts[0] == "y", parts[1].toInt()))
                    }
                    line.isNotBlank() -> {
                        val parts = line.split(",").map { it.toInt() }
                        points.add(Point(parts[0], parts[1]))
                    }
                }
            }
            return Manual(points, folds)
        }

    }
}