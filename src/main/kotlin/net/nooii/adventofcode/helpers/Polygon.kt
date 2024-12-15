package net.nooii.adventofcode.helpers

/**
 * Represents an arbitrary closed polygon formed by [points].
 */
class Polygon(
    val points: List<Point>,
) {

    /**
     * Perimeter of the polygon (sum of all edges)
     */
    val perimeter: Long by lazy {
        (points + points.first()).windowed(2, 1).sumOf { (p1, p2) -> p1.manhattanDistance(p2).toLong() }
    }

    /**
     * Area of the polygon, rounded down to the nearest integer
     */
    val area: Long by lazy {
        // Shoelace Formula
        (points + points.first()).windowed(2, 1).sumOf { (p1, p2) ->
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
}