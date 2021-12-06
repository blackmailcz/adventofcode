package net.nooii.adventofcode2021

import net.nooii.adventofcode2021.helpers.InputLoader

/**
 * Created by Nooii on 06.12.2021
 */
class Day6 {

    companion object {

        @JvmStatic
        fun main(args : Array<String>) {
            val input = InputLoader().loadStrings("Day6Input")
            part1(processInput(input))
            part2(processInput(input))
        }

        private fun part1(fish : MutableMap<Int, Long>) {
            processDays(fish, 80)
            println(countFish(fish))
        }

        private fun part2(fish : MutableMap<Int, Long>) {
            processDays(fish, 256)
            println(countFish(fish))
        }

        private fun processInput(rawInput : List<String>) : MutableMap<Int, Long> {
            val input = rawInput.first().split(",").map { it.toInt() }.toMutableList()
            val dayMap = mutableMapOf<Int, Long>()
            for (day in input) {
                dayMap.addFish(day, 1)
            }
            return dayMap
        }

        private fun processDay(dayMap : MutableMap<Int, Long>) {
            val newDayMap = mutableMapOf<Int, Long>()
            for ((daysLeft, fish) in dayMap) {
                if (daysLeft == 0) {
                    newDayMap.addFish(6, fish)
                    newDayMap.addFish(8, fish)
                } else {
                    newDayMap.addFish(daysLeft - 1, fish)
                }
            }
            dayMap.clear()
            dayMap.putAll(newDayMap)
        }

        private fun processDays(dayMap : MutableMap<Int, Long>, days : Long) {
            for (i in 0 until days) {
                processDay(dayMap)
            }
        }

        private fun countFish(fish : MutableMap<Int, Long>) = fish.values.sum()

        private fun MutableMap<Int,Long>.addFish(day : Int, fishCount : Long) {
            this[day] = (this[day] ?: 0) + fishCount
        }

    }

}