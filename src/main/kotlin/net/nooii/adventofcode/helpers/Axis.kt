package net.nooii.adventofcode.helpers

/**
 * Represents the possible axes for grid movement.
 */
enum class Axis {
    HORIZONTAL, VERTICAL;

    /**
     * This function returns the opposite axis of the current one:
     * - If the current axis is HORIZONTAL, it returns VERTICAL.
     * - If the current axis is VERTICAL, it returns HORIZONTAL.
     *
     * @return The rotated [Axis].
     */
    fun rotate(): Axis {
        return if (this == HORIZONTAL) VERTICAL else HORIZONTAL
    }
}