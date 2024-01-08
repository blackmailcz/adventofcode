package net.nooii.adventofcode.helpers

import java.awt.Point

enum class PointDirection(val xDiff: Int, val yDiff: Int, val axis: Axis) {
    UP(0, -1, Axis.VERTICAL),
    DOWN(0, 1, Axis.VERTICAL),
    LEFT(-1, 0, Axis.HORIZONTAL),
    RIGHT(1, 0, Axis.HORIZONTAL);

    fun letter(): Char {
        return when (this) {
            UP -> 'U'
            DOWN -> 'D'
            LEFT -> 'L'
            RIGHT -> 'R'
        }
    }

    fun next(point: Point, step: Int = 1): Point {
        return Point(point.x + xDiff * step, point.y + yDiff * step)
    }

    fun rotateCW(): PointDirection {
        return when (this) {
            UP -> RIGHT
            RIGHT -> DOWN
            DOWN -> LEFT
            LEFT -> UP
        }
    }

    fun rotateCCW(): PointDirection {
        return when (this) {
            UP -> LEFT
            LEFT -> DOWN
            DOWN -> RIGHT
            RIGHT -> UP
        }
    }

    fun mirror(): PointDirection {
        return when (this) {
            UP -> DOWN
            DOWN -> UP
            LEFT -> RIGHT
            RIGHT -> LEFT
        }
    }

    companion object {

        fun fromLetter(letter: String): PointDirection {
            return when (letter) {
                "U" -> UP
                "D" -> DOWN
                "L" -> LEFT
                "R" -> RIGHT
                else -> throw IllegalArgumentException("Invalid direction ($letter)")
            }
        }

        fun fromArrow(arrow: String): PointDirection {
            return when(arrow) {
                ">" -> RIGHT
                "<" -> LEFT
                "v" -> DOWN
                "^" -> UP
                else -> throw IllegalArgumentException("Invalid direction ($arrow)")
            }
        }

        fun determine(from: Point, to: Point): PointDirection {
            return when {
                from == to -> error("Points cannot be the same")
                from.x == to.x -> if (to.x > from.x) RIGHT else LEFT
                from.y == to.y -> if (to.y > from.y) DOWN else UP
                else -> error("Cannot determine direction from $from to $to")
            }
        }
    }
}