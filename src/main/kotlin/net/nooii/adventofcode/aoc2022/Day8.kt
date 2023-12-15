package net.nooii.adventofcode.aoc2022

import net.nooii.adventofcode.helpers.*
import net.nooii.adventofcode.helpers.PointDirection.*
import java.awt.Point
import kotlin.math.max

class Day8 {

    private class Tree(
        val point: Point,
        val value: Int
    ) {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Tree) return false

            if (point != other.point) return false

            return true
        }

        override fun hashCode(): Int {
            return point.hashCode()
        }
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2022).loadStrings("Day8Input")
            val trees = parseTrees(input)
            part1(trees)
            part2(trees)
        }

        private fun part1(trees: PointMap<Tree>) {
            val visibleTrees = mutableSetOf<Tree>()
            for (x in 0 until trees.width) {
                visibleTrees += discoverTreesFromGround(trees, Point(x, 0), DOWN)
                visibleTrees += discoverTreesFromGround(trees, Point(x, trees.height - 1), UP)
            }
            for (y in 0 until trees.height) {
                visibleTrees += discoverTreesFromGround(trees, Point(0, y), RIGHT)
                visibleTrees += discoverTreesFromGround(trees, Point(trees.width - 1, y), LEFT)
            }
            println(visibleTrees.count())
        }

        private fun part2(trees: PointMap<Tree>) {
            val maxScore = trees.values.maxOf { tree ->
                computeScore(trees, tree)
            }
            println(maxScore)
        }

        private fun computeScore(trees: PointMap<Tree>, candidate: Tree): Int {
            var score = 1
            for (direction in entries) {
                score *= computeViewDistance(trees, candidate, direction)
            }
            return score
        }

        private fun discoverTreesFromGround(trees: PointMap<Tree>, from: Point, direction: PointDirection): Set<Tree> {
            var point = from
            var highestTreeValue = -1
            val visibleTrees = mutableSetOf<Tree>()
            while (trees.containsKey(point)) {
                val tree = trees[point]
                if (tree.value > highestTreeValue) {
                    visibleTrees.add(tree)
                }
                highestTreeValue = max(tree.value, highestTreeValue)
                point = direction.next(point)
            }
            return visibleTrees
        }

        private fun computeViewDistance(trees: PointMap<Tree>, candidate: Tree, direction: PointDirection): Int {
            var point = candidate.point
            var distance = 0
            while (trees.containsKey(point)) {
                val tree = trees[point]
                if (tree != candidate) {
                    distance++
                    if (tree.value >= candidate.value) {
                        break
                    }
                }
                point = direction.next(point)
            }
            return distance
        }

        private fun parseTrees(input: List<String>): PointMap<Tree> {
            return PointMap.filled(input.first().length, input.size) { x, y -> Tree(Point(x, y), input[y][x].digitToInt()) }
        }
    }
}