package net.nooii.adventofcode2021

import net.nooii.adventofcode2021.Day8.Segment.*
import net.nooii.adventofcode2021.helpers.InputLoader
import net.nooii.adventofcode2021.helpers.NonNullHashMap
import java.lang.Exception

/**
 * Created by Nooii on 08.12.2021
 */
class Day8 {

    private class Pattern(
        val hints : List<String>,
        val output : List<String>,
    )

    private enum class Segment {
        TOP_LEFT, TOP, TOP_RIGHT, MIDDLE, BOTTOM_LEFT, BOTTOM, BOTTOM_RIGHT
    }

    companion object {

        @JvmStatic
        fun main(args : Array<String>) {
            val input = InputLoader().loadStrings("Day8Input")
            val patterns = processInput(input)
            part1(patterns)
            part2(patterns)
        }

        private fun part1(patterns : List<Pattern>) {
            val uniqueSizes = listOf(2, 3, 4, 7)
            println(patterns.sumOf { pattern -> pattern.output.count { it.length in uniqueSizes } })
        }

        private fun part2(patterns : List<Pattern>) {
            println(patterns.sumOf { pattern ->
                val map = decodeSegments(pattern)
                pattern.output.joinToString("") { digit -> decodeDigit(digit, map) }.toInt()
            })
        }

        private fun decodeSegments(pattern : Pattern) : NonNullHashMap<Segment, Char> {
            val map = NonNullHashMap<Segment, Char>()
            val one = pattern.hints.find { it.length == 2 }!!
            val seven = pattern.hints.find { it.length == 3 }!!
            val four = pattern.hints.find { it.length == 4 }!!
            val eight = pattern.hints.find { it.length == 7 }!!
            // Start by finding top segment
            map[TOP] = getMissingSegment(seven, *one.toCharArray())
            // Then find 6 in {0, 6, 9} because it contains only one part of 1. => finds bottom right
            map[BOTTOM_RIGHT] = getBottomRight(pattern.hints.filter { it.length == 6 }, one)
            // Then find top right by getting the other part of 1
            map[TOP_RIGHT] = getMissingSegment(one, map[BOTTOM_RIGHT])
            // Find 9 in {0, 6, 9} by combining 4 and top part => finds bottom
            map[BOTTOM] = getMissingSegmentFromList(
                pattern.hints.filter { it.length == 6 },
                *four.toCharArray().plus(map[TOP])
            )
            // Find middle by creating 3 from known parts
            map[MIDDLE] = getMissingSegmentFromList(
                pattern.hints.filter { it.length == 5 },
                map[TOP],
                map[BOTTOM],
                map[TOP_RIGHT],
                map[BOTTOM_RIGHT]
            )
            // Use 4 by combining 1 and middle part => finds top left
            map[TOP_LEFT] = getMissingSegment(four, *one.toCharArray().plus(map[MIDDLE]))
            // Get the last missing part from 8
            map[BOTTOM_LEFT] = getMissingSegment(eight, *map.values.toCharArray())
            return map
        }

        private fun decodeDigit(digit : String, map : NonNullHashMap<Segment, Char>) : String {
            return when {
                isMadeOf(digit, map, TOP_LEFT, TOP, TOP_RIGHT, BOTTOM_LEFT, BOTTOM, BOTTOM_RIGHT) -> "0"
                digit.length == 2 -> "1"
                isMadeOf(digit, map, TOP, TOP_RIGHT, MIDDLE, BOTTOM_LEFT, BOTTOM) -> "2"
                isMadeOf(digit, map, TOP, TOP_RIGHT, MIDDLE, BOTTOM_RIGHT, BOTTOM) -> "3"
                digit.length == 4 -> "4"
                isMadeOf(digit, map, TOP, TOP_LEFT, MIDDLE, BOTTOM_RIGHT, BOTTOM) -> "5"
                isMadeOf(digit, map, TOP, TOP_LEFT, MIDDLE, BOTTOM, BOTTOM_LEFT, BOTTOM_RIGHT) -> "6"
                digit.length == 3 -> "7"
                digit.length == 7 -> "8"
                isMadeOf(digit, map, TOP, TOP_LEFT, TOP_RIGHT, MIDDLE, BOTTOM, BOTTOM_RIGHT) -> "9"
                else -> throw Exception("Unknown digit - $digit | $map")
            }
        }

        private fun isMadeOf(encodedDigit : String, map : NonNullHashMap<Segment, Char>, vararg segments : Segment) : Boolean {
            if (encodedDigit.length != segments.size) {
                return false
            }
            return !segments.any { segment -> !encodedDigit.contains(map[segment]) }
        }

        private fun getMissingSegmentFromList(patterns : List<String>, vararg segments : Char) : Char {
            val number = patterns.find { it.toList().containsAll(segments.toList()) }!!
            return getMissingSegment(number, *segments)
        }

        private fun getMissingSegment(number : String, vararg segments : Char) : Char {
            return number.toList().minus(segments.toSet()).first()
        }

        private fun getBottomRight(patternsOfSizeSix : List<String>, one : String) : Char {
            val six = patternsOfSizeSix.find { !(it.contains(one[0]) && it.contains(one[1])) }!!
            return if (six.contains(one[0])) one[0] else one[1]
        }

        private fun processInput(input : List<String>) : List<Pattern> {
            return input.map { line ->
                val parts = line.split(" | ").map { it.split(" ") }
                Pattern(parts[0], parts[1])
            }
        }

    }

}