package net.nooii.adventofcode.helpers

import java.awt.Point

class PointMap<T: Any>(
    val width: Int,
    val height: Int,
    private val underlying: MutableMap<Point, T> = HashMap()
) : MutableMap<Point, T> by underlying {

    override fun get(key: Point): T {
        return underlying[key]!!
    }
}