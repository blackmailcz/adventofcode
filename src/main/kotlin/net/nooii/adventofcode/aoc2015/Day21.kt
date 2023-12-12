package net.nooii.adventofcode.aoc2015

import com.github.shiguruikai.combinatoricskt.combinations
import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.captureFirstMatch
import net.nooii.adventofcode.helpers.splitByEmptyLine
import kotlin.math.max
import kotlin.math.min

class Day21 {

    private sealed interface Player {
        var hp: Int
        val damage: Int
        val armor: Int
    }

    private data class Human(override var hp: Int) : Player {
        var equipment: List<Equipment> = emptyList()
        override val damage: Int
            get() = equipment.sumOf { it.damage }
        override val armor: Int
            get() = equipment.sumOf { it.armor }
    }

    private data class Boss(
        override var hp: Int,
        override val damage: Int,
        override val armor: Int
    ) : Player

    private class Shop(
        val weapons: List<Equipment>,
        val tunics: List<Equipment>,
        val rings: List<Equipment>
    )

    private class Equipment(
        val name: String,
        val cost: Int,
        val damage: Int,
        val armor: Int
    )

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2015).loadStrings("Day21Input")
            val boss = processInput(input)
            val shopInput = InputLoader(AoCYear.AOC_2015).loadStrings("Day21Shop")
            val shop = processShop(shopInput)
            part1(shop, boss)
            part2(shop, boss)
        }

        private fun part1(shop: Shop, boss: Boss) {
            var cheapest = Int.MAX_VALUE
            for (equipment in createEquipmentStates(shop)) {
                val winner = play(Human(100).also { it.equipment = equipment }, boss.copy())
                if (winner is Human) {
                    cheapest = min(cheapest, equipment.sumOf { it.cost })
                }
            }
            println(cheapest)
        }

        private fun part2(shop: Shop, boss: Boss) {
            var mostExpensive = 0
            for (equipment in createEquipmentStates(shop)) {
                val winner = play(Human(100).also { it.equipment = equipment }, boss.copy())
                if (winner is Boss) {
                    mostExpensive = max(mostExpensive, equipment.sumOf { it.cost })
                }
            }
            println(mostExpensive)
        }

        private fun play(human: Human, boss: Boss): Player {
            var player: Player = human
            var opponent: Player = boss
            while (true) {
                opponent.hp -= max(1, player.damage - opponent.armor)
                if (opponent.hp <= 0) {
                    return player
                }
                player = swap(player, human, boss)
                opponent = swap(opponent, human, boss)
            }
        }

        private fun swap(player: Player, human: Human, boss: Boss): Player {
            return when (player) {
                is Human -> boss
                is Boss -> human
            }
        }

        private fun createEquipmentStates(shop: Shop): List<List<Equipment>> {
            val none = Equipment("None", 0, 0, 0)
            val equipmentStates = mutableListOf<List<Equipment>>()
            for (weapon in shop.weapons) {
                for (tunic in shop.tunics + none) {
                    for (rings in (shop.rings + none + none).combinations(2)) {
                        equipmentStates.add(listOf(weapon, tunic, *rings.toTypedArray()))
                    }
                }
            }
            return equipmentStates
        }

        private fun parseEquipment(line: String): Equipment {
            val (name, cost, damage, armor) = Regex("(.*?)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)").captureFirstMatch(line)
            return Equipment(name, cost.toInt(), damage.toInt(), armor.toInt())
        }

        private fun parseEquipmentList(lines: List<String>): List<Equipment> {
            return lines.drop(1).map { parseEquipment(it) }
        }

        private fun processShop(input: List<String>): Shop {
            val (weapons, tunics, rings) = input.splitByEmptyLine()
            return Shop(
                weapons = parseEquipmentList(weapons),
                tunics = parseEquipmentList(tunics),
                rings = parseEquipmentList(rings)
            )
        }

        private fun processInput(input: List<String>): Boss {
            val (hp, damage, armor) = input.map { line ->
                Regex("(\\d+)").captureFirstMatch(line) { it.toInt() }.first()
            }
            return Boss(hp, damage, armor)
        }
    }
}