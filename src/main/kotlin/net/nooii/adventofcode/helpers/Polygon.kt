package net.nooii.adventofcode.helpers

/**
 * Represents an arbitrary closed polygon formed by [points].
 */
class Polygon(
    val points: List<Point>,
) {

    /**
     * Edges of the polygon, represented as a list of pairs of the edge points.
     */
    val edges: List<Pair<Point, Point>> by lazy {
        (points + points.first()).windowed(2, 1).map { (p1, p2) -> p1 to p2 }
    }

    /**
     * Perimeter of the polygon (sum of all edges)
     */
    val perimeter: Long by lazy {
        edges.sumOf { (p1, p2) -> p1.manhattanDistance(p2).toLong() }
    }

    /**
     * Area of the polygon, rounded down to the nearest integer
     */
    val area: Long by lazy {
        // Shoelace Formula
        edges.sumOf { (p1, p2) ->
            p1.x.toLong() * p2.y.toLong() - p1.y.toLong() * p2.x.toLong()
        } / 2
    }

    /**
     * Total number of all points inside the polygon
     */
    val numberOfInsidePoints: Long by lazy {
        // Pick's Theorem

        // Area = InnerVertices + OuterVertices / 2 - 1
        // InnerVertices = Area - OuterVertices / 2 + 1
        area - perimeter / 2 + 1
    }

    /**
     * Total number of all points of the polygon
     */
    val numberOfPoints: Long by lazy {
        numberOfInsidePoints + perimeter
    }

    /**
     * Checks if the polygon contains a given [point] (the point is either inside or on the edge).
     * Uses the Winding Number algorithm implementation.
     */
    operator fun contains(point: Point): Boolean {

        fun isLeft(p0: Point, p1: Point, p2: Point): Long {
            return (p1.x - p0.x).toLong() * (p2.y - p0.y).toLong() - (p2.x - p0.x).toLong() * (p1.y - p0.y).toLong()
        }

        // First check if point is on any edge
        for ((p1, p2) in edges) {
            val cross = (point.y - p1.y).toLong() * (p2.x - p1.x).toLong() - (point.x - p1.x).toLong() * (p2.y - p1.y).toLong()
            if (cross == 0L) {
                // Point is co-linear with edge
                if (point.x >= minOf(p1.x, p2.x) && point.x <= maxOf(p1.x, p2.x) &&
                    point.y >= minOf(p1.y, p2.y) && point.y <= maxOf(p1.y, p2.y)) {
                    return true
                }
            }
        }

        // Winding Number algorithm
        var winding = 0
        for ((p1, p2) in edges) {
            if (p1.y <= point.y) {
                if (p2.y > point.y && isLeft(p1, p2, point) > 0) {
                    winding++
                }
            } else {
                if (p2.y <= point.y && isLeft(p1, p2, point) < 0) {
                    winding--
                }
            }
        }

        return winding != 0
    }
}