package net.nooii.adventofcode.aoc2022

import net.nooii.adventofcode.helpers.*
import kotlin.math.max

class Day16 {

    private class Valve(
        val id: String,
        val flowRate: Int,
        val nextValves: List<String>
    ) {

        // Precomputed to speed up time
        val timeToOpenValve = NonNullMap<Valve, Int>()
    }

    private sealed class State(
        val closedValves: Set<Valve>,
        val score: Int
    ) {

        abstract fun nextStates(): List<State>
    }

    private class State1(
        closedValves: Set<Valve>,
        val currentValve: Valve,
        score: Int,
        val remainingTime: Int,
        val path: List<String> = emptyList()
    ) : State(closedValves, score) {

        override fun nextStates(): List<State1> {
            val testResult = computeMoves(
                closedValves = closedValves,
                time = remainingTime,
                valve = currentValve
            )
            return testResult.map { (valve, time) ->
                State1(
                    currentValve = valve,
                    closedValves = closedValves - valve,
                    score = score + valve.flowRate * time,
                    remainingTime = time,
                    path = path + valve.id
                )
            }
        }
    }

    private class State2(
        val yourFirstValve: String,
        closedValves: Set<Valve>,
        val yourCurrentValve: Valve,
        val elephantCurrentValve: Valve,
        score: Int,
        val yourRemainingTime: Int,
        val elephantRemainingTime: Int
    ) : State(closedValves, score) {

        override fun nextStates(): List<State2> {

            var yourMoves = computeMoves(
                closedValves = closedValves,
                time = yourRemainingTime,
                valve = yourCurrentValve
            )

            if (yourRemainingTime == TIME_2) {
                // Significant speedup
                yourMoves = yourMoves.filterKeys { it.id == yourFirstValve }
            }

            val elephantMoves = computeMoves(
                closedValves = closedValves,
                time = elephantRemainingTime,
                valve = elephantCurrentValve
            )

            val nextStates = mutableListOf<State2>()

            when {
                yourMoves.isEmpty() && elephantMoves.isNotEmpty() -> {
                    for ((elephantValve, elephantTime) in elephantMoves) {
                        nextStates.add(
                            State2(
                                yourFirstValve = yourFirstValve,
                                closedValves = closedValves - elephantValve,
                                score = score + elephantValve.flowRate * elephantTime,
                                yourCurrentValve = yourCurrentValve,
                                elephantCurrentValve = elephantValve,
                                yourRemainingTime = yourRemainingTime,
                                elephantRemainingTime = elephantTime
                            )
                        )
                    }
                }
                yourMoves.isNotEmpty() && elephantMoves.isEmpty() -> {
                    for ((yourValve, yourTime) in yourMoves) {
                        nextStates.add(
                            State2(
                                yourFirstValve = yourFirstValve,
                                closedValves = closedValves - yourValve,
                                score = score + yourValve.flowRate * yourTime,
                                yourCurrentValve = yourValve,
                                elephantCurrentValve = elephantCurrentValve,
                                yourRemainingTime = yourTime,
                                elephantRemainingTime = elephantRemainingTime
                            )
                        )
                    }
                }
                else -> {
                    for ((yourValve, yourTime) in yourMoves) {
                        for ((elephantValve, elephantTime) in elephantMoves) {
                            // If both you and elephant hit the same valve in the same iteration, count the better score.
                            val nextScore = if (yourValve == elephantValve) {
                                max(yourValve.flowRate * yourTime, elephantValve.flowRate * elephantTime)
                            } else {
                                yourValve.flowRate * yourTime + elephantValve.flowRate * elephantTime
                            }
                            nextStates.add(
                                State2(
                                    yourFirstValve = yourFirstValve,
                                    closedValves = closedValves - setOf(yourValve, elephantValve),
                                    score = score + nextScore,
                                    yourCurrentValve = yourValve,
                                    elephantCurrentValve = elephantValve,
                                    yourRemainingTime = yourTime,
                                    elephantRemainingTime = elephantTime
                                )
                            )
                        }
                    }
                }
            }
            return nextStates
        }
    }

    companion object {

        private const val TIME_1 = 30
        private const val TIME_2 = 26

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2022).loadStrings("Day16Input")
            val valves = parseInput(input)
            precompute(valves)
            // Runtime ~ 300 ms
            val yourFirstValve = part1(valves)
            // Runtime ~ 16 seconds
            part2(valves, yourFirstValve)
        }

        private fun part1(valves: NonNullMap<String, Valve>): String {
            val startState = State1(
                closedValves = valves.values.filter { it.flowRate > 0 }.toSet(),
                currentValve = valves["AA"],
                score = 0,
                remainingTime = TIME_1
            )
            val finalState = solution(startState)
            println(finalState.score)
            // Return first visited valve to speed up part 2
            return (finalState as State1).path.first()
        }

        private fun part2(valves: NonNullMap<String, Valve>, yourFirstValve: String) {
            val startState = State2(
                yourFirstValve = yourFirstValve,
                closedValves = valves.values.filter { it.flowRate > 0 }.toSet(),
                yourCurrentValve = valves["AA"],
                elephantCurrentValve = valves["AA"],
                score = 0,
                yourRemainingTime = TIME_2,
                elephantRemainingTime = TIME_2
            )
            val finalState = solution(startState)
            println(finalState.score)
        }

        private fun solution(startState: State): State {
            var remainingStates = mutableListOf(startState)
            var finalState = startState
            while (remainingStates.isNotEmpty()) {
                val nextStates = mutableListOf<State>()
                for (state in remainingStates) {
                    if (state.score > finalState.score) {
                        finalState = state
                    }
                    nextStates.addAll(state.nextStates())
                }
                remainingStates = nextStates
            }
            return finalState
        }

        private fun computeMoves(closedValves: Set<Valve>, time: Int, valve: Valve): Map<Valve, Int> {
            val timeMap = mutableMapOf<Valve, Int>()
            for (nextValve in closedValves) {
                val nextTime = valve.timeToOpenValve[nextValve]
                // Filter out moves that would yield zero score = not worth opening the valve.
                if (nextValve.flowRate * (time - nextTime) > 0) {
                    timeMap[nextValve] = time - nextTime
                }
            }
            return timeMap
        }

        private fun precompute(valves: NonNullMap<String, Valve>) {
            for (valve in valves.values) {
                var time = 0
                val visited = mutableSetOf<Valve>()
                var currentValves = mutableListOf(valve)
                while (currentValves.isNotEmpty()) {
                    val nextCurrent = mutableListOf<Valve>()
                    for (current in currentValves) {
                        visited.add(current)
                        valve.timeToOpenValve[current] = time + 1
                        for (nextId in current.nextValves) {
                            val next = valves[nextId]
                            if (next !in visited) {
                                nextCurrent.add(next)
                            }
                        }
                    }
                    currentValves = nextCurrent
                    time++
                }
            }
        }

        private fun parseInput(input: List<String>): NonNullMap<String, Valve> {
            return NonNullMap(input.associate { line ->
                val data = Regex("Valve (\\w+) has flow rate=(\\d+); tunnels? leads? to valves? (.*)")
                    .captureFirstMatch(line) { it }
                val valve = Valve(
                    data[0], data[1].toInt(), data[2].split(", ")
                )
                data[0] to valve
            }.toMutableMap())
        }
    }
}