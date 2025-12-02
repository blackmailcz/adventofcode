package net.nooii.adventofcode.aoc2024

import com.github.shiguruikai.combinatoricskt.combinations
import com.github.shiguruikai.combinatoricskt.permutations
import net.nooii.adventofcode.helpers.*

object Day24 {

    /**
     * Represents the logical operations that can be performed by gates in the circuit.
     */
    private enum class Operation {
        AND, OR, XOR
    }

    /**
     * Represents a logical gate in the circuit.
     *
     * @property operation The logical operation performed by this gate (AND, OR, or XOR).
     * @property input1 The identifier of the first input wire.
     * @property input2 The identifier of the second input wire.
     * @property output The identifier of the output wire.
     */
    private data class Gate(
        val operation: Operation,
        val input1: String,
        val input2: String,
        val output: String
    )

    /**
     * Represents the data used in the randomization process for finding unique gate combinations.
     *
     * @property knownWires All possible combinations of known wires.
     * @property unknownWires All possible combinations of unknown wires.
     */
    private data class RandomizationData(
        val knownWires: Set<Set<Pair<Gate, Gate>>>,
        val unknownWires: Set<Pair<Gate, Gate>>
    ) {

        /**
         * Checks if unique solution has been found or throws error if no solution is possible.
         */
        fun isUnique(): Boolean {
            if (knownWires.isEmpty() || unknownWires.isEmpty()) {
                error("No solution found")
            }
            return knownWires.size == 1 && unknownWires.size == 1
        }
    }

    /**
     * Represents the input data for the randomization process.
     *
     * @property xBin A binary string representation of the 'x' input to the circuit.
     * @property yBin A binary string representation of the 'y' input to the circuit.
     * @property wires A map of wire identifiers to their boolean states (true for high, false for low).
     */
    private data class RandomizationInput(
        val xBin: String,
        val yBin: String,
        val wires: Map<String, Boolean>
    )

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2024).loadStrings("Day24Input")
        val (wiresInput, gatesInput) = input.splitByEmptyLine()
        val wires = parseWires(wiresInput)
        val gates = parseGates(gatesInput)
        val pad = computePad(wires)
        part1(wires, gates, pad)
        // Runtime ~ 8-10 seconds
        part2(wires, gates, pad)
    }

    private fun part1(wires: Map<String, Boolean>, gates: Set<Gate>, pad: Int) {
        println(binToDecLong(computeOutput(wires, gates, pad)!!))
    }

    private fun part2(wires: Map<String, Boolean>, gates: Set<Gate>, pad: Int) {
        // The entire thing is a ripple carry adder
        // We start by eliminating the obvious mistakes:
        // Mistake #1 - Each gate with z** output must be formed by XOR operation
        val nonXorZ = findNonXorZGates(gates)
        // Mistake #2 - No gate without x** or y** input OR z** output can be formed by XOR operation
        val invalidXors = findInvalidXors(gates)
        // Assume these non-XORs and invalid XORs are mixed up together, because there can't be another XOR elsewhere

        // This should identify enough gates to run the circuit for random inputs until a single combination
        // of gates if valid for all of them

        // Initial data ("known pairs") - we don't know the correct wire pairs yet, so we will try it all combinations
        val knownWiresCombinations = mutableSetOf<Set<Pair<Gate, Gate>>>()
        for (nonXor in nonXorZ.permutations()) {
            for (invalidXor in invalidXors.permutations()) {
                knownWiresCombinations += nonXor.zip(invalidXor).toSet()
            }
        }
        // Initial candidates - can be any two of the remaining gates
        val unknownWiresCombinations = (gates - nonXorZ - invalidXors)
            .combinations(2)
            .map { Pair(it[0], it[1]) }
            .toSet()
        // Start randomizing the circuit, we will run it for our input first
        var input = RandomizationInput(
            xBin = buildBinaryNumber(wires, pad) { it.startsWith("x") },
            yBin = buildBinaryNumber(wires, pad) { it.startsWith("y") },
            wires = wires
        )
        var randomizationData = RandomizationData(knownWiresCombinations, unknownWiresCombinations)
        // Keep randomizing until a single combination of gates is found
        // In each iteration, the "known pairs" and "candidates" is reduced
        while (!randomizationData.isUnique()) {
            randomizationData = randomize(
                xBin = input.xBin,
                yBin = input.yBin,
                pad = pad,
                wires = input.wires,
                gates = gates,
                data = randomizationData
            )
            input = generateRandomInput(pad)
        }
        // Process the solution and print correct output
        val output = randomizationData.knownWires
            .flatMap { knownWires ->
                (knownWires + randomizationData.unknownWires).map { pair -> setOf(pair.first, pair.second) }
            }
            .flatten()
            .map { it.output }
            .sorted()
        println(output.joinToString(","))
    }

    /**
     * Generates a random input for the circuit simulation.
     *
     * This function creates a set of random boolean values for 'x' and 'y' wires,
     * and constructs binary string representations for these inputs.
     *
     * @param pad The padding length for the binary string representations.
     * @return A [RandomizationInput] object containing:
     *         - xBin: A binary string representation of the 'x' input.
     *         - yBin: A binary string representation of the 'y' input.
     *         - wires: A map of wire identifiers to their randomly generated boolean states.
     */
    private fun generateRandomInput(pad: Int): RandomizationInput {
        val booleans = setOf(true, false)
        val wires = buildMap {
            for (i in 0..44) {
                put("x${i.toString().padStart(2, '0')}", booleans.random())
                put("y${i.toString().padStart(2, '0')}", booleans.random())
            }
        }
        val xBin = buildBinaryNumber(wires, pad) { it.startsWith("x") }
        val yBin = buildBinaryNumber(wires, pad) { it.startsWith("y") }
        return RandomizationInput(
            xBin = xBin,
            yBin = yBin,
            wires = wires
        )
    }

    /**
     * Performs a randomization step in the process of finding the correct gate configuration.
     *
     * This function attempts to identify valid gate combinations by swapping outputs of gate pairs
     * and checking if the resulting circuit produces the correct binary sum.
     *
     * @param xBin The binary string representation of the 'x' input to the circuit.
     * @param yBin The binary string representation of the 'y' input to the circuit.
     * @param pad The padding length for binary representations.
     * @param wires A map of wire identifiers to their boolean states (true for high, false for low).
     * @param gates The set of all gates in the circuit.
     * @param data The current state of known wire combinations and unknown wire pairs.
     * @return A new [RandomizationData] object containing updated sets of known wire combinations
     *         and candidate pairs that produce correct binary sums.
     */
    private fun randomize(
        xBin: String,
        yBin: String,
        pad: Int,
        wires: Map<String, Boolean>,
        gates: Set<Gate>,
        data: RandomizationData
    ): RandomizationData {
        val nextKnownWiresCombinations = mutableSetOf<Set<Pair<Gate, Gate>>>()
        val nextLastPairCandidates = mutableSetOf<Pair<Gate, Gate>>()
        for (knownPairs in data.knownWires) {
            for (candidatePair in (data.unknownWires)) {
                // Create pair
                val copyGates = gates.toMutableSet()
                val gatePairs = knownPairs + candidatePair
                for ((gate1, gate2) in gatePairs) {
                    val newGate1 = gate1.copy(output = gate2.output)
                    val newGate2 = gate2.copy(output = gate1.output)
                    copyGates.add(newGate1)
                    copyGates.add(newGate2)
                    copyGates.remove(gate1)
                    copyGates.remove(gate2)
                }
                val zBin = computeOutput(wires, copyGates, pad) ?: continue
                if (isCorrectBinarySum(xBin, yBin, zBin)) {
                    nextKnownWiresCombinations.add(knownPairs)
                    nextLastPairCandidates.add(candidatePair)
                }
            }
        }
        return RandomizationData(nextKnownWiresCombinations, nextLastPairCandidates)
    }

    /**
     * Verifies if the binary sum of two numbers is correct.
     *
     * This function performs a bit-by-bit addition of two binary numbers represented as strings
     * and compares the result with an expected sum. It accounts for carry operations in the addition process.
     *
     * @param x The first binary number as a string.
     * @param y The second binary number as a string.
     * @param expectedSum The expected sum of x and y as a binary string.
     * @return Boolean indicating whether the sum is correct (true) or not (false).
     */
    private fun isCorrectBinarySum(x: String, y: String, expectedSum: String): Boolean {
        // Ensure all strings have the same length by padding them with leading zeros
        val maxLength = maxOf(x.length, y.length, expectedSum.length)
        val paddedX = x.padStart(maxLength, '0')
        val paddedY = y.padStart(maxLength, '0')
        val paddedExpectedSum = expectedSum.padStart(maxLength, '0')

        var actualCarry = 0 // This represents the carry as an integer (0 or 1)
        var expectedCarry = 0 // This represents the expected carry as an integer (0 or 1)

        // Iterate through each bit from the least significant (rightmost) to the most significant (leftmost)
        for (i in maxLength - 1 downTo 0) {
            val bitX = paddedX[i].toString().toInt() // Convert the character '0' or '1' to integer
            val bitY = paddedY[i].toString().toInt()
            val expectedBit = paddedExpectedSum[i].toString().toInt()

            // Compute the actual sum (bitwise addition including carry)
            val actualSum = bitX xor bitY xor actualCarry

            // Flag mismatches in the sum bit
            if (actualSum != expectedBit) {
                return false
            }

            // Compute the carry for the next iteration
            val nextActualCarry = (bitX and bitY) or (actualCarry and (bitX xor bitY))
            val nextExpectedCarry = (bitX and bitY) or (expectedCarry and (bitX xor bitY))

            // Flag mismatches in the carry if it affects the current bit
            if (actualCarry != expectedCarry) {
                return false
            }

            // Update carries for the next iteration
            actualCarry = nextActualCarry
            expectedCarry = nextExpectedCarry
        }
        return true
    }

    /**
     * Finds all gates in the circuit that output to a 'z' wire (except 'z45') and are not XOR gates.
     *
     * @param gates The set of all gates in the circuit to be examined.
     * @return A set of [Gate] objects that meet the criteria: output starts with 'z' (excluding 'z45'),
     *         and the operation is not XOR.
     */
    private fun findNonXorZGates(gates: Set<Gate>): Set<Gate> {
        return gates
            .filter { it.output.startsWith("z") && it.output != "z45" && it.operation != Operation.XOR }
            .toSet()
    }

    /**
     * Finds gates in the circuit that are incorrectly set as XOR operations.
     *
     * This function identifies gates that meet the following criteria:
     * 1. The gate's operation is XOR
     * 2. The gate's output does not start with 'z'
     * 3. At least one of the gate's inputs does not start with 'x' or 'y'
     *
     * @param gates The set of all gates in the circuit to be examined.
     * @return A set of [Gate] objects that are considered invalid XOR gates based on the above criteria.
     */
    private fun findInvalidXors(gates: Set<Gate>): Set<Gate> {
        return gates
            .filter { it.operation == Operation.XOR && !it.output.startsWith("z") && (it.input1.first() !in "xy" || it.input2.first() !in "xy") }
            .toSet()
    }

    /**
     * Computes the padding length for binary representations based on the wire identifiers.
     */
    private fun computePad(wires: Map<String, Boolean>): Int {
        return wires.keys.max().toString().filter { it.isDigit() }.toInt() + 2
    }

    /**
     * Computes the output of a logical circuit based on the given initial wire states and gates.
     *
     * This function simulates the operation of a logical circuit by iteratively applying the gates
     * to the wire states until no further changes can be made or all gates have been processed.
     *
     * @param initialWires A map of initial wire identifiers to their boolean states (true for high, false for low).
     * @param gates A set of [Gate] objects representing the logical gates in the circuit.
     * @param pad The padding length for the binary string representation of the output.
     * @param filter A function that determines which wires to include in the final output.
     *               By default, it includes wires whose identifiers start with 'z'.
     * @return A string representation of the circuit's output in binary format, or null if the circuit cannot be fully evaluated.
     */
    private fun computeOutput(
        initialWires: Map<String, Boolean>,
        gates: Set<Gate>,
        pad: Int,
        filter: (String) -> Boolean = { it.startsWith("z") }
    ): String? {
        val wires = initialWires.toMutableMap().nn()
        var current = gates
        while (current.isNotEmpty()) {
            val next = current.toMutableSet()
            for (gate in current) {
                if (gate.input1 in wires && gate.input2 in wires) {
                    wires[gate.output] = when (gate.operation) {
                        Operation.AND -> wires[gate.input1] && wires[gate.input2]
                        Operation.OR -> wires[gate.input1] || wires[gate.input2]
                        Operation.XOR -> wires[gate.input1] xor wires[gate.input2]
                    }
                    next.remove(gate)
                }
            }
            if (current.size == next.size) {
                return null
            }
            current = next
        }
        return buildBinaryNumber(wires, pad) { filter(it) }
    }

    /**
     * Builds a binary number string from a map of wire states.
     *
     * @param wires A map of wire identifiers to their boolean states (true for high, false for low).
     * @param pad The length to which the resulting binary string should be padded with leading zeros.
     * @param filter A function that determines which wires should be included in the binary number.
     * @return A string representation of the binary number, padded to the specified length.
     */
    private fun buildBinaryNumber(wires: Map<String, Boolean>, pad: Int, filter: (String) -> Boolean): String {
        return wires
            .filterKeys { filter(it) }
            .toSortedMap()
            .values
            .reversed()
            .joinToString("") { it.toInt().toString() }
            .padStart(pad, '0')
    }

    private fun parseWires(input: List<String>): Map<String, Boolean> {
        val regex = Regex("(\\w+): (\\d)")
        return input.associate { line ->
            val (wire, value) = regex.captureFirstMatch(line)
            wire to (value.toInt() == 1)
        }
    }

    private fun parseGates(input: List<String>): Set<Gate> {
        val regex = Regex("(\\w+) (AND|OR|XOR) (\\w+) -> (\\w+)")
        return input.map { line ->
            val (i1, type, i2, o) = regex.captureFirstMatch(line)
            Gate(Operation.valueOf(type), i1, i2, o)
        }.toSet()
    }
}