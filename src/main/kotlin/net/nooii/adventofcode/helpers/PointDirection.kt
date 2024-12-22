package net.nooii.adventofcode.helpers

/**
 * Represents cardinal directions in a 2D space.
 *
 * @property xDiff The change in x-coordinate for this direction.
 * @property yDiff The change in y-coordinate for this direction.
 * @property axis The axis (vertical or horizontal) this direction is aligned with.
 */
enum class PointDirection(val xDiff: Int, val yDiff: Int, val axis: Axis, val arrow: Char) {
    UP(0, -1, Axis.VERTICAL, '^'),
    DOWN(0, 1, Axis.VERTICAL, 'v'),
    LEFT(-1, 0, Axis.HORIZONTAL, '<'),
    RIGHT(1, 0, Axis.HORIZONTAL, '>');

    /**
     * Returns the letter representation of the direction.
     *
     * @return A character representing the direction ('U', 'D', 'L', or 'R').
     */
    fun letter(): Char {
        return when (this) {
            UP -> 'U'
            DOWN -> 'D'
            LEFT -> 'L'
            RIGHT -> 'R'
        }
    }

    /**
     * Calculates the next point in this direction from a given point.
     *
     * @param point The starting point.
     * @param step The number of steps to move in this direction (default is 1).
     * @return A new Point object representing the position after moving in this direction.
     */
    fun next(point: Point, step: Int = 1): Point {
        return Point(point.x + xDiff * step, point.y + yDiff * step)
    }

    /**
     * Rotates the direction 90 degrees clockwise.
     *
     * @return The PointDirection after rotating clockwise.
     */
    fun rotateCW(): PointDirection {
        return when (this) {
            UP -> RIGHT
            RIGHT -> DOWN
            DOWN -> LEFT
            LEFT -> UP
        }
    }

    /**
     * Rotates the direction 90 degrees counter-clockwise.
     *
     * @return The PointDirection after rotating counter-clockwise.
     */
    fun rotateCCW(): PointDirection {
        return when (this) {
            UP -> LEFT
            LEFT -> DOWN
            DOWN -> RIGHT
            RIGHT -> UP
        }
    }

    /**
     * Returns the opposite direction.
     *
     * @return The PointDirection that is opposite to this direction.
     */
    fun mirror(): PointDirection {
        return when (this) {
            UP -> DOWN
            DOWN -> UP
            LEFT -> RIGHT
            RIGHT -> LEFT
        }
    }

    companion object {

        /**
         * Creates a PointDirection from a single character.
         *
         * @param letter A character representing the direction ('U', 'D', 'L', or 'R').
         * @return The corresponding PointDirection.
         */
        fun fromLetter(letter: Char): PointDirection {
            return when (letter) {
                'U' -> UP
                'D' -> DOWN
                'L' -> LEFT
                'R' -> RIGHT
                else -> throw IllegalArgumentException("Invalid direction ($letter)")
            }
        }

        /**
         * Creates a PointDirection from a string representation.
         *
         * @param letter A string representing the direction ("U", "D", "L", or "R").
         * @return The corresponding PointDirection.
         */
        fun fromLetter(letter: String): PointDirection = fromLetter(letter[0])

        /**
         * Creates a PointDirection from a single arrow character.
         *
         * @param arrow A character representing the direction ('>', '<', 'v', or '^').
         * @return The corresponding PointDirection.
         */
        fun fromArrow(arrow: Char): PointDirection {
            return when (arrow) {
                '>' -> RIGHT
                '<' -> LEFT
                'v' -> DOWN
                '^' -> UP
                else -> throw IllegalArgumentException("Invalid direction ($arrow)")
            }
        }

        /**
         * Creates a PointDirection from an arrow string representation.
         *
         * @param arrow A string representing the direction (">", "<", "v", or "^").
         * @return The corresponding PointDirection.
         */
        fun fromArrow(arrow: String): PointDirection = fromArrow(arrow[0])

        /**
         * Determines the direction from one point to another.
         *
         * @param from The starting point.
         * @param to The ending point.
         * @return The PointDirection from the starting point to the ending point.
         * @throws IllegalStateException if the points are the same or not in a straight line.
         */
        fun determine(from: Point, to: Point): PointDirection {
            return when {
                from == to -> error("Points cannot be the same")
                from.y == to.y -> if (to.x > from.x) RIGHT else LEFT
                from.x == to.x -> if (to.y > from.y) DOWN else UP
                else -> error("Cannot determine direction from $from to $to")
            }
        }
    }
}