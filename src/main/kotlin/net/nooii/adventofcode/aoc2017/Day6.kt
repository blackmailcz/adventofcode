package net.nooii.adventofcode.aoc2017

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader

object Day6 {

    private data class Slot(
        val index: Int,
        val value: Int
    ) : Comparable<Slot> {
        override fun compareTo(other: Slot): Int {
            return other.value.compareTo(value).takeIf { it != 0 } ?: index.compareTo(other.index)
        }
    }

    private data class Result(
        val max: Slot,
        val slots: List<Slot>,
    )

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2017).loadStrings("Day6Input")
        val slots = processInput(input)
        solution(slots)
    }

    private fun solution(slots: List<Slot>) {
        // Part 1
        var result = Result(slots.maxBy { it.value }, slots)
        val visited = mutableSetOf<List<Slot>>()
        var counter = 0
        while (!visited.contains(result.slots)) {
            visited.add(result.slots)
            result = redistribute(result, slots.size)
            counter++
        }
        println(counter)
        // Part 2 (fluently continues with current state)
        counter = 0
        val target = result
        do {
            result = redistribute(result, slots.size)
            counter++
        } while (target != result)
        println(counter)
    }

    private fun redistribute(input: Result, slotCount: Int): Result {
        val target = input.max
        val fullValue = target.value / slotCount
        val remainder = target.value % slotCount
        val ranges = when {
            remainder <= 0 -> {
                emptySet()
            }
            target.index + 1 >= slotCount -> {
                setOf(IntRange(0, remainder - 1))
            }
            target.index + remainder < slotCount -> {
                setOf(IntRange(target.index + 1, target.index + remainder))
            }
            else -> {
                setOf(IntRange(0, (target.index + remainder) % slotCount), IntRange(target.index + 1, slotCount - 1))
            }
        }
        var nextPick = Slot(Int.MAX_VALUE, -1)
        val nextSlots = mutableListOf<Slot>()
        for ((i, slot) in input.slots.withIndex()) {
            val baseValue = if (slot == target) 0 else slot.value
            val value = baseValue + if (ranges.any { it.contains(i) }) fullValue + 1 else fullValue
            val nextSlot = Slot(slot.index, value)
            if (nextSlot.value > nextPick.value || (nextSlot.value == nextPick.value && nextSlot.index < nextPick.index)) {
                nextPick = nextSlot
            }
            nextSlots.add(nextSlot)
        }
        return Result(nextPick, nextSlots)
    }

    private fun processInput(input: List<String>): List<Slot> {
        return input.first().split(Regex("\\s+")).mapIndexed { index, s ->
            Slot(index, s.toInt())
        }
    }
}