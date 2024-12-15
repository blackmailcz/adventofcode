package net.nooii.adventofcode.helpers

/**
 * Represents directions in a 2D space, including diagonal directions.
 * Each direction is defined by its x and y differences from the origin.
 *
 * @property xDiff The difference in x-coordinate when moving in this direction.
 * @property yDiff The difference in y-coordinate when moving in this direction.
 */
enum class PointDirectionDiagonal(val xDiff: Int, val yDiff: Int) {

    NORTH_WEST(-1, -1),
    NORTH(0, -1),
    NORTH_EAST(1, -1),
    EAST(1, 0),
    SOUTH_EAST(1, 1),
    SOUTH(0, 1),
    SOUTH_WEST(-1, 1),
    WEST(-1, 0);

    /**
     * Calculates the next point in this direction from a given point.
     *
     * @param point The starting point.
     * @param step The number of steps to take in this direction. Defaults to 1.
     * @return A new Point that is [step] steps away from [point] in this direction.
     */
    fun next(point: Point, step: Int = 1): Point {
        return Point(point.x + xDiff * step, point.y + yDiff * step)
    }

    /**
     * Returns the opposite direction of this direction.
     *
     * @return The PointDirectionDiagonal that is opposite to this direction.
     */
    fun mirror(): PointDirectionDiagonal {
        return when (this) {
            NORTH_WEST -> SOUTH_EAST
            NORTH_EAST -> SOUTH_WEST
            SOUTH_EAST -> NORTH_WEST
            SOUTH_WEST -> NORTH_EAST
            EAST -> WEST
            WEST -> EAST
            SOUTH -> NORTH
            NORTH -> SOUTH
        }
    }

    companion object {

        /**
         * Returns a set of all diagonal directions.
         *
         * @return A Set containing the four diagonal PointDirectionDiagonal values.
         */
        fun diagonals(): Set<PointDirectionDiagonal> {
            return setOf(
                NORTH_WEST,
                NORTH_EAST,
                SOUTH_WEST,
                SOUTH_EAST
            )
        }
    }
}