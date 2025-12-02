package net.nooii.adventofcode.aoc2017

import net.nooii.adventofcode.helpers.*

object Day7 {

    private data class Program(
        val name: String,
        val weight: Int,
        val children: List<String>
    )

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2017).loadStrings("Day7Input")
        val programs = processInput(input)
        part1(programs)
        part2(programs)
    }

    private fun part1(programs: NNMap<String, Program>) {
        val bottomProgram = findBottomProgram(programs)
        println(bottomProgram.name)
    }

    private fun part2(programs: NNMap<String, Program>) {
        val bottomProgram = findBottomProgram(programs)
        // Compute all weights
        val weights = mutableNNMapOf<String, Int>()
        computeWeight(bottomProgram, programs, weights)
        // Now find the topmost unbalanced program
        var unbalancedPrograms = mutableSetOf(bottomProgram)
        while (unbalancedPrograms.isNotEmpty()) {
            val nextUnbalanced = mutableSetOf<Program>()
            for (program in unbalancedPrograms) {
                if (!hasBalancedChildren(program, weights)) {
                    // If any program has not balanced children, then check its grandchildren
                    val unbalancedGrandchildren = program.children.filter { !hasBalancedChildren(programs[it], weights) }
                    if (unbalancedGrandchildren.isEmpty()) {
                        // This means, one of the children is not balanced
                        val occurrenceMap = mutableMapOf<Int, MutableSet<String>>()
                        for (child in program.children) {
                            occurrenceMap.computeIfAbsent(weights[child]) { mutableSetOf() }.add(child)
                        }
                        if (occurrenceMap.size != 2 || occurrenceMap.minBy { it.value.size }.value.size != 1) {
                            error("More than one unbalanced programs found")
                        }
                        val targetProgram = occurrenceMap.minBy { it.value.size }.value.first()
                        val correctTotalWeight = occurrenceMap.maxBy { it.value.size }.key
                        val correctWeight = correctTotalWeight - weights[targetProgram] + programs[targetProgram].weight
                        println(correctWeight)
                        return
                    } else {
                        nextUnbalanced.addAll(unbalancedGrandchildren.map { programs[it] })
                    }
                }
            }
            unbalancedPrograms = nextUnbalanced
        }
        error("No solution found")
    }

    private fun hasBalancedChildren(program: Program, weights: MutableNNMap<String, Int>): Boolean {
        return program.children.map { weights[it] }.toSet().size == 1
    }

    private fun computeWeight(program: Program, programs: NNMap<String, Program>, weights: MutableNNMap<String, Int>): Int {
        val weight = program.weight + program.children.sumOf { computeWeight(programs[it], programs, weights) }
        weights[program.name] = weight
        return weight
    }

    private fun findBottomProgram(programs: Map<String, Program>): Program {
        for (program in programs.values) {
            if (program.children.isNotEmpty() && programs.values.none { it.children.contains(program.name) }) {
                return program
            }
        }
        error("No bottom program found")
    }

    private fun processInput(input: List<String>): NNMap<String, Program> {
        val programs = mutableNNMapOf<String, Program>()
        val regex = Regex("(\\w+) \\((\\d+)\\)")
        for (line in input) {
            val (name, weight) = regex.captureFirstMatch(line)
            val children = mutableListOf<String>()
            if (line.contains("-> ")) {
                children.addAll(line.substringAfter("-> ").split(", "))
            }
            programs[name] = Program(name, weight.toInt(), children)
        }
        return programs
    }
}