package net.nooii.adventofcode.aoc2022

import net.nooii.adventofcode.aoc2022.Day19.Material.*
import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.product

class Day19 {

    private enum class Material {
        ORE, CLAY, OBSIDIAN, GEODE
    }

    private class Robot(
        val costs: Map<Material, Int>
    ) {

        fun getCost(material: Material) = costs[material] ?: 0
    }

    private class Blueprint(
        val id: Int,
        val oreCollector: Robot,
        val clayCollector: Robot,
        val obsidianCollector: Robot,
        val geodeCollector: Robot
    )

    private data class State(
        val ore: Int,
        val clay: Int,
        val obsidian: Int,
        val geode: Int,
        val oreCollectors: Int,
        val clayCollectors: Int,
        val obsidianCollectors: Int,
        val geodeCollectors: Int,
    ) {

        fun canBuy(robot: Robot): Boolean {
            return robot.costs.all { (material, cost) ->
                getMatCount(material) / cost > 0
            }
        }

        private fun getMatCount(material: Material): Int {
            return when (material) {
                ORE -> ore
                CLAY -> clay
                OBSIDIAN -> obsidian
                GEODE -> geode
            }
        }

        fun nextState(blueprint: Blueprint, robot: Robot?): State {
            return State(
                ore = ore + oreCollectors - (robot?.getCost(ORE) ?: 0),
                clay = clay + clayCollectors - (robot?.getCost(CLAY) ?: 0),
                obsidian = obsidian + obsidianCollectors - (robot?.getCost(OBSIDIAN) ?: 0),
                geode = geode + geodeCollectors - (robot?.getCost(GEODE) ?: 0),
                oreCollectors = oreCollectors + if (robot == blueprint.oreCollector) 1 else 0,
                clayCollectors = clayCollectors + if (robot == blueprint.clayCollector) 1 else 0,
                obsidianCollectors = obsidianCollectors + if (robot == blueprint.obsidianCollector) 1 else 0,
                geodeCollectors = geodeCollectors + if (robot == blueprint.geodeCollector) 1 else 0
            )
        }
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2022).loadStrings("Day19Input")
            val blueprints = parseInput(input)
            part1(blueprints)
            part2(blueprints)
        }

        private fun part1(blueprints: List<Blueprint>) {
            // Runtime ~ 14 sec
            val sum = blueprints.sumOf { it.id * getMaxGeodes(it, 24) }
            println(sum)
        }

        private fun part2(blueprints: List<Blueprint>) {
            // Runtime ~ 3.5 sec
            val product = blueprints.take(3).map { getMaxGeodes(it, 32) }.product()
            println(product)
        }

        private fun getMaxGeodes(blueprint: Blueprint, totalTime: Int): Int {
            var states = mutableSetOf(
                State(
                    ore = 0,
                    clay = 0,
                    obsidian = 0,
                    geode = 0,
                    oreCollectors = 1,
                    clayCollectors = 0,
                    obsidianCollectors = 0,
                    geodeCollectors = 0
                )
            )
            var time = 0
            while (time < totalTime) {
                val nextStates = mutableSetOf<State>()
                for (state in states) {
                    // If possible to buy geode bot
                    if (state.canBuy(blueprint.geodeCollector)) {
                        nextStates.add(state.nextState(blueprint, blueprint.geodeCollector))
                        continue // Other options are always worse
                    }
                    // If possible to buy obsidian bot
                    if (state.canBuy(blueprint.obsidianCollector)) {
                        nextStates.add(state.nextState(blueprint, blueprint.obsidianCollector))
                        continue // Other options are always worse
                    }
                    // If possible to buy clay bot
                    if (state.canBuy(blueprint.clayCollector)) {
                        nextStates.add(state.nextState(blueprint, blueprint.clayCollector))
                    }
                    // If possible to buy ore bot
                    if (state.canBuy(blueprint.oreCollector)) {
                        nextStates.add(state.nextState(blueprint, blueprint.oreCollector))
                    }
                    // Or don't buy anything
                    nextStates.add(state.nextState(blueprint, null))
                }
                states = if (nextStates.any { it.geode >= 1 }) {
                    // Filter out states with fewer geodes than the maximum
                    val max = nextStates.maxOf { it.geode }
                    nextStates.filter { it.geode == max }.toMutableSet()
                } else {
                    nextStates
                }
                time++
            }
            return states.maxOfOrNull { it.geode } ?: 0
        }

        private fun parseInput(input: List<String>): List<Blueprint> {
            return input.map { line ->
                val data = Regex("(\\d+)").findAll(line).map { it.groupValues[1].toInt() }.toList()
                Blueprint(
                    id = data[0],
                    oreCollector = Robot(
                        mutableMapOf(
                            ORE to data[1]
                        )
                    ),
                    clayCollector = Robot(
                        mutableMapOf(
                            ORE to data[2]
                        )
                    ),
                    obsidianCollector = Robot(
                        mutableMapOf(
                            ORE to data[3],
                            CLAY to data[4]
                        )
                    ),
                    geodeCollector = Robot(
                        mutableMapOf(
                            ORE to data[5],
                            OBSIDIAN to data[6]
                        )
                    )
                )
            }
        }
    }
}
