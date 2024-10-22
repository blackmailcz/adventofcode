package net.nooii.adventofcode.aoc2016

import com.github.shiguruikai.combinatoricskt.combinations
import com.github.shiguruikai.combinatoricskt.permutations
import net.nooii.adventofcode.helpers.*
import java.awt.Point

class Day24 {

    private sealed interface TileType {
        data object Wall : TileType
        data object Empty : TileType
        data class Mark(val number: Int) : TileType
    }

    private data class Mark(
        val point: Point,
        val number: Int
    )

    private fun interface TransformPermutation {
        fun transform(marks: List<Mark>, path: List<Mark>): List<Mark>
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2016).loadStrings("Day24Input")
            val blueprint = processInput(input)
            part1(blueprint)
            part2(blueprint)
        }

        private fun part1(blueprint: PointMap<TileType>) {
            solution(blueprint) { marks, path ->
                buildList {
                    add(marks.first())
                    addAll(path)
                }
            }
        }

        private fun part2(blueprint: PointMap<TileType>) {
            solution(blueprint) { marks, path ->
                buildList {
                    add(marks.first())
                    addAll(path)
                    add(marks.first())
                }
            }
        }

        private fun solution(blueprint: PointMap<TileType>, transformPermutation: TransformPermutation) {
            val marks = findMarks(blueprint)
            // Find the shortest paths between all pairs of marks
            val matrix = Array(marks.size) { LongArray(marks.size) }
            for ((m1, m2) in marks.combinations(2)) {
                val shortestPath = findShortestPath(m1.point, m2.point, blueprint)
                matrix[m1.number][m2.number] = shortestPath
                matrix[m2.number][m1.number] = shortestPath
            }
            // Iterate over permutations of the marks to find the shortest route
            val shortestRoute = marks.drop(1).permutations().minOf { perm ->
                transformPermutation.transform(marks, perm).windowed(2).sumOf { (m1, m2) ->
                    matrix[m1.number][m2.number]
                }
            }
            println(shortestRoute)
        }

        private fun findMarks(pointMap: PointMap<TileType>): List<Mark> {
            return pointMap.entries
                .filter { it.value is TileType.Mark }
                .sortedBy { (it.value as TileType.Mark).number }
                .map { Mark(it.key, (it.value as TileType.Mark).number) }
        }

        private fun findShortestPath(start: Point, end: Point, pointMap: PointMap<TileType>): Long {
            val result = traverse(
                start = start,
                traverseMode = TraverseMode.ToEnd { it == end },
                nextItems = { current ->
                    buildList {
                        for (direction in PointDirection.entries) {
                            val next = direction.next(current)
                            if (pointMap.isInRange(next) && pointMap[next] !is TileType.Wall) {
                                add(ItemWithCost(next))
                            }
                        }
                    }
                }
            )
            return result!!.cost
        }

        private fun processInput(input: List<String>): PointMap<TileType> {
            val w = input.first().length
            val h = input.size
            val map = PointMap<TileType>(w, h)
            for ((y, line) in input.withIndex()) {
                for ((x, char) in line.withIndex()) {
                    val tile = when (char) {
                        '#' -> TileType.Wall
                        '.' -> TileType.Empty
                        else -> TileType.Mark(char.digitToInt())
                    }
                    map[Point(x, y)] = tile
                }
            }
            return map
        }
    }
}