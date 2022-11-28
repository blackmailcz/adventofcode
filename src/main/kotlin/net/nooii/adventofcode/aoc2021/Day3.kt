package net.nooii.adventofcode.aoc2021

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader

/**
 * Created by Nooii on 03.12.2021
 */
class Day3 {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2021).loadStrings("Day3Input")
            val size = computeSize(input)
            val editedInput = convertToBooleans(input)
            part1(size, editedInput)
            part2(size, editedInput)
        }

        private fun part1(size: Int, input: List<List<Boolean>>) {
            val bits = mutableListOf<Boolean>()
            for (bitIndex in 0 until size) {
                bits += computeSignificantBit(input, bitIndex)
            }
            val gamma = binArray2dec(bits)
            val epsilon = binArray2dec(bits) { !it }
            println(gamma * epsilon)
        }

        private fun part2(size: Int, input: List<List<Boolean>>) {
            val oxygen = computePart2(size, input, true)
            val scrubber = computePart2(size, input, false)
            println(oxygen * scrubber)
        }

        private fun computePart2(size: Int, input: List<List<Boolean>>, sig: Boolean): Int {
            var currentInput = input
            val bits = mutableListOf<Boolean>()
            for (bitIndex in 0 until size) {
                if (currentInput.size > 1) {
                    currentInput = filter(currentInput, bitIndex, sig)
                }
                bits += computeSignificantBit(currentInput, bitIndex)
            }
            return binArray2dec(bits)
        }

        private fun computeSize(input: List<String>): Int {
            return input.first().length
        }

        private fun convertToBooleans(input: List<String>): List<List<Boolean>> {
            return input.map { line ->
                line.mapNotNull {
                    when (it) {
                        '0' -> false
                        '1' -> true
                        else -> null
                    }
                }
            }
        }

        private fun computeSignificantBit(input: List<List<Boolean>>, index: Int): Boolean {
            var (zeroes, ones) = Pair(0, 0)
            input.forEach { line ->
                if (line[index]) ones++ else zeroes++
            }
            return ones > zeroes
        }

        private fun filter(input: List<List<Boolean>>, bitIndex: Int, sig: Boolean): List<List<Boolean>> {
            val zeroes = input.count { !it[bitIndex] }
            val ones = input.count { it[bitIndex] }
            val filterBy = when {
                zeroes == ones -> sig
                ones > zeroes -> sig
                else -> !sig
            }
            return input.filter { line ->
                line.getOrNull(bitIndex)?.let { it == filterBy } ?: false
            }
        }

        private fun binArray2dec(bits: List<Boolean>, mod: (bit: Boolean) -> Boolean = { it }): Int {
            return bits.joinToString(separator = "", transform = { mod(it).toInt().toString() }).toInt(2)
        }

        private fun Boolean.toInt() = this.compareTo(false)

    }
}