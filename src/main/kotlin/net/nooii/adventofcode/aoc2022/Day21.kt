package net.nooii.adventofcode.aoc2022

import net.nooii.adventofcode.helpers.*

class Day21 {

    private enum class Operator(val sign: String) {
        PLUS("+"),
        MINUS("-"),
        TIMES("*"),
        DIVIDE("/");

        companion object {

            fun from(sign: String) = entries.first { it.sign == sign }
        }
    }

    private sealed class Monkey(protected val id: String) {

        abstract fun compute(monkeys: NonNullMap<String, Monkey>): Long

        class Simple(id: String, private val value: Long) : Monkey(id) {

            override fun compute(monkeys: NonNullMap<String, Monkey>) = value
        }

        class Complex(
            id: String,
            val left: String,
            val right: String,
            private val operator: Operator
        ) : Monkey(id) {

            val children = listOf(left, right)

            override fun compute(monkeys: NonNullMap<String, Monkey>): Long {
                return when (operator) {
                    Operator.PLUS -> monkeys[left].compute(monkeys) + monkeys[right].compute(monkeys)
                    Operator.MINUS -> monkeys[left].compute(monkeys) - monkeys[right].compute(monkeys)
                    Operator.TIMES -> monkeys[left].compute(monkeys) * monkeys[right].compute(monkeys)
                    Operator.DIVIDE -> monkeys[left].compute(monkeys) / monkeys[right].compute(monkeys)
                }
            }

            fun tryEvaluate(
                monkeys: NonNullMap<String, Monkey>,
                evaluatedMonkeys: NonNullMap<String, Long>
            ) {
                // Check if reversal process is even needed:
                val known: String
                val unknown: String
                when {
                    left in evaluatedMonkeys && right !in evaluatedMonkeys -> {
                        known = left
                        unknown = right
                    }
                    left !in evaluatedMonkeys && right in evaluatedMonkeys -> {
                        unknown = left
                        known = right
                    }
                    else -> return // Either can't evaluate or further evaluation is not needed
                }

                evaluatedMonkeys[unknown] = when (operator) {
                    Operator.PLUS -> evaluatedMonkeys[id] - evaluatedMonkeys[known]
                    Operator.MINUS -> {
                        when (known) {
                            left -> evaluatedMonkeys[known] - evaluatedMonkeys[id]
                            right -> evaluatedMonkeys[id] + evaluatedMonkeys[known]
                            else -> error("No known monkey")
                        }
                    }
                    Operator.TIMES -> evaluatedMonkeys[id] / evaluatedMonkeys[known]
                    Operator.DIVIDE -> {
                        when (known) {
                            left -> evaluatedMonkeys[known] / evaluatedMonkeys[id]
                            right -> evaluatedMonkeys[id] * evaluatedMonkeys[known]
                            else -> error("No known monkey")
                        }
                    }
                }

                // Evaluate deeper
                (monkeys[unknown] as? Complex)?.tryEvaluate(monkeys, evaluatedMonkeys)
            }
        }

        class Unknown(id: String) : Monkey(id) {

            override fun compute(monkeys: NonNullMap<String, Monkey>): Long {
                // Exception is more convenient than null.
                throw IllegalStateException("Cannot compute")
            }
        }
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2022).loadStrings("Day21Input")
            part1(parseInput(input))
            part2(parseInput(input))
        }

        private fun part1(monkeys: NonNullMap<String, Monkey>) {
            val result = monkeys["root"].compute(monkeys)
            println(result)
        }

        private fun part2(monkeys: NonNullMap<String, Monkey>) {
            val root = monkeys["root"] as Monkey.Complex
            monkeys["root"] = Monkey.Unknown("root")
            monkeys["humn"] = Monkey.Unknown("humn")
            val evaluatedMonkeys = NonNullMap<String, Long>()
            // Start in root and collect all monkeys that can be evaluated
            var pendingMonkeys = mutableMapOf(
                root.left to monkeys[root.left],
                root.right to monkeys[root.right]
            )
            while (pendingMonkeys.isNotEmpty()) {
                val nextMonkeys = mutableMapOf<String, Monkey>()
                for ((monkeyId, monkey) in pendingMonkeys) {
                    when (monkey) {
                        is Monkey.Simple -> evaluatedMonkeys[monkeyId] = monkey.compute(monkeys)
                        is Monkey.Complex -> {
                            for (child in monkey.children) {
                                try {
                                    evaluatedMonkeys[child] = monkeys[child].compute(monkeys)
                                } catch (e: IllegalStateException) {
                                    nextMonkeys[child] = monkeys[child]
                                }
                            }
                            if (monkey.children.all { it in evaluatedMonkeys }) {
                                evaluatedMonkeys[monkeyId] = monkey.compute(monkeys)
                            }
                        }
                        is Monkey.Unknown -> continue
                    }
                }
                pendingMonkeys = nextMonkeys
            }
            // Complete the equality and evaluate the unknown operand of the root
            when {
                root.left !in evaluatedMonkeys -> {
                    evaluatedMonkeys[root.left] = evaluatedMonkeys[root.right]
                    (monkeys[root.left] as Monkey.Complex).tryEvaluate(monkeys, evaluatedMonkeys)
                }
                root.right !in evaluatedMonkeys -> {
                    evaluatedMonkeys[root.right] = evaluatedMonkeys[root.left]
                    (monkeys[root.right] as Monkey.Complex).tryEvaluate(monkeys, evaluatedMonkeys)
                }
            }
            println(evaluatedMonkeys["humn"])
        }

        private fun parseInput(input: List<String>): NonNullMap<String, Monkey> {
            val simple = Regex("(\\w+): (\\d+)")
            val complex = Regex("(\\w+): (\\w+) (.) (\\w+)")
            return NonNullMap(input.associate { line ->
                when {
                    line.matches(simple) -> {
                        val (id, value) = simple.captureFirstMatch(line) { it }
                        id to Monkey.Simple(id, value.toLong())
                    }
                    line.matches(complex) -> {
                        val (id, left, op, right) = complex.captureFirstMatch(line) { it }
                        id to Monkey.Complex(
                            id = id,
                            left = left,
                            right = right,
                            operator = Operator.from(op)
                        )
                    }
                    else -> error("Unknown line")
                }
            }.toMutableMap())
        }
    }
}