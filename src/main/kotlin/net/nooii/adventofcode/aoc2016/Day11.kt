package net.nooii.adventofcode.aoc2016

import com.github.shiguruikai.combinatoricskt.combinations
import net.nooii.adventofcode.aoc2016.Day11.Type.GENERATOR
import net.nooii.adventofcode.aoc2016.Day11.Type.MICROCHIP
import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.add
import net.nooii.adventofcode.helpers.captureFirstMatch

object Day11 {

    // GeneratorMicrochip (= GM)
    private data class GM(
        val generator: Int,
        val microchip: Int
    )

    private enum class Type {
        MICROCHIP, GENERATOR
    }

    private data class State(
        val floor: Int,
        val gm: Map<GM, Int>
    )

    private data class IndexToType(
        val index: Int,
        val type: Type
    )

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2016).loadStrings("Day11Input")
        val state = processInput(input)
        // Runtime ~ 125 ms
        part1(state)
        // Runtime ~ 1 second
        part2(state)
    }

    private fun part1(state: State) {
        println(solution(state))
    }

    private fun part2(state: State) {
        val newMap = LinkedHashMap<GM, Int>()
        newMap.putAll(state.gm)
        newMap.add(GM(1, 1), 2)
        val newState = State(state.floor, newMap)
        // The key to speed up part 2 is to actually remove as many duplicate states as possible
        // The optimizations are following:
        // - Each state is unique - if we reach the same state twice within one timeframe, we can ignore the copy
        // - We track all visited states we've already reached with better time
        // - Each pair is interchangeable, we only save floor of given microchip and its corresponding generator
        //   If there is a same combination of microchip and its generator across floors, we only keep track of
        //   how many such occurrences are there, doesn't matter which pair is which
        // - No usage of fancy functions to maximize the performance:
        //      * One state iteration to break down the state to list
        //      * One iteration for each combination of possible microchip/generator moving
        //      * For each combination, one iteration of broken-down list to validate the move state and combine it back to state
        println(solution(newState))
    }

    private fun solution(initialState: State): Int {
        var states = setOf(initialState)
        var time = 0
        val visited = mutableSetOf<State>()
        while (states.isNotEmpty()) {
            val nextStates = mutableSetOf<State>()
            for (state in states) {
                if (state in visited) {
                    continue
                }
                visited.add(state)
                if (state.gm.keys.all { (g, m) -> g == 4 && m == 4 }) {
                    return time
                }
                if (state.floor + 1 in 1..4) {
                    nextStates.addAll(generateNextStates(visited, state, state.floor + 1))
                }
                if (state.floor - 1 in 1..4) {
                    nextStates.addAll(generateNextStates(visited, state, state.floor - 1))
                }
            }
            time++
            states = nextStates
        }
        error("No solution found")
    }

    private fun generateNextStates(visited: Set<State>, state: State, toFloor: Int): Set<State> {
        var pairIndex = 0
        val combs = mutableListOf<IndexToType>()
        val destructured = mutableListOf<GM>()
        for ((gm, count) in state.gm) {
            repeat(count) {
                if (gm.generator == state.floor) {
                    combs.add(IndexToType(pairIndex, GENERATOR))
                }
                if (gm.microchip == state.floor) {
                    combs.add(IndexToType(pairIndex, MICROCHIP))
                }
                destructured.add(gm)
                pairIndex++
            }
        }
        val nextStates = mutableSetOf<State>()
        comb@
        for (comb in combs.combinations(1)) {
            val oldGMPair = destructured[comb[0].index]
            val newGMPair = when (comb[0].type) {
                GENERATOR -> GM(toFloor, oldGMPair.microchip)
                MICROCHIP -> GM(oldGMPair.generator, toFloor)
            }
            val newGMMap = LinkedHashMap<GM, Int>()
            newGMMap.add(newGMPair, 1)
            for ((i, gm) in destructured.withIndex()) {
                when {
                    i == comb[0].index -> {} // No-op, already processed
                    // c1: adding microchip
                    // c2: check if the added microchip is not connected to the same generator
                    // c3: check if there is any generator on the new floor
                    comb[0].type == MICROCHIP && newGMPair.microchip != newGMPair.generator && gm.generator == toFloor -> {
                        continue@comb // Will be fried
                    }
                    // c1: adding generator
                    // c2: check if any microchip is not connected to the same generator
                    // c3: check if that microchip is on the same floor
                    comb[0].type == GENERATOR && gm.microchip != gm.generator && gm.microchip == toFloor -> {
                        continue@comb // Will be fried
                    }
                    else -> newGMMap.add(gm, 1)
                }
            }
            val nextState = State(toFloor, newGMMap)
            if (nextState !in visited) {
                nextStates.add(nextState)
            }
        }
        comb@
        for (comb in combs.combinations(2)) {
            // Validate the combination
            if (comb[0].index != comb[1].index) {
                // Invalid combination, taking different microchip and generator together
                if (comb[0].type == MICROCHIP && comb[1].type == GENERATOR) {
                    continue@comb
                }
                if (comb[0].type == GENERATOR && comb[1].type == MICROCHIP) {
                    continue@comb
                }
            }
            val oldGMPair1 = destructured[comb[0].index]
            val newGMPair1 = when (comb[0].type) {
                GENERATOR -> GM(toFloor, oldGMPair1.microchip)
                MICROCHIP -> GM(oldGMPair1.generator, toFloor)
            }
            val oldGMPair2 = destructured[comb[1].index]
            val newGMPair2 = when (comb[1].type) {
                GENERATOR -> GM(toFloor, oldGMPair2.microchip)
                MICROCHIP -> GM(oldGMPair2.generator, toFloor)
            }
            // First check if these two can even coexist together
            if (comb[0].type == MICROCHIP && newGMPair1.generator != toFloor && newGMPair2.generator == toFloor) {
                continue@comb // Will be fried
            }
            if (comb[0].type == GENERATOR && newGMPair2.generator != newGMPair2.microchip && newGMPair2.microchip == toFloor) {
                continue@comb // Will be fried
            }
            if (comb[1].type == MICROCHIP && newGMPair2.generator != toFloor && newGMPair1.generator == toFloor) {
                continue@comb // Will be fried
            }
            if (comb[1].type == GENERATOR && newGMPair1.generator != newGMPair1.microchip && newGMPair1.microchip == toFloor) {
                continue@comb // Will be fried
            }
            // Ok, they can coexist together
            val newGMMap = LinkedHashMap<GM, Int>()
            newGMMap.add(newGMPair1, 1)
            newGMMap.add(newGMPair2, 1)
            for ((i, gm) in destructured.withIndex()) {
                when {
                    i == comb[0].index || i == comb[1].index -> {} // No-op, already processed
                    comb[0].type == MICROCHIP && (newGMPair1.microchip != newGMPair1.generator || newGMPair2.microchip != newGMPair2.generator) && gm.generator == toFloor -> {
                        continue@comb // Will be fried
                    }
                    (comb[0].type == GENERATOR || comb[1].type == GENERATOR) && gm.microchip != gm.generator && gm.microchip == toFloor -> {
                        continue@comb // Will be fried
                    }
                    else -> newGMMap.add(gm, 1)
                }
            }
            val nextState = State(toFloor, newGMMap)
            if (nextState !in visited) {
                nextStates.add(nextState)
            }
        }
        return nextStates
    }

    private fun processInput(input: List<String>): State {
        val idMap = mutableMapOf<String, MutableList<Int>>()
        for (line in input) {
            val floor = Regex("The (\\w+) floor").captureFirstMatch(line) {
                when (it) {
                    "first" -> 1
                    "second" -> 2
                    "third" -> 3
                    "fourth" -> 4
                    else -> error("Invalid floor")
                }
            }.first()
            for (id in Regex("(\\w+) generator").findAll(line).map { it.groupValues[1] }) {
                idMap.getOrPut(id) { mutableListOf(floor, -1) }[0] = floor
            }
            for (id in Regex("(\\w+)-compatible").findAll(line).map { it.groupValues[1] }) {
                idMap.getOrPut(id) { mutableListOf(-1, floor) }[1] = floor
            }
        }
        val pairMap = LinkedHashMap<GM, Int>()
        for ((g, m) in idMap.values) {
            pairMap.add(GM(g, m), 1)
        }
        return State(1, pairMap)
    }
}