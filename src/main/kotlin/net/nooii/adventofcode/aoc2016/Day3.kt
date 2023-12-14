package net.nooii.adventofcode.aoc2016

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader

class Day3 {

    private data class Triangle(
        val a: Int,
        val b: Int,
        val c: Int
    ) {
        fun isValid(): Boolean {
            return a + b > c && b + c > a && c + a > b
        }
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2016).loadStrings("Day3Input")
            val triangles = processInput(input)
            part1(triangles)
            part2(triangles)
        }

        private fun part1(triangles: List<Triangle>) {
            solution(triangles)
        }

        private fun part2(triangles: List<Triangle>) {
            val newTriangles = triangles.windowed(3, 3).flatMap { triangle ->
                listOf(
                    Triangle(triangle[0].a, triangle[1].a, triangle[2].a),
                    Triangle(triangle[0].b, triangle[1].b, triangle[2].b),
                    Triangle(triangle[0].c, triangle[1].c, triangle[2].c)
                )
            }
            solution(newTriangles)
        }

        private fun solution(triangles: List<Triangle>) {
            println(triangles.count { it.isValid() })
        }

        private fun processInput(input: List<String>): List<Triangle> {
            return input.map { line ->
                val (a, b, c) = line.trim().split(Regex("\\s+")).map { it.toInt() }
                Triangle(a, b, c)
            }
        }
    }
}