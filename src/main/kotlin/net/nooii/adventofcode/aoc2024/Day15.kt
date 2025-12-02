package net.nooii.adventofcode.aoc2024

import net.nooii.adventofcode.helpers.*

object Day15 {

    private enum class Tile {
        EMPTY, WALL, BOX, ROBOT
    }

    /**
     * Represents the result of expanding the map.
     *
     * @property area The expanded area represented as a [PointMap] of [Tile]s.
     * @property boxConnections A mutable map of box connections, where each key is a [Point] representing a box's position,
     *                          and the value is the [PointDirection] indicating the connection to its paired box.
     * @property robot The [Point] representing the new position of the robot in the expanded map.
     */
    private data class ExpansionResult(
        val area: PointMap<Tile>,
        val boxConnections: MutableNNMap<Point, PointDirection>,
        val robot: Point
    )

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2024).loadStrings("Day15Input")
        val (mapInput, directionsInput) = input.splitByEmptyLine()
        val area = processMap(mapInput)
        val directions = processDirections(directionsInput)
        part1(area.copy(), directions)
        part2(area.copy(), directions)
    }

    private fun part1(area: PointMap<Tile>, directions: List<PointDirection>) {
        // Perform movement
        var robot = area.filter { it.value == Tile.ROBOT }.keys.first()
        area[robot] = Tile.EMPTY
        for (direction in directions) {
            robot = moveRobot1(robot, direction, area)
        }
        // Compute the sum
        val sum = area.filterValues { it == Tile.BOX }.keys.sumOf { it.x + 100 * it.y }
        println(sum)
    }

    private fun part2(initialArea: PointMap<Tile>, directions: List<PointDirection>) {
        // Expand the map
        val expansionResult = expandMap(initialArea)
        val (area, boxConnections) = expansionResult
        var robot = expansionResult.robot
        area[robot] = Tile.EMPTY
        // Perform movement
        for (direction in directions) {
            robot = moveRobot2(robot, direction, area, boxConnections)
        }
        // Merge the boxes
        val visited = mutableSetOf<Point>()
        val mergedBoxes = mutableSetOf<Set<Point>>()
        for (point in area.filterValues { it == Tile.BOX }.keys) {
            val otherPoint = boxConnections[point].next(point)
            if (point !in visited && otherPoint !in visited) {
                val mergedBox = setOf(point, otherPoint)
                mergedBoxes.add(mergedBox)
                visited.addAll(mergedBox)
            }
        }
        // Compute the sum
        val edge = Point(0, 0)
        val sum = mergedBoxes.sumOf { points ->
            val closestPoint = points.minBy { edge.manhattanDistance(it) }
            closestPoint.x + 100 * closestPoint.y
        }
        println(sum)
    }

    /**
     * Moves the robot in the specified direction on the given area map.
     *
     * This function attempts to move the robot in the given direction, handling different scenarios:
     * - If the next position is out of bounds or a wall, the robot doesn't move.
     * - If the next position is empty, the robot moves there.
     * - If the next position contains a box, the function attempts to push the box (and any contiguous boxes) in the same direction.
     *
     * @param robot The current position of the robot.
     * @param direction The direction in which to move the robot.
     * @param area The map of the area, represented as a PointMap of Tiles.
     * @return The new position of the robot after the move attempt. If the move is not possible, returns the original position.
     */
    private fun moveRobot1(robot: Point, direction: PointDirection, area: PointMap<Tile>): Point {
        val next = direction.next(robot)
        if (!area.isInRange(next)) {
            return robot
        }
        when (area[next]) {
            Tile.EMPTY -> return next
            Tile.BOX -> {
                var nextInDirection = next
                // Keep searching for boxes in a direction until a non-box tile is found
                while (area.isInRange(nextInDirection) && area[nextInDirection] == Tile.BOX) {
                    nextInDirection = direction.next(nextInDirection)
                }
                if (area.isInRange(nextInDirection) && area[nextInDirection] == Tile.EMPTY) {
                    // It's enough to swap first and last point only
                    area[next] = Tile.EMPTY
                    area[nextInDirection] = Tile.BOX
                    return next
                } else {
                    return robot
                }
            }
            else -> return robot
        }
    }

    /**
     * Moves the robot in the specified direction on the given area map, handling box pushing and connections.
     *
     * This function attempts to move the robot in the given direction, handling different scenarios:
     * - If the next position is out of bounds or a wall, the robot doesn't move.
     * - If the next position is empty, the robot moves there.
     * - If the next position contains a box, the function attempts to push the box (and any contiguous boxes) in the same direction,
     *   updating box connections accordingly.
     *
     * The function handles horizontal and vertical movements differently:
     * - For horizontal movements, it pushes a line of boxes if possible.
     * - For vertical movements, it considers connected box pairs and pushes them as a group.
     *
     * @param robot The current position of the robot.
     * @param direction The direction in which to move the robot.
     * @param area The map of the area, represented as a PointMap of Tiles.
     * @param boxConnections A mutable map representing connections between boxes.
     * @return The new position of the robot after the move attempt. If the move is not possible, returns the original position.
     */
    private fun moveRobot2(
        robot: Point,
        direction: PointDirection,
        area: PointMap<Tile>,
        boxConnections: MutableNNMap<Point, PointDirection>
    ): Point {
        val next = direction.next(robot)
        if (!area.isInRange(next)) {
            return robot
        }
        when (area[next]) {
            Tile.EMPTY -> return next
            Tile.BOX -> {
                when (direction.axis) {
                    Axis.HORIZONTAL -> {
                        var nextInDirection = next
                        val boxPoints = mutableSetOf<Point>()
                        // Keep searching for boxes in a direction until a non-box tile is found
                        while (area.isInRange(nextInDirection) && area[nextInDirection] == Tile.BOX) {
                            boxPoints.add(nextInDirection) // Collect boxes on the way
                            nextInDirection = direction.next(nextInDirection)
                        }
                        // That non-box tile must be empty to be able to push the boxes
                        if (area.isInRange(nextInDirection) && area[nextInDirection] == Tile.EMPTY) {
                            // It's enough to swap first and last point only
                            area[next] = Tile.EMPTY
                            area[nextInDirection] = Tile.BOX
                            // Create new box connections and remove the old ones
                            val newBoxConnections = mutableMapOf<Point, PointDirection>()
                            for (point in boxPoints) {
                                // The key of connections is shifted by 1 in the direction, the value remains
                                newBoxConnections[direction.next(point)] = boxConnections[point]
                                boxConnections.remove(point)
                            }
                            // Add new connections after removal of the old ones to avoid interference
                            boxConnections.putAll(newBoxConnections)
                            return next
                        } else {
                            return robot
                        }
                    }
                    Axis.VERTICAL -> {
                        val boxPoints = mutableSetOf<Point>()
                        // Start with next point
                        var points = mutableSetOf(next)
                        while (points.isNotEmpty()) {
                            val nextPoints = mutableSetOf<Point>()
                            for (point in points) {
                                if (!area.isInRange(point) || area[point] == Tile.WALL) {
                                    // If a wall is found or if we get out of range, stop immediately.
                                    return robot
                                } else if (area[point] == Tile.BOX) {
                                    // If we find a box, we have to add all box points to the next round
                                    for (pointOfBox in setOf(point, boxConnections[point].next(point))) {
                                        boxPoints.add(pointOfBox) // Collect boxes on the way
                                        nextPoints.add(direction.next(pointOfBox))
                                    }
                                }
                                // Empty tiles are not further explored
                            }
                            points = nextPoints
                        }
                        // Create new box connections and remove the old ones
                        val newBoxConnections = mutableMapOf<Point, PointDirection>()
                        for (point in boxPoints) {
                            area[point] = Tile.EMPTY // Clear old tile
                            newBoxConnections[direction.next(point)] = boxConnections[point]
                            boxConnections.remove(point)
                        }
                        // Set new tiles and also update box connections
                        for ((point, connectionDirection) in newBoxConnections) {
                            area[point] = Tile.BOX
                            boxConnections[point] = connectionDirection
                        }
                        return next
                    }
                }
            }
            else -> return robot
        }
    }

    /**
     * Expands the initial map by doubling its width while maintaining its height.
     * This function creates an expanded area, establishes box connections, and locates the robot's new position.
     *
     * @param initialArea The original map represented as a [PointMap] of [Tile]s.
     * @return An [ExpansionResult] containing:
     *         - The expanded area as a [PointMap] of [Tile]s
     *         - A map of box connections where each key is a [Point] representing a box's position,
     *           and the value is the [PointDirection] indicating the connection to its paired box
     *         - The new position of the robot as a [Point]
     */
    private fun expandMap(initialArea: PointMap<Tile>): ExpansionResult {
        val boxConnections = mutableNNMapOf<Point, PointDirection>()
        val expandedArea = PointMap<Tile>(initialArea.width * 2, initialArea.height)
        var robot: Point? = null
        forEachPoint(initialArea) { oldPoint, oldTile ->
            val newPoint1 = Point(oldPoint.x * 2, oldPoint.y)
            val newPoint2 = Point(oldPoint.x * 2 + 1, oldPoint.y)
            expandedArea[newPoint1] = oldTile
            expandedArea[newPoint2] = oldTile.takeIf { it != Tile.ROBOT } ?: Tile.EMPTY
            if (oldTile == Tile.ROBOT) {
                robot = newPoint1
            }
            // Establish box connections
            if (oldTile == Tile.BOX) {
                boxConnections[newPoint1] = PointDirection.RIGHT
                boxConnections[newPoint2] = PointDirection.LEFT
            }
        }
        return ExpansionResult(expandedArea, boxConnections, robot!!)
    }

    private fun processDirections(input: List<String>): List<PointDirection> {
        return input.joinToString("").map { PointDirection.fromArrow(it) }
    }

    private fun processMap(input: List<String>): PointMap<Tile> {
        return PointMap.filled(input.first().length, input.size) { x, y ->
            when (val char = input[y][x]) {
                '.' -> Tile.EMPTY
                '#' -> Tile.WALL
                '@' -> Tile.ROBOT
                'O' -> Tile.BOX
                else -> error("Invalid character: $char")
            }
        }
    }
}