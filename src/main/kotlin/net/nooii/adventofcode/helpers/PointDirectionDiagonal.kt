package net.nooii.adventofcode.helpers

import java.awt.Point

enum class PointDirectionDiagonal(val xDiff: Int, val yDiff: Int) {

    NORTH_WEST(-1, -1),
    NORTH(0, -1),
    NORTH_EAST(1, -1),
    EAST(1, 0),
    SOUTH_EAST(1, 1),
    SOUTH(0, 1),
    SOUTH_WEST(-1, 1),
    WEST(-1, 0);

    fun next(point: Point, step: Int = 1): Point {
        return Point(point.x + xDiff * step, point.y + yDiff * step)
    }
}