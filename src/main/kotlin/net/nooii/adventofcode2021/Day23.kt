package net.nooii.adventofcode2021

import net.nooii.adventofcode2021.helpers.InputLoader
import java.awt.Point
import java.util.*
import kotlin.math.abs
import kotlin.math.sign

/**
 * Created by Nooii on 23.12.2021
 */
class Day23 {

    private class InitialConfiguration(
        val topRoom : List<Amphipod>,
        val bottomRoom : List<Amphipod>
    )

    private sealed class Amphipod(val x : Int, val cost : Long) {
        class Amber : Amphipod(3, 1) {
            override fun toString() = "A"
        }

        class Bronze : Amphipod(5, 10) {
            override fun toString() = "B"
        }

        class Copper : Amphipod(7, 100) {
            override fun toString() = "C"
        }

        class Desert : Amphipod(9, 1000) {
            override fun toString() = "D"
        }
    }

    private class WeightedField(
        val cost : Int,
        val field : Field
    ) {
        override fun equals(other : Any?) : Boolean {
            if (this === other) return true
            if (other !is WeightedField) return false

            if (field != other.field) return false

            return true
        }

        override fun hashCode() : Int {
            return field.hashCode()
        }
    }

    private sealed class Field(val point : Point) {

        class Hallway(point : Point) : Field(point) {
            override fun toString() = "."
        }

        class Room(point : Point, val accepts : Class<out Amphipod>) : Field(point) {
            override fun toString() = accepts.simpleName
        }

        override fun equals(other : Any?) : Boolean {
            if (this === other) return true
            if (other !is Field) return false

            if (point != other.point) return false

            return true
        }

        override fun hashCode() : Int {
            return point.hashCode()
        }

    }

    private class State(
        var cost : Long,
        val fields : Map<Point, Field>,
        val amphipodsToPoints : Map<Amphipod, Point>,
        val pointsToAmphipods : Map<Point, Amphipod>
    ) {

        var prev : State? = null

        fun copy(cost : Long, target : Field, amphipod : Amphipod) : State {
            // Create a copy of state where amphipod is moved to target point
            return State(
                cost = cost,
                fields = fields,
                amphipodsToPoints = amphipodsToPoints.toMutableMap().also { m ->
                    m[amphipod] = target.point
                },
                pointsToAmphipods = pointsToAmphipods.toMutableMap().also { m ->
                    amphipodsToPoints[amphipod]?.let { m.remove(it) }
                    m[target.point] = amphipod
                }
            ).also { it.prev = this }
        }

        fun isEndState() : Boolean {
            return amphipodsToPoints.all { (a, p) ->
                val f = fields[p]
                f is Field.Room && f.accepts == a.javaClass
            }
        }

        private fun isFieldAccepted(field : Field, amphipod : Amphipod) : Boolean {
            return when (field) {
                is Field.Hallway -> !fields.containsKey(Point(field.point.x, field.point.y + 1))
                is Field.Room -> field.accepts.isInstance(amphipod)
            }
        }

        private fun isFieldBlocked(field : Field) : Boolean {
            return pointsToAmphipods.containsKey(field.point)
        }

        private fun moveToJunctionAbove(amphipod : Amphipod, from : Field.Room) : Pair<Long, Field>? {
            // Find junction above
            val fieldOneAbove = fields[Point(from.point.x, from.point.y - 1)] ?: throw IllegalStateException("No point above")
            val costToJunction : Long
            val junction : Field
            if (fieldOneAbove is Field.Hallway) {
                costToJunction = amphipod.cost
                junction = fieldOneAbove
            } else {
                if (isFieldBlocked(fieldOneAbove)) {
                    return null
                }
                junction = fields[Point(from.point.x, from.point.y - 2)] ?: throw IllegalStateException("No junction above")
                assert(junction is Field.Hallway)
                costToJunction = amphipod.cost * 2
            }
            return Pair(costToJunction, junction)
        }

        private fun moveInHallway(
            amphipod : Amphipod,
            from : Field,
            initialCost : Long,
            diff : Int,
            onPointFound : (Long, Field) -> Unit = { _, _ -> }
        ) : Boolean {
            val target = fields[Point(from.point.x + diff, from.point.y)] ?: return false
            if (isFieldBlocked(target)) {
                return false
            }
            if (isFieldAccepted(target, amphipod)) {
                onPointFound.invoke(initialCost + abs(diff) * amphipod.cost, target)
            }
            return true
        }

        private fun findHallwayDestinations(amphipod : Amphipod, from : Field.Room, onPointFound : (Long, Field) -> Unit) {
            val (junctionCost, junction) = moveToJunctionAbove(amphipod, from) ?: return
            var diff = -1
            while (moveInHallway(amphipod, junction, junctionCost, diff, onPointFound)) {
                diff--
            }
            diff = 1
            while (moveInHallway(amphipod, junction, junctionCost, diff, onPointFound)) {
                diff++
            }
        }

        private fun findDestinationRoom(amphipod : Amphipod, from : Field) : Pair<Long, Field>? {
            // The amphipod is in hallway
            val junction = Point(amphipod.x, from.point.y)
            val distance = junction.x - from.point.x
            val costToJunction = abs(distance) * amphipod.cost
            var diff = distance.sign
            while (diff != distance) {
                if (!moveInHallway(amphipod, from, 0, diff)) {
                    return null
                }
                diff += distance.sign
            }
            // Move down
            val oneFieldBelow = fields[Point(junction.x, from.point.y + 1)] as? Field.Room ?: return null
            val twoFieldsBelow = fields[Point(junction.x, from.point.y + 2)] as? Field.Room ?: return null
            // Check one field below
            if (isFieldBlocked(oneFieldBelow)) {
                return null
            }
            //if (oneFieldBelow.accepts.isInstance(amphipod) && twoFieldsBelow.accepts.isInstance(amphipod)) {
            if (isFieldBlocked(twoFieldsBelow)) {
                val amphipodTwoFieldsBelow = pointsToAmphipods[twoFieldsBelow.point]
                if (twoFieldsBelow.accepts.isInstance(amphipodTwoFieldsBelow)) {
                    return Pair(costToJunction + amphipod.cost, oneFieldBelow)
                }
            } else {
                return Pair(costToJunction + amphipod.cost * 2, twoFieldsBelow)
            }
            //}
            return null
        }

        fun getNextStates(amphipod : Amphipod) : Collection<State> {
            val point = amphipodsToPoints[amphipod] ?: return emptySet()
            val field = fields[point] ?: return emptySet()
            val nextStates = mutableSetOf<State>()
            // Inside
            if (field is Field.Room) {
                // Eliminate correct cases
                if (field.accepts.isInstance(amphipod)) {
                    val fieldOneBelow = fields[Point(point.x, point.y + 1)] ?: return emptySet()
                    if (fieldOneBelow is Field.Room) {
                        val amphipodOneBelow = pointsToAmphipods[fieldOneBelow.point]
                        if (fieldOneBelow.accepts.isInstance(amphipodOneBelow)) {
                            return emptySet()
                        }
                    }
                }
                findHallwayDestinations(amphipod, field) { targetCost, target ->
                    nextStates.add(copy(cost + targetCost, target, amphipod))
                }
            } else {
                // Outside - can move to correct room only
                val (targetCost, target) = findDestinationRoom(amphipod, field) ?: return emptySet()
                nextStates.add(copy(cost + targetCost, target, amphipod))
            }
            return nextStates
        }

        fun getNextStates() : MutableSet<State> {
            val nextStates = mutableSetOf<State>()
            for ((amphipod, _) in amphipodsToPoints) {
                nextStates.addAll(getNextStates(amphipod))
            }
            return nextStates
        }

        override fun equals(other : Any?) : Boolean {
            if (this === other) return true
            if (other !is State) return false

            if (amphipodsToPoints != other.amphipodsToPoints) return false

            return true
        }

        override fun hashCode() : Int {
            return amphipodsToPoints.hashCode()
        }

        fun print() {
            println("------------- State (cost = $cost) --------------")
            for (y in 0..4) {
                for (x in 0..12) {
                    val point = Point(x, y)
                    val amphipod = pointsToAmphipods[point]
                    val symbol = when {
                        amphipod != null -> amphipod.toString()
                        fields.containsKey(point) -> "."
                        else -> "#"
                    }
                    print(symbol)
                }
                println()
            }
        }

    }

    companion object {

        @JvmStatic
        fun main(args : Array<String>) {
            val input = InputLoader().loadStrings("Day23Input")
            val state = processInput(input)
            println(solution(state))
        }

        private fun solution(initialState : State) : Long {
            val queue = PriorityQueue<State> { a, b -> a.cost.compareTo(b.cost) }
            queue.add(initialState)
            val visited = mutableSetOf<State>()
            while (queue.isNotEmpty()) {
                val state = queue.poll()
                if (state in visited) {
                    continue
                }
                visited.add(state)
                if (state.isEndState()) {
                    val states = mutableListOf<State>()
                    var s = state
                    while(s != null) {
                        states.add(s)
                        s = s.prev
                    }
                    states.reverse()
                    states.forEach {
                        it.print()
                        println()
                    }
                    return state.cost
                }
                queue.addAll(state.getNextStates())
            }
            return -1
        }

        private fun processInput(input : List<String>) : State {
            val fields = mutableMapOf<Point, Field>()
            val amphipodsToPoints = mutableMapOf<Amphipod, Point>()
            val pointsToAmphipods = mutableMapOf<Point, Amphipod>()
            for ((y, line) in input.withIndex()) {
                for ((x, char) in line.withIndex()) {
                    val point = Point(x, y)
                    val field = when (char) {
                        '.' -> Field.Hallway(point)
                        'A', 'B', 'C', 'D' -> Field.Room(point, xToClass(x))
                        else -> continue
                    }
                    fields[point] = field
                    if (field is Field.Room) {
                        val amphipod = parseAmphipod(char)
                        amphipodsToPoints[amphipod] = point
                        pointsToAmphipods[point] = amphipod
                    }
                }
            }
            return State(0, fields, amphipodsToPoints, pointsToAmphipods)
        }

        private fun xToClass(x : Int) : Class<out Amphipod> {
            return when (x) {
                3 -> Amphipod.Amber::class.java
                5 -> Amphipod.Bronze::class.java
                7 -> Amphipod.Copper::class.java
                9 -> Amphipod.Desert::class.java
                else -> throw IllegalStateException("Invalid room X")
            }
        }

        private fun parseAmphipod(char : Char) : Amphipod {
            return when (char) {
                'A' -> Amphipod.Amber()
                'B' -> Amphipod.Bronze()
                'C' -> Amphipod.Copper()
                'D' -> Amphipod.Desert()
                else -> throw IllegalArgumentException("Unkown amphipod")
            }
        }
    }

}