package net.nooii.adventofcode.helpers

/**
 * Represents a 2D map of points with associated values of type T.
 *
 * @param T The type of values stored in the map.
 * @property width The width of the map.
 * @property height The height of the map.
 * @param underlying The underlying mutable [MutableNNMap] to store the point-value pairs. Defaults to an empty mutable map.
 */
class PointMap<T : Any>(
    val width: Int,
    val height: Int,
    underlying: MutableNNMap<Point, T> = mutableNNMapOf()
) : MutableNNMap<Point, T>(underlying) {

    /**
     * Checks if a given point is within the bounds of the map.
     *
     * @param point The point to check.
     * @return true if the point is within the map's bounds, false otherwise.
     */
    fun isInRange(point: Point): Boolean {
        return point.x in 0 until width && point.y in 0 until height
    }

    companion object {

        /**
         * Creates a new PointMap filled with values generated by the provided function.
         *
         * @param T The type of values in the map.
         * @param width The width of the map.
         * @param height The height of the map.
         * @param value A function that takes x and y coordinates and returns a value of type T.
         * @return A new PointMap filled with values generated by the provided function.
         */
        fun <T : Any> filled(width: Int, height: Int, value: (x: Int, y: Int) -> T): PointMap<T> {
            val pointMap = PointMap<T>(width, height)
            for (y in 0 until height) {
                for (x in 0 until width) {
                    pointMap[Point(x, y)] = value.invoke(x, y)
                }
            }
            return pointMap
        }

        /**
         * Creates a new PointMap filled with a single value.
         *
         * @param T The type of values in the map.
         * @param width The width of the map.
         * @param height The height of the map.
         * @param value The value to fill the map with.
         * @return A new PointMap filled with the provided value.
         */
        fun <T : Any> filled(width: Int, height: Int, value: T): PointMap<T> {
            return filled(width, height) { _, _ -> value }
        }
    }
}

/**
 * Creates a copy of the current [PointMap].
 *
 * This function creates a new [PointMap] instance with the same dimensions as the original,
 * and copies all key-value pairs from the original map to the new one.
 *
 * @param T The type of values stored in the map.
 * @return A new [PointMap] instance containing all the key-value pairs of the original map.
 */
fun <T : Any> PointMap<T>.copy(): PointMap<T> {
    return PointMap<T>(this.width, this.height).also {
        it.putAll(this)
    }
}