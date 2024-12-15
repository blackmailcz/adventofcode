package net.nooii.adventofcode.helpers

/**
 * Represents a point in 3D space using double precision coordinates.
 *
 * @property x The x-coordinate of the point.
 * @property y The y-coordinate of the point.
 * @property z The z-coordinate of the point. Defaults to positive infinity if not specified.
 */
data class DoublePoint(
    val x: Double,
    val y: Double,
    val z: Double = Double.POSITIVE_INFINITY
)

/**
 * Computes the equation of a plane given two points on the plane and a vector.
 *
 * This function calculates the coefficients of the plane equation Ax + By + Cz + D = 0,
 * where (A, B, C) is the normal vector to the plane and D is the constant term.
 *
 * @param p1 A point on the plane, represented as a DoublePoint.
 * @param v1 A vector on the plane, represented as a DoublePoint.
 * @param p2 Another point on the plane, different from p1, represented as a DoublePoint.
 * @return A List of four Double values representing the coefficients [A, B, C, D] of the plane equation.
 */
private fun computePlaneEquation(p1: DoublePoint, v1: DoublePoint, p2: DoublePoint): List<Double> {

//    val p1 = DoublePoint(5.0, 8.0, -1.0)
//    val v1 = DoublePoint(4.0, 3.0, -1.0)
//    val p2 = DoublePoint(-6.0, 1.0, 5.0)

    val v2 = DoublePoint(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z)

    println(v1)
    println(v2)

    // Determinants
    val a = (v1.y * v2.z - v1.z * v2.y)
    val b = (v1.x * v2.z - v1.z * v2.x)
    val c = (v1.x * v2.y - v1.y * v2.x)
    // Equation
    val n = DoublePoint(a, -b, c)
    val d = (-1 * n.x * p2.x) + (-1 * n.y * p2.y) + (-1 * n.z * p2.z)
    // Ax + By + Cz + D = 0
    return listOf(n.x, n.y, n.z, d)
}


/**
 * Calculates the intersection point of a line with a plane in 3D space.
 *
 * This function determines where a given line intersects with a plane defined by its equation.
 * It uses the parametric form of the line equation and substitutes it into the plane equation
 * to solve for the intersection point.
 *
 * @param planeEquation A list of four Double values representing the coefficients [A, B, C, D]
 *                      of the plane equation Ax + By + Cz + D = 0.
 * @param p A DoublePoint representing a point on the line.
 * @param v A DoublePoint representing the direction vector of the line.
 * @return A DoublePoint representing the intersection point of the line and the plane.
 *         If the line is parallel to the plane (no intersection), the behavior is undefined.
 */
private fun intersectPlaneWithLine(planeEquation: List<Double>, p: DoublePoint, v: DoublePoint): DoublePoint {
    val (X, Y, Z, D) = planeEquation

//            val (X, Y, Z, D) = listOf(1, -1, 2, -9)
//            val p = DoublePoint(3.0, 2.0, 0.0)
//            val v = DoublePoint(-1.0, 1.0, 5.0)

    val t = (-1 * D - X * p.x - Y * p.y - Z * p.z) / (X * v.x + Y * v.y + Z * v.z)
    val x = p.x + v.x * t
    val y = p.y + v.y * t
    val z = p.z + v.z * t
    return DoublePoint(x, y, z)
}


/**
 * Calculates the intersection point of two 3D lines, if it exists.
 *
 * This function determines whether two lines in 3D space intersect and, if so, computes their intersection point.
 * It uses parametric equations of the lines to solve for the intersection.
 *
 * @param p1 A DoublePoint representing a point on the first line.
 * @param v1 A DoublePoint representing the direction vector of the first line.
 * @param p2 A DoublePoint representing a point on the second line.
 * @param v2 A DoublePoint representing the direction vector of the second line.
 * @return A DoublePoint representing the intersection point if the lines intersect, or null if they do not intersect
 *         or are parallel.
 */
private fun xyzIntersection(
    p1: DoublePoint,
    v1: DoublePoint,
    p2: DoublePoint,
    v2: DoublePoint
): DoublePoint? {

//    val p1 = DoublePoint(-2.0, -1.0, 0.0)
//    val v1 = DoublePoint(1.0, 1.0, 1.0)
//    val p2 = DoublePoint(8.0, -6.0, -11.0)
//    val v2 = DoublePoint(-2.0, 3.0, 5.0)

    // p1x + v1x alpha = p2x + v2x beta
    // p1y + v1y alpha = p2y + v2y beta
    // p1z + v1z alpha = p2z + v2z beta

    // alpha = (p2x + v2x beta - p1x) / v1x
    // p1y + v1y ( (p2x + v2x beta - p1x) / v1x ) = p2y + v2y beta
    val beta = (p2.y * v1.x - p1.y * v1.x - v1.y * p2.x + v1.y * p1.x) / (v1.y * v2.x - v2.y * v1.x)
    val alpha = (p2.x + v2.x * beta - p1.x) / v1.x
    val c1 = p1.z + v1.z * alpha
    val c2 = p2.z + v2.z * beta
    return if (c1 == c2) {
        val x = p1.x + v1.x * alpha
        val y = p1.y + v1.y * alpha
        val z = p1.z + v1.z * alpha
        DoublePoint(x, y, z)
    } else {
        null
    }
}