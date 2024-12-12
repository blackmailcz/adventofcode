package net.nooii.adventofcode.aoc2024

import net.nooii.adventofcode.helpers.*
import java.awt.Point

class Day12 {

    /**
     * Represents a touch point between a polygon and its adjacent area.
     *
     * @property polygonPoint The point on the polygon's edge.
     * @property touchPoint The adjacent point outside the polygon that touches the polygon point.
     */
    private data class Touch(
        val polygonPoint: Point,
        val touchPoint: Point
    )

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2024).loadStrings("Day12Input")
            val map = processInput(input)
            val polygons = createPolygons(map)
            part1(polygons)
            part2(polygons)
        }

        private fun part1(polygons: List<Set<Point>>) {
            val sum = polygons.sumOf { it.size * computeTouches(it).size }
            println(sum)
        }

        private fun part2(polygons: List<Set<Point>>) {
            val sum = polygons.sumOf { it.size * computeSides(it) }
            println(sum)
        }

        /**
         * Creates a list of polygons from the given character map.
         *
         * This function iterates through all points in the map and creates polygons
         * by grouping connected points with the same character value. Each polygon
         * is represented as a set of points.
         *
         * @param map A PointMap containing character values for each point.
         * @return A List of Sets, where each Set represents a polygon as a collection of Points.
         */
        private fun createPolygons(map: PointMap<Char>): List<Set<Point>> {
            val visited = mutableSetOf<Point>()
            val polygons = mutableListOf<Set<Point>>()
            forEachPoint(map) { point, _ ->
                if (point !in visited) {
                    polygons += createPolygon(map, visited, point)
                }
            }
            return polygons
        }


        /**
         * Creates a polygon (a set of connected points) starting from an initial point.
         *
         * This function performs a breadth-first search to find all connected points
         * with the same character value in the map, forming a polygon.
         *
         * @param map The PointMap containing the character values for each point.
         * @param visited A mutable set to keep track of points that have been visited.
         * @param initialPoint The starting point for creating the polygon.
         * @return A Set of Points representing the created polygon.
         */
        private fun createPolygon(map: PointMap<Char>, visited: MutableSet<Point>, initialPoint: Point): Set<Point> {
            val polygon = mutableSetOf<Point>()
            var points = setOf(initialPoint)
            while (points.isNotEmpty()) {
                val nextPoints = mutableSetOf<Point>()
                for (point in points) {
                    visited.add(point)
                    polygon.add(point)
                    for (direction in PointDirection.entries) {
                        val next = direction.next(point)
                        if (next !in visited && map.isInRange(next) && map[next] == map[point]) {
                            nextPoints.add(next)
                            visited.add(next)
                            polygon.add(next)
                        }
                    }
                    points = nextPoints
                }
            }
            return polygon
        }

        /**
         * Computes all the touch points for a given polygon.
         *
         * This function identifies all points that are adjacent to the polygon but not part of it.
         * It checks in all directions (up, down, left, right) for each point in the polygon.
         *
         * @param polygon A Set of Points representing the polygon.
         * @return A Set of Touch objects, each representing a point in the polygon and its adjacent point outside the polygon.
         */
        private fun computeTouches(polygon: Set<Point>): Set<Touch> {
            return polygon
                .flatMap { point ->
                    PointDirection.entries.mapNotNull { direction ->
                        direction.next(point).takeIf { it !in polygon }?.let { Touch(point, it) }
                    }
                }.toSet()
        }

        /**
         * Computes the number of sides of a polygon.
         *
         * This function calculates the number of distinct sides of a polygon by analyzing its touch points
         * and excluding adjacent touches that are part of the same side.
         *
         * @param polygon A Set of Points representing the polygon for which to compute the sides.
         * @return An Int representing the number of sides of the polygon.
         */
        private fun computeSides(polygon: Set<Point>): Int {
            val touches = computeTouches(polygon)
            val exclusions = mutableSetOf<Touch>()
            var sides = 0
            for (touch in touches) {
                if (touch !in exclusions) {
                    sides++
                    exclusions.addAll(computeExclusions(touch, touches))
                }
            }
            return sides
        }

        /**
         * Computes the set of touches that should be excluded when counting sides of a polygon.
         *
         * This function determines the axis of the touch and then finds adjacent touches in both
         * positive and negative directions along that axis.
         *
         * @param touch The Touch object for which to compute exclusions.
         * @param touches The set of all Touch objects for the polygon.
         * @return A Set of Touch objects that should be excluded when counting sides.
         */
        private fun computeExclusions(touch: Touch, touches: Set<Touch>): Set<Touch> {
            val axis = PointDirection.determine(touch.polygonPoint, touch.touchPoint).axis
            return adjacentTouches(touch, axis, touches, 1) + adjacentTouches(touch, axis, touches, -1)
        }

        /**
         * Finds adjacent touches along a specified axis from a given touch point.
         *
         * This function iterates along the specified axis (horizontal or vertical) from the given touch point,
         * collecting adjacent touches until it finds a point that is not in the set of touches.
         *
         * @param touch The starting Touch object from which to find adjacent touches.
         * @param axis The axis (HORIZONTAL or VERTICAL) along which to search for adjacent touches.
         * @param touches The set of all Touch objects to search within.
         * @param step The direction to search: 1 for positive direction, -1 for negative direction.
         * @return A Set of Touch objects that are adjacent to the given touch along the specified axis and direction.
         */
        private fun adjacentTouches(touch: Touch, axis: Axis, touches: Set<Touch>, step: Int): Set<Touch> {
            val adjacentTouches = mutableSetOf<Touch>()
            var distance = step
            do {
                val adjacentTouch = when (axis) {
                    Axis.HORIZONTAL -> {
                        Touch(
                            polygonPoint = Point(touch.polygonPoint.x + distance, touch.polygonPoint.y),
                            touchPoint = Point(touch.touchPoint.x + distance, touch.touchPoint.y)
                        )
                    }
                    Axis.VERTICAL -> {
                        Touch(
                            polygonPoint = Point(touch.polygonPoint.x, touch.polygonPoint.y + distance),
                            touchPoint = Point(touch.touchPoint.x, touch.touchPoint.y + distance)
                        )
                    }
                }
                val touchFound = (adjacentTouch in touches).also { adjacentTouches.add(adjacentTouch) }
                distance += step
            } while (touchFound)
            return adjacentTouches
        }

        private fun processInput(input: List<String>): PointMap<Char> {
            return PointMap.filled(input.first().length, input.size) { x, y -> input[y][x] }
        }
    }
}