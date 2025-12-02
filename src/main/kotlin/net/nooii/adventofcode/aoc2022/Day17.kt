package net.nooii.adventofcode.aoc2022

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.Point
import kotlin.math.abs
import kotlin.math.min

object Day17 {

    private enum class JetStreamOffset(val by: Int) {
        LEFT(-1), RIGHT(1)
    }

    private class Rock(val points: Set<Point>) {
        val height = points.maxOf { it.y } + 1
        var offset = Point(0, 0)
    }

    private class PatternData(
        val cut: List<String>,
        val repeat: List<String>
    )

    private class Room(
        val allRocks: List<Rock>,
        val jetStream: List<JetStreamOffset>
    ) {
        private val rockSpawnYSpacing = 3
        private val rockSpawnXOffset = 2
        private val roomWidth = 7

        private var nextRockIndex = 0
        private var nextJetStreamIndex = 0

        val points = mutableSetOf<Point>()

        // Highest point is cached and updated only with any imprint, this is a great speed improvement. Negative coords.
        private var highestPoint = 0

        init {
            // Generate initial floor
            for (x in 0 until roomWidth) {
                points.add(Point(x, 0))
            }
        }

        fun snapshot(): List<String> {
            val strings = mutableListOf<String>()
            for (y in highestPoint..0) {
                strings.add(createLineSnapshot(y))
            }
            return strings.reversed()
        }

        private fun createLineSnapshot(y: Int): String {
            val str = IntRange(0, roomWidth - 1).map { x ->
                if (points.contains(Point(x, y))) "#" else "."
            }
            return str.joinToString("")
        }

        fun getHeight() = abs(highestPoint)

        fun processRock() {
            // Rock will start at point 2 from left and 4 from the highest point in the current area
            val rock = generateRock().apply {
                offset = Point(
                    rockSpawnXOffset,
                    highestPoint - rockSpawnYSpacing - height
                )
            }
            while (true) {
                moveRockInJetStream(rock)
                if (canRockFall(rock)) {
                    rock.offset = rock.offset.copy(y = rock.offset.y + 1)
                } else {
                    imprintRock(rock)
                    break
                }
            }
        }

        private fun canRockFall(rock: Rock): Boolean {
            // Translate all points and check collision
            for (rockPoint in rock.points) {
                val point = Point(
                    rock.offset.x + rockPoint.x,
                    rock.offset.y + rockPoint.y + 1
                )
                if (point in points) {
                    return false
                }
            }
            return true
        }

        private fun imprintRock(rock: Rock) {
            val pointsToImprint = mutableSetOf<Point>()
            for (rockPoint in rock.points) {
                pointsToImprint.add(
                    Point(
                        rockPoint.x + rock.offset.x,
                        rockPoint.y + rock.offset.y
                    )
                )
            }
            points.addAll(pointsToImprint)
            // Update highest point
            highestPoint = min(highestPoint, pointsToImprint.minOf { it.y })
        }

        private fun moveRockInJetStream(rock: Rock) {
            val jetStreamOffset = jetStream[nextJetStreamIndex]
            nextJetStreamIndex = (nextJetStreamIndex + 1) % jetStream.size
            for (rockPoint in rock.points) {
                val targetPoint = Point(
                    rockPoint.x + rock.offset.x + jetStreamOffset.by,
                    rockPoint.y + rock.offset.y
                )
                if (targetPoint in points || targetPoint.x < 0 || targetPoint.x >= roomWidth) {
                    // Cannot move by jet stream
                    return
                }
            }
            rock.offset = rock.offset.copy(x = rock.offset.x + jetStreamOffset.by)
        }

        private fun generateRock(): Rock {
            val rock = allRocks[nextRockIndex]
            nextRockIndex = (nextRockIndex + 1) % allRocks.size
            return rock
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val rocksInput = InputLoader(AoCYear.AOC_2022).loadStrings("Day17Rocks")
        val rocks = parseRocks(rocksInput)
        val input = InputLoader(AoCYear.AOC_2022).loadStrings("Day17Input")
        val jetStream = parseInput(input)
        part1(Room(rocks, jetStream))
        // Runtimes:
        // ~ 2 min 15 sec when analyzing 8000 rocks
        // ~ 1 min 40 sec when analyzing 7000 rocks
        // ~ 1 min 2 sec when analyzing 6000 rocks
        // ~ 1.3 sec with precomputed Cut and Repeat
        val data = findCutAndRepeat(Room(rocks, jetStream), 7000)
        part2(Room(rocks, jetStream), data)
    }

    private fun part2(room: Room, patternData: PatternData) {
        // 1. First part is to find the number of rows that don't match the repeat pattern until it starts to repeat

        // 2. Second part is to find how often the rows repeat. We will find the lowest possible repeat pattern and then
        // count rocks until the pattern is formed.

        // 3. Third part is to find the remainder after repeating to reach final amount of rocks

        // Process rocks until the cut is formed.
        var rocks = 0L
        while (room.snapshot().take(patternData.cut.size) != patternData.cut) {
            room.processRock()
            rocks++
        }
        val rocks1 = rocks
        val height1 = room.getHeight()

        // Process rocks until the repeat pattern is formed.
        rocks = 0
        while (room.snapshot().drop(patternData.cut.size).take(patternData.repeat.size) != patternData.repeat) {
            room.processRock()
            rocks++
        }

        val rocks2 = rocks
        val height2 = room.getHeight()

        val maxRocks = 1_000_000_000_000L

        // We need to compute the number of missing rocks to reach maxRocks
        val remainderRocks = (maxRocks - rocks1) % rocks2

        // Process the remainder
        rocks = 0
        repeat(remainderRocks.toInt()) {
            room.processRock()
            rocks++
        }

        val height3 = room.getHeight() - height2

        // The equation for rocks is: rocks1 + (cycles) * rocks2 + rocks3 = from
        // The equation for height is: height1 + (cycles) * height2 + height3 = totalHeight
        val sum: Long = height1 + ((maxRocks - rocks1) / rocks2) * patternData.repeat.size + height3
        println(sum)
    }

    private fun part1(room: Room) {
        val rocks = 2022
        repeat(rocks) {
            room.processRock()
        }
        println(room.getHeight())
    }

    private fun findCutAndRepeat(room: Room, rocks: Int): PatternData {
        // 7000 seemed to be optimal for most inputs after min-maxing by hand and is on the edge of computability..
        repeat(rocks) {
            room.processRock()
        }
        val snapshot = room.snapshot()

        // The best values are 53,26 for Test input and 2750,421 for puzzle input

        // Test heuristics
//            val minSubChunkSize = 53
//            val minCutSize = 26

        // Puzzle input heuristics
//            val minSubChunkSize = 2750
//            val minCutSize = 421

        // No heuristics
        val minSubChunkSize = snapshot.size / 2
        val minCutSize = 1

        for (cut in minCutSize until (snapshot.size - minSubChunkSize / 2)) {
            val cutSnapshot = snapshot.drop(cut)
            // It is better approach to start with bigger chunks and proceed to smaller ones.
            for (subChunkSize in cutSnapshot.size / 2 downTo 1) {
                if (checkWindowMatch(cutSnapshot, subChunkSize)) {
                    val unoptimizedRepeat = snapshot.drop(cut).take(subChunkSize)
                    val optimizedRepeat = findLowestRepeat(unoptimizedRepeat)
                    return PatternData(
                        cut = snapshot.take(cut),
                        repeat = optimizedRepeat
                    )
                }
            }
        }
        error("No repeating pattern found")
    }

    private fun findLowestRepeat(pattern: List<String>): List<String> {
        var i = 1
        while (!checkWindowMatch(pattern, i)) {
            i++
        }
        return pattern.take(i)
    }

    private fun checkWindowMatch(input: List<String>, windowSize: Int): Boolean {
        val windows = input.windowed(windowSize, windowSize)
        if (windows.size == 1) {
            return false
        }
        for ((windowI, window) in windows.withIndex()) {
            if (windowI > 0 && window != windows[0]) {
                return false
            }
        }
        return true
    }

    private fun parseInput(input: List<String>): List<JetStreamOffset> {
        return input.first().mapNotNull {
            when (it) {
                '<' -> JetStreamOffset.LEFT
                '>' -> JetStreamOffset.RIGHT
                else -> null
            }
        }
    }

    private fun parseRocks(input: List<String>): List<Rock> {
        val ranges = listOf(-1)
            .plus(input.mapIndexedNotNull { index, line -> index.takeIf { line.isEmpty() } })
            .plus(input.size)
        return ranges.windowed(2, 1).map { window ->
            parseRock(input.subList(window[0] + 1, window[1]))
        }
    }

    private fun parseRock(input: List<String>): Rock {
        val points = mutableSetOf<Point>()
        for ((y, line) in input.withIndex()) {
            for ((x, char) in line.withIndex()) {
                if (char == '#') {
                    points.add(Point(x, y))
                }
            }
        }
        return Rock(points)
    }
}
