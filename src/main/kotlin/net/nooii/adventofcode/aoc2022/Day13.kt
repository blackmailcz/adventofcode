package net.nooii.adventofcode.aoc2022

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import kotlin.math.max

object Day13 {

    private sealed class Packet : Comparable<Packet> {
        class Simple(val value: Int) : Packet() {
            override fun toString() = "$value"
        }

        class Complex(val packets: List<Packet>) : Packet() {
            override fun toString() = "[${packets.joinToString(",")}]"
        }

        fun asComplex() = if (this is Complex) this else Complex(listOf(this))

        override fun compareTo(other: Packet): Int {
            if (this is Simple && other is Simple) {
                when {
                    this.value < other.value -> return -1
                    this.value > other.value -> return 1
                }
            } else {
                val leftComplex = this.asComplex()
                val rightComplex = other.asComplex()
                val maxSize = max(leftComplex.packets.size, rightComplex.packets.size)
                for (i in 0 until maxSize) {
                    val nextLeft = leftComplex.packets.getOrNull(i) ?: return -1
                    val nextRight = rightComplex.packets.getOrNull(i) ?: return 1
                    nextLeft.compareTo(nextRight).takeIf { it != 0 }?.let { return it }
                }
            }
            return 0
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2022).loadStrings("Day13Input")
        val packets = parseInput(input)
        part1(packets)
        part2(packets)
    }

    private fun part1(packets: List<Packet>) {
        val sum = packets.windowed(2, 2).withIndex().sumOf { (index, pair) ->
            val (left, right) = pair
            if (left < right) index + 1 else 0 // Pairs are "indexed" from 1
        }
        println(sum)
    }

    private fun part2(packets: List<Packet>) {
        val firstDivider = parsePacket("[[2]]")
        val secondDivider = parsePacket("[[6]]")
        val sortedPackets = (packets + listOf(firstDivider, secondDivider)).sorted()
        val decoderKey = (sortedPackets.indexOf(firstDivider) + 1) * (sortedPackets.indexOf(secondDivider) + 1)
        println(decoderKey)
    }

    private fun parseInput(input: List<String>): List<Packet> {
        return input.filter { it.isNotEmpty() }.map { parsePacket(it) }
    }

    private fun parsePacket(packetString: String): Packet {
        if (packetString.startsWith("[")) {
            var level = 0
            val borders = sortedSetOf(0, packetString.length - 1)
            for ((i, char) in packetString.withIndex()) {
                when {
                    char == '[' -> level++
                    char == ']' -> level--
                    char == ',' && level == 1 -> borders.add(i)
                }
            }
            val packets = borders.windowed(2, 1).mapNotNull { (from, to) ->
                // Each substring begins with either '[' or ',' - we need to skip it
                if (from + 1 != to) {
                    parsePacket(packetString.substring(from + 1, to))
                } else {
                    // No substring falls back to empty list.
                    null
                }
            }
            return Packet.Complex(packets)
        } else {
            return Packet.Simple(packetString.toInt())
        }
    }
}