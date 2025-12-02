package net.nooii.adventofcode.aoc2015

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.captureFirstMatch
import kotlin.math.max
import kotlin.math.min

object Day22 {

    private enum class Spell(val cost: Int) {
        MAGIC_MISSILE(53),
        DRAIN(73),
        SHIELD(113),
        POISON(173),
        RECHARGE(229)
    }

    private enum class EffectType {
        SHIELD,
        POISON,
        RECHARGE
    }

    private data class Human(
        var hp: Int,
        var mana: Int,
        var manaSpent: Int
    )

    private data class Boss(
        var hp: Int,
        val damage: Int
    )

    private data class Effect(
        val type: EffectType,
        var remaining: Int
    )

    private data class State(
        val isHardDifficulty: Boolean,
        var isHumanActive: Boolean,
        var human: Human,
        var boss: Boss,
        var effects: Set<Effect>,
        var lastSpellUsed: Spell? = null
    )

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2015).loadStrings("Day22Input")
        val boss = processInput(input)
        part1(boss)
        part2(boss)
    }

    private fun part1(boss: Boss) {
        solution(boss, false)
    }

    private fun part2(boss: Boss) {
        solution(boss, true)
    }

    private fun solution(boss: Boss, isHardDifficulty: Boolean) {
        val initialState = State(
            isHardDifficulty = isHardDifficulty,
            isHumanActive = true,
            human = Human(50, 500, 0),
            boss = boss,
            effects = emptySet()
        )
        var states = mutableSetOf(initialState)
        var minManaSpent = Int.MAX_VALUE
        while (states.isNotEmpty()) {
            val nextStates = mutableSetOf<State>()
            for (state in states) {
                if (state.boss.hp <= 0) {
                    minManaSpent = min(minManaSpent, state.human.manaSpent)
                } else {
                    nextStates.addAll(playTurn(state))
                }
            }
            states = nextStates
        }
        println(minManaSpent)
    }

    private fun playTurn(initialState: State): Set<State> {
        val nextStates = mutableSetOf<State>()
        // Create base state to fork from
        val baseState = State(
            isHardDifficulty = initialState.isHardDifficulty,
            isHumanActive = initialState.isHumanActive,
            human = initialState.human.copy(),
            boss = initialState.boss.copy(),
            effects = initialState.effects
        )

        // Hard difficulty
        if (baseState.isHardDifficulty && baseState.isHumanActive) {
            baseState.human.hp--
            if (baseState.human.hp <= 0) {
                return emptySet()
            }
        }

        // Apply effects
        val nextEffects = mutableSetOf<Effect>()
        for (effect in initialState.effects) {
            // Apply
            when (effect.type) {
                EffectType.SHIELD -> {} // No-op
                EffectType.POISON -> baseState.boss.hp -= 3
                EffectType.RECHARGE -> baseState.human.mana += 101
            }
            // Tick
            val remaining = effect.remaining - 1
            if (remaining > 0) {
                nextEffects.add(Effect(effect.type, remaining))
            }
        }
        baseState.effects = nextEffects
        // Check if boss is dead, in this case we only return one state, that will be caught in next iteration
        if (baseState.boss.hp <= 0) {
            return setOf(baseState)
        }
        // Play
        if (baseState.isHumanActive) {
            // Create a state for each possible spell
            for (spell in Spell.entries) {
                // Check if we have enough mana
                if (spell.cost > baseState.human.mana) {
                    continue
                }
                // Fork state
                val forkState = State(
                    isHardDifficulty = baseState.isHardDifficulty,
                    isHumanActive = false,
                    human = baseState.human.copy(),
                    boss = baseState.boss.copy(),
                    effects = baseState.effects
                )

                // Resolve spell
                var castSuccessful = false
                when (spell) {
                    Spell.MAGIC_MISSILE -> {
                        forkState.boss.hp -= 4
                        castSuccessful = true
                    }

                    Spell.DRAIN -> {
                        forkState.boss.hp -= 2
                        forkState.human.hp += 2
                        castSuccessful = true
                    }

                    Spell.SHIELD -> {
                        if (forkState.effects.none { it.type == EffectType.SHIELD }) {
                            forkState.effects += Effect(EffectType.SHIELD, 6)
                            castSuccessful = true
                        }
                    }

                    Spell.POISON -> {
                        if (forkState.effects.none { it.type == EffectType.POISON }) {
                            forkState.effects += Effect(EffectType.POISON, 6)
                            castSuccessful = true
                        }
                    }

                    Spell.RECHARGE -> {
                        if (forkState.effects.none { it.type == EffectType.RECHARGE }) {
                            forkState.effects += Effect(EffectType.RECHARGE, 5)
                            castSuccessful = true
                        }
                    }
                }
                if (castSuccessful) {
                    // Pay for spell
                    forkState.human.mana -= spell.cost
                    forkState.human.manaSpent += spell.cost
                    forkState.lastSpellUsed = spell
                    nextStates.add(forkState)
                }
            }
        } else {
            val forkState = State(
                isHardDifficulty = baseState.isHardDifficulty,
                isHumanActive = true,
                human = baseState.human.copy(),
                boss = baseState.boss.copy(),
                effects = baseState.effects
            )
            val armor = if (forkState.effects.any { it.type == EffectType.SHIELD }) 7 else 0
            forkState.human.hp -= max(1, forkState.boss.damage - armor)
            if (forkState.human.hp > 0) {
                nextStates.add(forkState)
            } else {
                return emptySet()
            }
        }
        return nextStates
    }

    private fun processInput(input: List<String>): Boss {
        val (hp, damage) = input.map { line -> Regex("(\\d+)").captureFirstMatch(line).first().toInt() }
        return Boss(hp, damage)
    }
}