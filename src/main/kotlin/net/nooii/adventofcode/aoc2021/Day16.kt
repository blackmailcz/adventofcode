package net.nooii.adventofcode.aoc2021

import net.nooii.adventofcode.aoc2021.Day16.SubPacketMethod.*
import net.nooii.adventofcode.helpers.*

/**
 * Created by Nooii on 16.12.2021
 */
object Day16 {

    private class PacketHeader(
        val version: Int,
        val type: Int
    ) {
        override fun toString() = "($version|$type)"
    }

    private sealed class Packet(val header: PacketHeader) {
        class Literal(header: PacketHeader, val value: Long) : Packet(header) {
            override fun toString() = "$header {$value}"
        }

        class Container(header: PacketHeader, val packets: List<Packet>) : Packet(header) {
            override fun toString() = "$header <$packets>"
        }
    }

    private enum class SubPacketMethod { BY_LENGTH, BY_COUNT }

    @JvmStatic
    fun main(args: Array<String>) {
        val input = hexToBin(InputLoader(AoCYear.AOC_2021).loadStrings("Day16Input").first())
        val (_, packet) = parsePacket(0, input)
        packet!!
        println(countVersionSum(packet))
        println(compute(packet))
    }

    private fun parsePacket(initialI: Int, input: String): Pair<Int, Packet?> {
        val none = Pair(input.length, null)
        var i = initialI
        val (nextI, header) = parseHeader(i, input) ?: return none
        i = nextI
        if (header.type == 4) {
            return parseLiteral(i, input, header) ?: none
        } else {
            val method = parseSubPacketMethod(i, input) ?: return none
            i++
            return when (method) {
                BY_LENGTH -> parseSubPacketsByLength(i, input, header) ?: none
                BY_COUNT -> parseSubPacketsByCount(i, input, header) ?: none
            }
        }
    }

    private fun parseHeader(i: Int, input: String): Pair<Int, PacketHeader>? {
        if (i + 6 > input.length) {
            return null
        }
        val version = binToDecInt(input.substring(i, i + 3))
        val type = binToDecInt(input.substring(i + 3, i + 6))
        return Pair(i + 6, PacketHeader(version, type))
    }

    private fun parseLiteral(initialI: Int, input: String, header: PacketHeader): Pair<Int, Packet>? {
        val literal = StringBuilder()
        var i = initialI
        do {
            if (i + 5 > input.length) {
                return null
            }
            val isLastLiteralDigit = !bitToBool(input[i])
            i++
            literal.append(input.substring(i, i + 4))
            i += 4
        } while (!isLastLiteralDigit)
        return Pair(i, Packet.Literal(header, binToDecLong(literal.toString())))
    }

    private fun parseSubPacketMethod(i: Int, input: String): SubPacketMethod? {
        if (i >= input.length) {
            return null
        }
        return if (bitToBool(input[i])) BY_COUNT else BY_LENGTH
    }

    private fun parseSubPacketsByLength(initialI: Int, input: String, header: PacketHeader): Pair<Int, Packet>? {
        var i = initialI
        if (i + 15 > input.length) {
            return null
        }
        val subPacketLength = binToDecInt(input.substring(i, i + 15))
        i += 15
        val subPackets = mutableListOf<Packet>()
        val subPacketEnd = i + subPacketLength
        while (i < subPacketEnd) {
            val (nextI, subPacket) = parsePacket(i, input)
            i = nextI
            subPacket?.let { subPackets.add(it) }
        }
        return Pair(i, Packet.Container(header, subPackets))
    }

    private fun parseSubPacketsByCount(initialI: Int, input: String, header: PacketHeader): Pair<Int, Packet>? {
        var i = initialI
        if (i + 11 > input.length) {
            return null
        }
        val totalSubPackets = binToDecInt(input.substring(i, i + 11))
        i += 11
        var subPacketCount = 0
        val subPackets = mutableListOf<Packet>()
        while (subPacketCount < totalSubPackets) {
            val (nextI, subPacket) = parsePacket(i, input)
            i = nextI
            subPacket?.let { subPackets.add(it) }
            subPacketCount++
        }
        return Pair(i, Packet.Container(header, subPackets))
    }

    private fun countVersionSum(packet: Packet): Int {
        val nestedSum = when (packet) {
            is Packet.Literal -> 0
            is Packet.Container -> packet.packets.sumOf { countVersionSum(it) }
        }
        return packet.header.version + nestedSum
    }

    private fun compute(packet: Packet): Long {
        return when (packet) {
            is Packet.Literal -> packet.value
            is Packet.Container -> {
                val values = packet.packets.map { compute(it) }
                when (packet.header.type) {
                    0 -> values.sum()
                    1 -> values.reduce(Long::times)
                    2 -> values.min()
                    3 -> values.max()
                    5 -> (values[0] > values[1]).toLong()
                    6 -> (values[0] < values[1]).toLong()
                    7 -> (values[0] == values[1]).toLong()
                    else -> throw IllegalStateException("Unknown type")
                }
            }
        }
    }

    private fun bitToBool(bit: Char) = bit == '1'
}