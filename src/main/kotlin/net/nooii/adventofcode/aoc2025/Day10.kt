package net.nooii.adventofcode.aoc2025

import com.github.shiguruikai.combinatoricskt.combinations
import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.GaussJordanElimination
import net.nooii.adventofcode.helpers.GeneralGaussJordanElimination
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.captureFirstMatch
import net.nooii.adventofcode.helpers.repeat
import java.util.*
import kotlin.math.floor

object Day10 {

    private data class Button(
        val bits: BitSet,
        val onlineBits: Set<Int>
    ): Comparable<Button> {

        override fun compareTo(other: Button): Int {
            return other.onlineBits.size.compareTo(this.onlineBits.size)
        }

        override fun toString(): String {
            return "[${onlineBits.joinToString(",")}]"
        }
    }

    private class Machine(
        val goal: BitSet,
        val buttons: List<Button>,
        val joltages: List<Int>
    )

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2025).loadStrings("Day10Input")
        val machines = processInput(input)
//        part1(machines)
        part2(machines)

        // 24005 too high
    }

    private fun part1(machines: List<Machine>) {
        val sum = machines.sumOf { countPresses1(it) }
        println(sum)
    }

    private fun part2(machines: List<Machine>) {
//        test(machines.first())
//        return

//        val machine = machines.first()
//
//        val array = Array(machine.joltages.size) { DoubleArray(machine.buttons.size) { 0.0 } }
//
//        for ((i, joltage) in machine.joltages.withIndex()) {
//            for ((j, button) in machine.buttons.withIndex()) {
//                array[i][j] = if (i in button.onlineBits) 1.0 else 0.0
//            }
//        }
//
//        for (y in 0 until array.size) {
//            for (x in 0 until array[y].size) {
//                print(array[y][x].toInt())
//            }
//            println()
//        }
//
//        val b = machine.joltages.map { it.toDouble() }.toDoubleArray()
//
//        val gauss = GaussJordanElimination(array, b)
//        val solution = gauss.solution()
//        println(solution?.map { it.toDouble() })
//        println(solution?.sum())

        var s = 0
        for ((i, machine) in machines.withIndex()) {
            println("Machine $i")
            s += solve(machine)
        }
        println("Final $s")


//
//        var sum = 0
//        for ((i, machine) in machines.withIndex()) {
//            println("Machine $i")
//            sum += test(machine)
//        }
//        println("SUM: $sum")
    }

    private fun solve(machine: Machine): Int {
        // Variables to pick:
        val freeVariables = machine.buttons.size - machine.joltages.size
        println(machine.joltages)

        val rawArray = Array(machine.joltages.size) { DoubleArray(machine.buttons.size) { 0.0 } }
        val rawRightSide = Array(machine.joltages.size) { 0.0 }

        for (j in machine.joltages.indices) {
            for ((b, button) in machine.buttons.withIndex()) {
                rawArray[j][b] = if (j in button.onlineBits) 1.0 else 0.0
            }
            rawRightSide[j] = machine.joltages[j].toDouble()
        }

        val gje = GeneralGaussJordanElimination(rawArray, rawRightSide.toDoubleArray())
        val s = gje.solve()
        if (s is GeneralGaussJordanElimination.Result.NeedsFreeVariables) {
            println("Needs free variables: ${s.numberOfFreeVariables}")
            var best = Int.MAX_VALUE
            for (c in IntRange(0, 250).map { it.toDouble() }.repeat(s.numberOfFreeVariables).combinations(s.numberOfFreeVariables)) {
                val gje2 = GeneralGaussJordanElimination(rawArray, rawRightSide.toDoubleArray(), c)
                val solution = gje2.solve()
                if (solution is GeneralGaussJordanElimination.Result.UniqueSolution) {
                    val sss = solution.getSumIfAllNonNegativeIntegers()?.toInt()
                    if (sss != null) {
                        best = minOf(best, sss)
                    }
                }
            }
            println("Best solution: $best")
            return best
        } else if (s is GeneralGaussJordanElimination.Result.UniqueSolution) {
            val solution = s.getSumIfAllNonNegativeIntegers()?.toInt()
            println("Solution: $solution")
            return solution!!
        } else {
            error("????")
        }

        // Now we need to find columns to cut
        if (freeVariables > 0) {

            for (y in 0 until rawArray.size) {
                for (x in 0 until rawArray[y].size) {
                    print(rawArray[y][x].toInt())
                }
                println()
            }
            println(rawRightSide.joinToString())

            val fillers = IntRange(0, 500).toList().repeat(freeVariables)
            val combs = machine.buttons.indices.combinations(freeVariables).map { it.toSet() }
            var matchingComb: Set<Int>? = null
            comb@
            for (comb in combs) {
                for (row in rawArray) {
                    var s = 0
                    for (column in row.indices) {
                        if (column !in comb && row[column] == 1.0) {
                            s++
                        }
                    }
                    if (s == 0) {
                        continue@comb
                    }
                }
                matchingComb = comb
                break
            }
            matchingComb!!

            var best = Int.MAX_VALUE

            for (filler in fillers.combinations(freeVariables)) {

                // Remove columns from array and convert them to B
                val array = Array(machine.joltages.size) { DoubleArray(machine.joltages.size) { 0.0 } }
                val rightSide = rawRightSide.copyOf().toDoubleArray()
                for (j in machine.joltages.indices) {
                    var bb = 0
                    for (b in machine.buttons.indices) {
                        if (b !in matchingComb) {
                            array[j][bb] = rawArray[j][b]
                            bb++
                        } else {
                            val combIndex = matchingComb.indexOf(b).also { check( it != -1) }
                            val fillerValue = filler[combIndex]
                            rightSide[j] = rightSide[j] - rawArray[j][b] * fillerValue
                        }
                    }
                }



                // Array is ready
//
//                println("FOR: $filler")
//
//                for (y in 0 until array.size) {
//                    for (x in 0 until array[y].size) {
//                        print(array[y][x].toInt())
//                    }
//                    println()
//                }

//                println(rightSide.joinToString())



                val gauss = GaussJordanElimination(array, rightSide)
                val gaussSolution = gauss.solution()
                val solution = filterSolution(gaussSolution)
                if (solution != null) {
//                    println("Found solution: $solution")
                    best = minOf(best, solution + filler.sum())
                }

//                println("---")



            }

            println("BEST: $best")
            return best



        } else {
            val array = Array(machine.buttons.size) { DoubleArray(machine.buttons.size) { 0.0 } }
            val rightSide = DoubleArray(machine.buttons.size) { 0.0 }

            val existingRows = mutableSetOf<List<Double>>()
            var rowIndex = 0
            for (j in machine.joltages.indices) {
                println("j $j, Checking row: ${rawArray[j].joinToString()}")
                if (rawArray[j].toList() in existingRows) {
                    println("Row already exists ${rawArray[j].joinToString()}")
                    continue
                }
                existingRows.add(rawArray[j].toList())
                for (b in machine.buttons.indices) {
                    array[rowIndex][b] = rawArray[j][b]
                }
                rightSide[rowIndex] = rawRightSide[j]
                if (existingRows.size == machine.buttons.size) {
                    break
                }
                rowIndex++
            }

            for (y in 0 until array.size) {
                for (x in 0 until array[y].size) {
                    print(array[y][x].toInt())
                }
                println()
            }
            println(rightSide.joinToString())

            val gje = GeneralGaussJordanElimination(rawArray, rawRightSide.toDoubleArray())
            val s = gje.solve()

            if (s is GeneralGaussJordanElimination.Result.NeedsFreeVariables) {
                var best = Int.MAX_VALUE
                for (c in IntRange(-500, 500).map { it.toDouble() }.repeat(s.numberOfFreeVariables).combinations(s.numberOfFreeVariables)) {
                    val gje2 = GeneralGaussJordanElimination(rawArray, rawRightSide.toDoubleArray(), c)
                    val solution = gje2.solve()
                    if (solution is GeneralGaussJordanElimination.Result.UniqueSolution) {
                        val sss = filterSolution(solution.solution)
                        if (sss != null) {
                            best = minOf(best, sss)
                        }
                    }
                }
                println("Best solution: $best")
                return best
            } else if (s is GeneralGaussJordanElimination.Result.UniqueSolution) {
                val solution = filterSolution(s.solution)
                println("Solution: $solution")
                return solution!!
            } else {
                error("????")
            }
        }

        return 0
    }

    private fun filterSolution(solution: DoubleArray?): Int? {
        return solution?.takeIf { s ->
            s.all { it == floor(it) && it >= 0.0 }
        }?.sum()?.toInt()
    }

    private fun countPresses1(machine: Machine): Long {
        var presses = 0L
        val visited = mutableSetOf<BitSet>()
        var states = setOf(BitSet(machine.goal.length()))
        while (states.isNotEmpty()) {
            val nextStates = mutableSetOf<BitSet>()
            for (state in states) {
                if (state == machine.goal) {
                    return presses
                }
                if (state in visited) continue
                visited.add(state)
                for (button in machine.buttons) {
                    val copy = state.clone() as BitSet
                    copy.xor(button.bits)
                    nextStates.add(copy)
                }
            }
            states = nextStates
            presses++
        }
        error("No solution found")
    }

    private data class Joltage(
        val index: Int,
        val value: Int
    ) : Comparable<Joltage> {

        override fun compareTo(other: Joltage): Int {
            return value.compareTo(other.value)
        }
    }

    private fun processInput(input: List<String>): List<Machine> {
        val regex = Regex("\\[(.*)] (.*) \\{(.*)}")
        return input.map { line ->
            val (goal, buttons, joltages) = regex.captureFirstMatch(line)
            val goalBits = BitSet(goal.length)
            // Goal
            for ((i, symbol) in goal.withIndex()) {
                if (symbol == '#') {
                    goalBits.set(i)
                }
            }
            // Buttons
            val buttonBitsList = mutableListOf<Button>()
            for (button in buttons.split(" ")) {
                val buttonNumbers = button.drop(1).dropLast(1).split(",").map { it.toInt() }
                val buttonBits = BitSet(buttonNumbers.size)
                for (i in buttonNumbers) {
                    buttonBits.set(i)
                }
                buttonBitsList.add(Button(buttonBits, buttonNumbers.toSet()))
            }
            // Joltages
            val joltageList = joltages.split(",").map { it.toInt() }
            Machine(goalBits, buttonBitsList,  joltageList)
        }
    }
}

