package net.nooii.adventofcode.helpers

enum class Axis {
    HORIZONTAL, VERTICAL;

    fun rotate(): Axis {
        return if (this == HORIZONTAL) VERTICAL else HORIZONTAL
    }
}