package net.nooii.adventofcode.helpers

import kotlin.math.abs

/**
 * Represents a point in 2D space.
 */
data class Point(
    val x: Int,
    val y: Int
) {

    /**
     * Computes Manhattan distance to other [Point].
     */
    fun manhattanDistance(other: Point): Int {
        return abs(x - other.x) + abs(y - other.y)
    }

    /**
     * Computes coordinate difference to other [Point] represented as a [Point].
     */
    fun diff(other: Point): Point {
        return Point(other.x - x, other.y - y)
    }

    override fun toString() = "[$x,$y]"
}