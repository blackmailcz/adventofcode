package net.nooii.adventofcode.aoc2017

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.NNMap
import net.nooii.adventofcode.helpers.nn

object Day12 {

    private data class Program(
        val id: Int,
        val connections: Set<Int>
    )

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2017).loadStrings("Day12Input")
        val programs = processInput(input)
        part1(programs)
        part2(programs)
    }

    private fun part1(programs: NNMap<Int, Program>) {
        val connectionsToZero = mutableSetOf<Int>()
        for (program in programs.values) {
            if (!connectionsToZero.contains(program.id)) {
                connectionsToZero.addAll(findConnectionsToTarget(program, 0, programs))
            }
        }
        println(connectionsToZero.size)
    }

    private fun part2(programs: NNMap<Int, Program>) {
        val remainingPrograms = programs.keys.toMutableSet()
        var groups = 0
        while (remainingPrograms.isNotEmpty()) {
            val target = remainingPrograms.first()
            val connectionsToTarget = mutableSetOf<Int>()
            for (programId in remainingPrograms) {
                if (!connectionsToTarget.contains(programId)) {
                    connectionsToTarget.addAll(findConnectionsToTarget(programs[programId], target, programs))
                }
            }
            remainingPrograms.removeAll(connectionsToTarget)
            groups++
        }
        println(groups)
    }

    private fun findConnectionsToTarget(
        initialProgram: Program,
        targetProgramId: Int,
        programs: NNMap<Int, Program>
    ): Set<Int> {
        val visited = mutableSetOf<Int>()
        var current = setOf(listOf(initialProgram))
        while (current.isNotEmpty()) {
            val next = mutableSetOf<List<Program>>()
            for (currentPath in current) {
                val program = currentPath.last()
                if (program.id == targetProgramId) {
                    return currentPath.map { it.id }.toSet()
                }
                if (program.id in visited) {
                    continue
                }
                visited.add(program.id)
                next.addAll(program.connections.map { currentPath + programs[it] })
            }
            current = next
        }
        return emptySet()
    }

    private fun processInput(input: List<String>): NNMap<Int, Program> {
        return input.associate { line ->
            val (id, connections) = line.split(" <-> ")
            id.toInt() to Program(id.toInt(), connections.split(", ").map { it.toInt() }.toSet())
        }.nn()
    }
}