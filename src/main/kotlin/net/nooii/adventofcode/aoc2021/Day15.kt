package net.nooii.adventofcode.aoc2021

import net.nooii.adventofcode.helpers.*
import java.awt.Point

/**
 * Created by Nooii on 15.12.2021
 */
class Day15 {

    private class Input(
        val sizeX: Int,
        val sizeY: Int,
        val map: List<List<Int>>
    )

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2021).loadStrings("Day15Input")
            val input1 = processInput1(input)
            println(solution(input1))
            println(solution(processInput2(input1)))
        }

        private fun solution(input: Input): Int {
            val result = traverse(
                start = Point(0, 0),
                isEnd = { isEndPoint(it, input) },
                nextItems = { current ->
                    PointDirection.entries
                        .map { it.next(current) }
                        .filter { isValidPoint(it, input) }
                        .map { ItemWithCost(it, input.map[it.y][it.x].toLong()) }
                }
            )
            return result!!.cost().toInt()
        }

        private fun isValidPoint(point: Point, input: Input): Boolean {
            return point.x in 0 until input.sizeX && point.y in 0 until input.sizeY
        }

        private fun isEndPoint(point: Point, input: Input): Boolean {
            return point.x == input.sizeX - 1 && point.y == input.sizeY - 1
        }

        private fun processInput1(input: List<String>): Input {
            val map = input.map { line -> line.map { it.digitToInt() } }
            return Input(map.first().size, map.size, map)
        }

        private fun processInput2(input1: Input): Input {
            val map = mutableListOf<MutableList<Int>>()
            for (y in 0 until input1.sizeY * 5) {
                val cY = y / input1.sizeY
                map.add(mutableListOf())
                for (x in 0 until input1.sizeX * 5) {
                    val cX = x / input1.sizeX
                    map[y].add(shift(input1.map[y % input1.sizeY][x % input1.sizeX], (cX + cY).mod(10)))
                }
            }
            return Input(input1.sizeX * 5, input1.sizeY * 5, map)
        }

        private fun shift(v: Int, c: Int) = if (v + c > 9) v + c - 9 else v + c
    }
}