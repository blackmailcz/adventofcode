package net.nooii.adventofcode.helpers

import java.awt.Point

class PointMap<T : Any>(
    val width: Int,
    val height: Int,
    underlying: MutableNNMap<Point, T> = mutableNNMapOf()
) : MutableNNMap<Point, T>(underlying) {

    fun isInRange(point: Point): Boolean {
        return point.x in 0 until width && point.y in 0 until height
    }

    companion object {

        fun <T : Any> filled(width: Int, height: Int, value: (x: Int, y: Int) -> T): PointMap<T> {
            val pointMap = PointMap<T>(width, height)
            for (y in 0 until height) {
                for (x in 0 until width) {
                    pointMap[Point(x, y)] = value.invoke(x, y)
                }
            }
            return pointMap
        }

        fun <T : Any> filled(width: Int, height: Int, value: T): PointMap<T> {
            return filled(width, height) { _, _ -> value }
        }
    }
}

fun <T : Any> PointMap<T>.copy(): PointMap<T> {
    return PointMap<T>(this.width, this.height).also {
        it.putAll(this)
    }
}