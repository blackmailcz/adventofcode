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

    fun isInRange(point: Point): Boolean {
        return point.x in 0 until width && point.y in 0 until height
    }
}

fun <T : Any> PointMap<T>.copy(): PointMap<T> {
    return PointMap<T>(this.width, this.height).also {
        it.putAll(this)
    }
}