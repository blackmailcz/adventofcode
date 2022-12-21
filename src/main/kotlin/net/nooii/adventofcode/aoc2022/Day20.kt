package net.nooii.adventofcode.aoc2022

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader

class Day20 {

    private class Item(val v: Long) // Wrap item to class to handle duplicate numbers

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2022).loadInts("Day20Input")
            val list = input.map { Item(it.toLong()) }
            part1(list)
            part2(list)
        }

        private fun part1(list: List<Item>) {
            solution(list, 1)
        }

        private fun part2(list: List<Item>) {
            solution(list.map { Item(it.v * 811589153L) }, 10)
        }

        private fun solution(inList: List<Item>, cycles: Int) {
            val outList = inList.toMutableList()
            repeat(cycles) {
                for (item in inList) {
                    shift(item, outList)
                }
            }
            val zeroIndex = outList.indexOfFirst { it.v == 0L }
            val sum = listOf(1000, 2000, 3000).sumOf {
                outList[(zeroIndex + it) % outList.size].v
            }
            println(sum)
        }

        private fun shift(item: Item, list: MutableList<Item>) {
            if (item.v == 0L) {
                return
            }
            val index = list.indexOf(item)
            list.removeAt(index)
            val by = if (item.v > 0) {
                item.v
            } else {
                // Convert negative shifting to positive shifting by wrapping
                (item.v % list.size) + list.size
            }
            val targetIndex = ((index + by) % list.size).toInt()
            list.add(targetIndex, item)
        }
    }
}