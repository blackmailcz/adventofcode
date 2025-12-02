package net.nooii.adventofcode.aoc2023

import net.nooii.adventofcode.helpers.*

object Day20 {

    // 0 = low input
    // 1 = high input

    private sealed interface Module {
        val id: String
        val next: Set<String>

        fun processPulse(from: Module, pulse: Boolean): Boolean?
    }

    private data class FF(
        override val id: String,
        override val next: Set<String>,
        var isOn: Boolean = false
    ) : Module {

        override fun processPulse(from: Module, pulse: Boolean): Boolean? {
            if (!pulse) {
                isOn = !isOn
                return isOn
            } else {
                return null
            }
        }
    }

    private data class Con(
        override val id: String,
        override val next: Set<String>,
        val inputs: MutableMap<String, Boolean> = mutableMapOf()
    ) : Module {

        override fun processPulse(from: Module, pulse: Boolean): Boolean {
            inputs[from.id] = pulse
            return !inputs.all { it.value }
        }
    }

    private data class Broadcaster(
        override val next: Set<String>,
    ) : Module {
        override val id: String = "broadcaster"

        override fun processPulse(from: Module, pulse: Boolean): Boolean {
            return pulse
        }
    }

    private data object Button : Module {
        override val id: String = "button"
        override val next: Set<String> = setOf("broadcaster")

        override fun processPulse(from: Module, pulse: Boolean): Boolean {
            return false
        }
    }

    private data class Output(override val id: String) : Module {
        override val next: Set<String> = emptySet()
        val inputs: MutableSet<Module> = mutableSetOf()

        override fun processPulse(from: Module, pulse: Boolean): Boolean? {
            return null
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2023).loadStrings("Day20Input")
        val modules = processInput(input)
        part1(modules)
        part2(modules)
    }

    private fun part1(modules: NNMap<String, Module>) {
        var lowCount = 0L
        var highCount = 0L
        repeat(1000) {
            push(modules, onPulseDelivered = {
                if (it.pulse) highCount++ else lowCount++
            })
        }
        reset(modules)
        println(lowCount * highCount)
    }

    private fun part2(modules: NNMap<String, Module>) {
        // Solved on the paper at first

        // The solution is not completely generic, it kinda expects the correct connection of CON modules
        // Also mix of CON + FF modules on a single module input is not supported.

        // Find Con modules that are only made of FF modules
        val ffCons = modules.values
            .filterIsInstance<Con>()
            .filter { conInput -> conInput.inputs.keys.all { modules[it] is FF } }
        // Determine cycle (number of pushes) after which the module emits low pulse and resets to default state
        val conCycles = mutableNNMapOf<String, Long>()
        for (con in ffCons) {
            var i = 0L
            var isLowPulseSent = false
            var resetsBackToInitialState = false
            while (!isLowPulseSent || !resetsBackToInitialState) {
                i++
                push(modules, onPulseAccepted = { state, pulse ->
                    if (state.target.id == con.id && pulse == false) {
                        isLowPulseSent = true
                        conCycles[con.id] = i
                    }
                })
                // After the push, the con must reset to initial state, that will close the cycle
                if (con.inputs.all { !it.value }) {
                    resetsBackToInitialState = true
                }
            }
            reset(modules)
        }
        // Backtrack from RX and break down to all evaluated modules. Since only CONS modules are processed,
        // the number of total pushes will be LCM of all the evaluated modules.
        var endpoints = mutableListOf(modules["rx"])
        while (!endpoints.all { it.id in conCycles }) {
            val nextQueue = mutableListOf<Module>()
            for (module in endpoints) {
                if (module.id in conCycles) {
                    continue
                }
                when (module) {
                    is Output -> nextQueue.addAll(module.inputs)
                    is Con -> nextQueue.addAll(module.inputs.keys.map { modules[it] })
                    else -> error("Only Output/Con modules currently supported")
                }
            }
            endpoints = nextQueue
        }
        val totalPushes = lcm(endpoints.map { conCycles[it.id] })
        println(totalPushes)
    }

    private data class State(
        val source: Module,
        val pulse: Boolean,
        val target: Module,
    )

    private fun reset(modules: NNMap<String, Module>) {
        for (module in modules.values) {
            when (module) {
                is FF -> module.isOn = false
                is Con -> {
                    for (input in module.inputs.keys) {
                        module.inputs[input] = false
                    }
                }
                else -> {}
            }
        }
    }

    private fun push(
        modules: NNMap<String, Module>,
        onPulseAccepted: (state: State, nextPulse: Boolean?) -> Unit = { _, _ -> },
        onPulseDelivered: (state: State) -> Unit = {}
    ) {
        var states = listOf(
            State(
                source = modules["button"],
                target = modules["button"],
                pulse = false
            )
        )
        while (states.isNotEmpty()) {
            val nextStates = mutableListOf<State>()
            for (state in states) {
                val nextPulse = state.target.processPulse(state.source, state.pulse)
                onPulseAccepted.invoke(state, nextPulse)
                if (nextPulse != null) {
                    for (next in state.target.next) {
                        nextStates.add(
                            State(
                                source = state.target,
                                target = modules[next],
                                pulse = nextPulse
                            ).also(onPulseDelivered)
                        )
                    }
                }
            }
            states = nextStates
        }
    }

    private fun processInput(input: List<String>): NNMap<String, Module> {
        val modules: MutableNNMap<String, Module> = mutableNNMapOf(
            "button" to Button,
        )
        val regex = Regex("([%&])(\\w+)")
        for (line in input) {
            val next = line.substringAfter(" -> ").split(", ").toSet()
            if (line.startsWith("broadcaster")) {
                modules["broadcaster"] = Broadcaster(next)
            } else {
                val (type, id) = regex.captureFirstMatch(line)
                when (type) {
                    "%" -> modules[id] = FF(id, next)
                    "&" -> modules[id] = Con(id, next)
                    else -> error("Invalid type: $type")
                }
            }
        }
        // Add possible outputs
        for (module in modules.values.flatMap { it.next }) {
            if (module !in modules) {
                modules[module] = Output(module)
            }
        }
        // Also, we need to determine all inputs of the Con/Output modules, and initialize then to low
        for (module in modules.values) {
            for (next in module.next) {
                val nextModule = modules[next]
                if (nextModule is Con) {
                    nextModule.processPulse(module, false)
                }
                if (nextModule is Output) {
                    nextModule.inputs.add(module)
                }
            }
        }
        return modules
    }
}