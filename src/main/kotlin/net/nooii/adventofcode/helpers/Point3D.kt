package net.nooii.adventofcode.helpers

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Represents a point in 3D space.
 */
data class Point3D(
    val x: Int,
    val y: Int,
    val z: Int
) {

    /**
     * Retrieves the set of adjacent points that share a side with this point in 3D space.
     *
     * This function returns a set of six [Point3D] objects, each representing a point
     * that is adjacent to the current point along one of the three axes (x, y, or z).
     * The adjacent points are those that differ by exactly 1 unit in one coordinate,
     * while the other two coordinates remain the same.
     *
     * @return A [Set] of six [Point3D] objects representing the adjacent points
     *         that share a side with this point in 3D space.
     */
    fun getAdjacentSidePoints() = setOf(
        Point3D(x - 1, y, z),
        Point3D(x + 1, y, z),
        Point3D(x, y - 1, z),
        Point3D(x, y + 1, z),
        Point3D(x, y, z - 1),
        Point3D(x, y, z + 1)
    )

    /**
     * Computes Manhattan distance to other [Point3D].
     */
    fun manhattanDistance(other: Point3D): Int {
        return abs(x - other.x) + abs(y - other.y) + abs(z - other.z)
    }

    /**
     * Computes Euclidean distance to other [Point3D].
     */
    fun euclideanDistance(other: Point3D): Double {
        return sqrt(
            (x - other.x).toDouble().pow(2) + (y - other.y).toDouble().pow(2) + (z - other.z).toDouble().pow(2)
        )
    }

    override fun toString() = "[$x,$y,$z]"

    companion object {

        /**
         * Creates a Point3D object from a string representation of x, y, and z coordinates.
         */
        fun fromXYZString(string: String): Point3D {
            val (x, y, z) = Regex("(-?\\d+),\\s*(-?\\d+),\\s*(-?\\d+)").captureFirstMatch(string) { it.toInt() }
            return Point3D(x, y, z)
        }
    }
}