package net.nooii.adventofcode.aoc2023

import com.github.shiguruikai.combinatoricskt.combinations
import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.GaussJordanEliminationBigDecimal
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.round
import java.math.BigDecimal
import java.math.RoundingMode

class Day24 {

    private data class BigDecimalRange(
        val minInclusive: BigDecimal,
        val maxInclusive: BigDecimal
    ) {
        operator fun contains(value: BigDecimal): Boolean {
            return value in minInclusive..maxInclusive
        }
    }

    private data class BigDecimalPoint(
        val x: BigDecimal,
        val y: BigDecimal,
        val z: BigDecimal
    )

    private data class Hailstone(
        val point: BigDecimalPoint,
        val velocity: BigDecimalPoint
    )

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2023).loadStrings("Day24Input")
            val hailstones = processInput(input)
            part1(hailstones)
            part2(hailstones)
        }

        private fun part1(hailstones: List<Hailstone>) {
            val intersectionRange = BigDecimalRange(BigDecimal(200000000000000L), BigDecimal(400000000000000L))
            val count = hailstones.combinations(2).count { (h1, h2) ->
                val equationA = computeXYLineEquation(
                    BigDecimalPoint(h1.point.x, h1.point.y, BigDecimal.ZERO),
                    BigDecimalPoint(h1.velocity.x, h1.velocity.y, BigDecimal.ZERO)
                )
                val equationB = computeXYLineEquation(
                    BigDecimalPoint(h2.point.x, h2.point.y, BigDecimal.ZERO),
                    BigDecimalPoint(h2.velocity.x, h2.velocity.y, BigDecimal.ZERO)
                )
                val intersection = findXYIntersection(equationA, equationB) ?: return@count false
                val inRangeX = intersection.x in intersectionRange
                val inRangeY = intersection.y in intersectionRange
                val inFuture1 = isInFuture(h1, intersection)
                val inFuture2 = isInFuture(h2, intersection)
                inRangeX && inRangeY && inFuture1 && inFuture2
            }
            println(count)
        }

        private fun part2(hailstones: List<Hailstone>) {
            val matrixXYData = hailstones.take(5).windowed(2, 1).map { (h1, h2) ->
                createXYMatrixRow(h1.point, h1.velocity, h2.point, h2.velocity)
            }
            val matrixXY = matrixXYData.map { it.first }.toTypedArray()
            val vectorXY = matrixXYData.map { it.second }.toTypedArray()

            val (x, vx, y, vy) = GaussJordanEliminationBigDecimal(matrixXY, vectorXY).solution()
                ?.toList()
                ?: error("No solution found")

            val (z, vz) = computeZ(
                x.round(),
                vx.round(),
                hailstones[0].point,
                hailstones[0].velocity,
                hailstones[1].point,
                hailstones[1].velocity
            )

            val roundedSum = x.round() + y.round() + z.round()
            println(roundedSum)
        }

        private fun computeZ(
            x: BigDecimal,
            vx: BigDecimal,
            p1: BigDecimalPoint,
            v1: BigDecimalPoint,
            p2: BigDecimalPoint,
            v2: BigDecimalPoint
        ): Pair<BigDecimal, BigDecimal> {

            val t1 = (p1.x - x).divide((vx - v1.x), 64, RoundingMode.HALF_UP)
            val t2 = (p2.x - x).divide((vx - v2.x), 64, RoundingMode.HALF_UP)

            val r1 = p1.z + v1.z * t1
            val r2 = p2.z + v2.z * t2

            val dz = (r2 - r1) / (t2 - t1)
            val z = r1 - dz * t1

            return Pair(z, dz)
        }

        private fun createXYMatrixRow(
            p1: BigDecimalPoint,
            v1: BigDecimalPoint,
            p2: BigDecimalPoint,
            v2: BigDecimalPoint
        ): Pair<Array<BigDecimal>, BigDecimal> {
            // Idea credits: https://www.reddit.com/r/adventofcode/comments/18pnycy/comment/kf068o2/

            // Consider only XY plane, where x, y, dx, dy are unknowns for the crossing line

            // x + t * dx = p1.x + t * v1.x
            // y + t * dy = p1.y + t * v1.y

            // This can be rewritten as:
            // t = (p1.x - x) / (dx - v1.x)
            // t = (p1.x - y) / (dy - v1.x)

            // Combine equations:
            // (p1.x - x) / (dx - v1.x) = (p1.y - y) / (dy - v1.y)
            // p1.x * dxy - p1.x * v1.y - x*dy + x * v1.y = p1.y * dx - p1.y * v1.x - y * dx - y * v1.x

            // Rewrite:
            // y * dx - x * dy = p1.y * dx - p1.y * v1.x + y * v1.x - p1.x * dy + p1.x * v1.y - x * v1.y

            // Now bring another equation (for another source point and vector):
            // y * dx - x * dy = p2.y * dx - p2.y * v2.x + y * v2.x - p2.x * dy + p2.x * v2.y - x * v2.y

            // Combine:
            // p1.y * dx - p1.y * v1.x + y * v1.x - p1.x * dy + p1.x * v1.y - x * v1.y = p2.y * dx - p2.y * v2.x + y * v2.x - p2.x * dy + p2.x * v2.y - x * v2.y

            // Rearrange:
            // (v1.y - v2.y) * x + (p2.y - p1.y) * dx + (v2.x - v1.x) * y + (p1.x - p2.x) * dy = v1.y * p1.x - v1.x * p1.y + v2.x * p2.y - v2.y * p2.x

            return Pair(
                arrayOf((v1.y - v2.y), (p2.y - p1.y), (v2.x - v1.x), (p1.x - p2.x)),
                v1.y * p1.x - v1.x * p1.y + v2.x * p2.y - v2.y * p2.x
            )
        }

        private fun isInFuture(hailstone: Hailstone, intersection: BigDecimalPoint): Boolean {
            return isInFuture(hailstone.point.x, hailstone.velocity.x, intersection.x) &&
                    isInFuture(hailstone.point.y, hailstone.velocity.y, intersection.y)
        }

        private fun isInFuture(
            coordinate: BigDecimal,
            velocity: BigDecimal,
            intersectionCoordinate: BigDecimal
        ): Boolean {
            return when (velocity.signum()) {
                1 -> coordinate <= intersectionCoordinate
                -1 -> coordinate >= intersectionCoordinate
                else -> true
            }
        }

        private fun computeXYLineEquation(p0: BigDecimalPoint, diff: BigDecimalPoint): BigDecimalPoint {
            val slope = diff.y.divide(diff.x, 64, RoundingMode.HALF_UP)
            // b = y0 - a * x0
            val b = p0.y - slope * p0.x
            // y = slope * x + b
            return BigDecimalPoint(slope, b, BigDecimal.ZERO)
        }

        private fun findXYIntersection(equationA: BigDecimalPoint, equationB: BigDecimalPoint): BigDecimalPoint? {
            val (a1, b1) = equationA
            val (a2, b2) = equationB
            if (a1 == a2) {
                return null
            }
            val x = (b2 - b1).divide((a1 - a2), 64, RoundingMode.HALF_UP)
            val y = a1 * x + b1
            return BigDecimalPoint(x, y, BigDecimal.ZERO)
        }

        private fun processInput(input: List<String>): List<Hailstone> {
            return input.map { line ->
                val (p, diff) = line.split(Regex("\\s+@\\s+")).map { point ->
                    val (x, y, z) = point.split(Regex(",\\s*")).map { BigDecimal(it) }
                    BigDecimalPoint(x, y, z)
                }
                Hailstone(p, diff)
            }
        }
    }
}
