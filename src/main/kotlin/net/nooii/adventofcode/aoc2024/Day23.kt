package net.nooii.adventofcode.aoc2024

import net.nooii.adventofcode.helpers.*

class Day23 {

    private class CacheKey(
        val computers: Set<String>,
        val candidates: Set<String>
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is CacheKey) return false

            if (computers != other.computers) return false

            return true
        }

        override fun hashCode(): Int {
            return computers.hashCode()
        }
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2024).loadStrings("Day23Input")
            val connections = processInput(input).nn()
            part1(connections)
            // Runtime ~ 1.3 second
            part2(connections)
        }

        private fun part1(connections: NNMap<String, Set<String>>) {
            val threeConnections = mutableSetOf<Set<String>>()
            for (computer1 in connections.keys) {
                for (computer2 in connections[computer1]) {
                    for (computer3 in connections[computer2]) {
                        if (computer3 in connections[computer1]) {
                            val connection = setOf(computer1, computer2, computer3)
                            if (connection.any { it.startsWith('t') }) {
                                threeConnections.add(connection)
                            }
                        }
                    }
                }
            }
            println(threeConnections.size)
        }

        private fun part2(connections: NNMap<String, Set<String>>) {
            var current = connections.keys.map { CacheKey(setOf(it), connections.keys - it) }.toSet()
            while (current.size > 1) {
                val next = mutableSetOf<CacheKey>()
                for (key in current) {
                    // Sharing one instance of all next candidates is more effective despite the duplicate of one candidate (itself)
                    val nextCandidates = key.candidates.toMutableSet()
                    for (candidate in key.candidates) {
                        if (candidate !in key.computers && key.computers.all { candidate in connections[it] }) {
                            next.add(CacheKey(key.computers + candidate, nextCandidates))
                        } else {
                            nextCandidates.remove(candidate)
                        }
                    }
                }
                current = next
            }
            println(current.first().computers.toSortedSet().joinToString(","))
        }

        private fun processInput(input: List<String>): Map<String, Set<String>> {
            val regex = Regex("(\\S+)-(\\S+)")
            val map = mutableMapOf<String, MutableSet<String>>()
            for (connection in input) {
                val (from, to) = regex.captureFirstMatch(connection)
                map.getOrPut(from) { mutableSetOf() }.add(to)
                map.getOrPut(to) { mutableSetOf() }.add(from)
            }
            return map
        }
    }
}