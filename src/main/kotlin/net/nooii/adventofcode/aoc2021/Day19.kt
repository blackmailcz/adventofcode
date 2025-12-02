package net.nooii.adventofcode.aoc2021

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.Point3D
import kotlin.math.abs

/**
 * Created by Nooii on 19.12.2021
 */
object Day19 {

    private open class Scanner(
        val id: Int,
        val points: Set<Point3D>
    ) {
        override fun toString(): String {
            return "----- Scanner -----\n${points.joinToString("\n")}\n"
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Scanner) return false

            if (id != other.id) return false

            return true
        }

        override fun hashCode(): Int {
            return id
        }
    }

    private class CorrectedScanner(
        id: Int,
        points: Set<Point3D>,
        val coordinate: Point3D
    ) : Scanner(id, points)

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2021).loadStrings("Day19Input")
        val scanners = processInput(input)
        val angles = getAllRotationAngles()
        val correctedScanners = correctScanners(scanners, angles)
        part1(correctedScanners)
        part2(correctedScanners)
    }

    private fun part1(scanners: Set<CorrectedScanner>) {
        println(scanners.map { it.points }.flatten().distinct().size)
    }

    private fun part2(scanners: Set<CorrectedScanner>) {
        val coordinates = scanners.map { it.coordinate }
        var maxDistance = 0
        for (c1 in coordinates) {
            for (c2 in coordinates.minus(c1)) {
                val distance = computeManhattanDistance(c1, c2)
                if (distance > maxDistance) {
                    maxDistance = distance
                }
            }
        }
        println(maxDistance)
    }

    private fun computeManhattanDistance(c1: Point3D, c2: Point3D): Int {
        return abs(c1.x - c2.x) + abs(c1.y - c2.y) + abs(c1.z - c2.z)
    }

    private fun correctScanners(scanners: List<Scanner>, angles: List<Point3D>): Set<CorrectedScanner> {
        val scanner0 = CorrectedScanner(scanners[0].id, scanners[0].points, Point3D(0, 0, 0))
        val correctedScanners = mutableSetOf(scanner0)
        while (true) {
            for (correctedScanner in correctedScanners.toList()) {
                for (scanner in scanners) {
                    if (correctedScanners.contains(scanner)) {
                        continue // Do not compute already corrected scanners
                    }
                    for (rotatedScanner in getAllScannerRotations(scanner, angles)) {
                        val coordinates = computeAbsoluteCoordinates(correctedScanner, rotatedScanner)
                        if (coordinates != null) {
                            correctedScanners.add(
                                CorrectedScanner(
                                    scanner.id,
                                    shiftPoints(rotatedScanner.points, coordinates),
                                    coordinates
                                )
                            )
                            if (correctedScanners.size == scanners.size) {
                                return correctedScanners
                            }
                            break
                        }
                    }
                }
            }
        }
    }

    private fun computeAbsoluteCoordinates(s1: CorrectedScanner, s2: Scanner): Point3D? {
        for ((x1, y1, z1) in s1.points) {
            for ((x2, y2, z2) in s2.points) {
                val shift = Point3D(x1 - x2, y1 - y2, z1 - z2)
                // Compute distance between points and shift all points from s2 by the distance until 12 same points are formed
                val shifts = mutableMapOf<Point3D, Int>()
                for ((x3, y3, z3) in s2.points) {
                    val shiftedPoint = Point3D(x3 + shift.x, y3 + shift.y, z3 + shift.z)
                    if (s1.points.contains(shiftedPoint)) {
                        val shiftCount = shifts.getOrDefault(shift, 0) + 1
                        shifts[shift] = shiftCount
                        if (shiftCount >= 12) {
                            return shift
                        }
                    }
                }
            }
        }
        return null
    }

    private fun shiftPoints(points: Set<Point3D>, by: Point3D): Set<Point3D> {
        return points.map { Point3D(it.x + by.x, it.y + by.y, it.z + by.z) }.toSet()
    }

    private fun getAllScannerRotations(scanner: Scanner, angles: List<Point3D>): List<Scanner> {
        return angles.map { (ax, ay, az) ->
            Scanner(scanner.id, scanner.points.map { rotate(it, ax, ay, az) }.toSet())
        }
    }

    private fun getAllRotationAngles(): List<Point3D> {
        val allAngles = listOf(0, 90, 180, 270)
        val angles = mutableListOf<Point3D>()
        // Do not process mirror angles
        for (ay in allAngles) {
            for (az in allAngles) {
                angles.add(Point3D(0, ay, az))
            }
        }
        for (ay in listOf(0, 180)) {
            for (az in allAngles) {
                angles.add(Point3D(90, ay, az))
            }
        }
        return angles
    }

    private fun rotate(point: Point3D, ax: Int, ay: Int, az: Int): Point3D {
        return rotateX(rotateY(rotateZ(point, az), ay), ax)
    }

    private fun rotateX(point: Point3D, deg: Int): Point3D {
        val matrix = arrayOf(arrayOf(1, 0, 0), arrayOf(0, cos(deg), -sin(deg)), arrayOf(0, sin(deg), cos(deg)))
        return performRotate(point, matrix)
    }

    private fun rotateY(point: Point3D, deg: Int): Point3D {
        val matrix = arrayOf(arrayOf(cos(deg), 0, sin(deg)), arrayOf(0, 1, 0), arrayOf(-sin(deg), 0, cos(deg)))
        return performRotate(point, matrix)
    }

    private fun rotateZ(point: Point3D, deg: Int): Point3D {
        val matrix = arrayOf(arrayOf(cos(deg), -sin(deg), 0), arrayOf(sin(deg), cos(deg), 0), arrayOf(0, 0, 1))
        return performRotate(point, matrix)
    }

    private fun performRotate(point: Point3D, matrix: Array<Array<Int>>): Point3D {
        return Point3D(
            x = point.x * matrix[0][0] + point.y * matrix[0][1] + point.z * matrix[0][2],
            y = point.x * matrix[1][0] + point.y * matrix[1][1] + point.z * matrix[1][2],
            z = point.x * matrix[2][0] + point.y * matrix[2][1] + point.z * matrix[2][2]
        )
    }

    private fun sin(deg: Int): Int {
        return when (deg) {
            0, 180 -> 0
            90 -> 1
            270 -> -1
            else -> throw IllegalArgumentException("Invalid angle")
        }
    }

    private fun cos(deg: Int): Int {
        return when (deg) {
            0 -> 1
            90, 270 -> 0
            180 -> -1
            else -> throw IllegalArgumentException("Invalid angle")
        }
    }

    private fun processInput(input: List<String>): List<Scanner> {
        val scanners = mutableListOf<Scanner>()
        val points = mutableListOf<Point3D>()
        for (line in input.plus("")) {
            when {
                line.startsWith("--") -> continue
                line.isBlank() -> {
                    if (points.isNotEmpty()) {
                        scanners.add(Scanner(scanners.size, points.toSet()))
                    }
                    points.clear()
                }
                else -> {
                    val parts = line.split(",").map { it.toInt() }
                    points.add(Point3D(parts[0], parts[1], parts[2]))
                }
            }
        }
        return scanners
    }

}