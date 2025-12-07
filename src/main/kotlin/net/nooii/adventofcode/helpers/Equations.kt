package net.nooii.adventofcode.helpers

import kotlin.math.sqrt

/**
 * Solves a system of two linear equations represented in matrix form.
 *
 * The system is represented as:
 * a[0] * x + a[1] * y = a[2]
 * b[0] * x + b[1] * y = b[2]
 *
 * @param a LongArray representing the coefficients and constant of the first equation (a[0], a[1], a[2])
 * @param b LongArray representing the coefficients and constant of the second equation (b[0], b[1], b[2])
 * @return A Pair<Long, Long> representing the solution (x, y) if it exists and is integral,
 *         or null if no integral solution exists or the system is inconsistent
 */
fun solveTwoLinearEquations(a: LongArray, b: LongArray): Pair<Long, Long>? {
    if (a.size != 3 || b.size != 3) {
        error("a and b must have exactly 3 elements each")
    }
    val xTop = b[1] * a[2] - a[1] * b[2]
    val xBottom = a[0] * b[1] - b[0] * a[1]
    if (xBottom == 0L || xTop % xBottom != 0L) {
        return null
    }
    val x = xTop / xBottom
    val yTop = a[2] - a[0] * x
    val yBottom = a[1]
    if (yBottom == 0L || yTop % yBottom != 0L) {
        return null
    }
    val y = yTop / yBottom
    return x to y
}

/**
 * Solves a quadratic equation of the form ax^2 + bx + c = 0.
 * Also handles linear equations if a = 0.
 *
 * @param a The coefficient of the x^2 term.
 * @param b The coefficient of the x term.
 * @param c The constant term.
 * @return A list of real roots.
 *         - Returns two roots if the discriminant is positive.
 *         - Returns one root if the discriminant is zero or if the equation is linear.
 *         - Returns an empty list if there are no real roots or if the equation is a contradiction (e.g., 0x^2 + 0x + 5 = 0).
 *         - Returns a list containing [Double.NaN] if the equation is an identity (0x^2 + 0x + 0 = 0), as any number is a solution.
 */
fun solveQuadraticEquation(a: Double, b: Double, c: Double): List<Double> {
    when {
        a != 0.0 -> {
            val discriminant = b * b - 4.0 * a * c
            return if (discriminant > 0) {
                listOf((-b + sqrt(discriminant)) / (2.0 * a), (-b - sqrt(discriminant)) / (2.0 * a))
            } else if (discriminant == 0.0) {
                listOf(-b / (2.0 * a))
            } else {
                listOf()
            }
        }
        b != 0.0 -> return listOf(-c / b)
        c != 0.0 -> return listOf()
        else -> return listOf(Double.NaN)
    }
}
