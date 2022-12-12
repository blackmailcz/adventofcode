package net.nooii.adventofcode.helpers

import java.awt.Point

enum class PointDirection(val xDiff: Int, val yDiff: Int) {
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0);

    fun next(point: Point, step: Int = 1): Point {
        return Point(point.x + xDiff * step, point.y + yDiff * step)
    }

    companion object {

        fun fromLetter(letter: String): PointDirection {
            return when(letter) {
                "U" -> UP
                "D" -> DOWN
                "L" -> LEFT
                "R" -> RIGHT
                else -> throw IllegalArgumentException("Invalid direction ($letter)")
            }
        }
    }
}