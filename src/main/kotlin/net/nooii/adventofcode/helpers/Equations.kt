package net.nooii.adventofcode.helpers

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