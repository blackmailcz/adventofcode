package net.nooii.adventofcode.aoc2023

import net.nooii.adventofcode.helpers.*

object Day19 {

    private data class Item(
        val x: Int,
        val m: Int,
        val a: Int,
        val s: Int
    ) {
        fun components() = listOf(x, m, a, s)
    }

    private data class State(
        val x: IntRange,
        val m: IntRange,
        val a: IntRange,
        val s: IntRange,
        val workflow: String,
        val instructionIndex: Int
    ) {
        fun components() = listOf(x, m, a, s)
    }

    private class Constraint(
        val operand1: String,
        val operator: String,
        val operand2: Int
    ) {

        fun constrainValidBranch(operand1ToConstrain: String, currentRange: IntRange): IntRange? {
            return constrain(operand1ToConstrain, currentRange, operator)
        }

        fun constrainInvalidBranch(operand1ToConstrain: String, currentRange: IntRange): IntRange? {
            return constrain(operand1ToConstrain, currentRange, if (operator == ">") "<=" else ">=")
        }

        private fun constrain(operand1ToConstrain: String, currentRange: IntRange, targetOperator: String): IntRange? {
            return if (operand1ToConstrain == operand1) {
                val range = when (targetOperator) {
                    ">" -> IntRange(operand2 + 1, currentRange.last)
                    ">=" -> IntRange(operand2, currentRange.last)
                    "<" -> IntRange(currentRange.first, operand2 - 1)
                    "<=" -> IntRange(currentRange.first, operand2)
                    else -> error("Invalid operator: $operator")
                }
                range.takeIf { it.size() >= 0 }
            } else {
                currentRange
            }
        }
    }

    private class Instruction(
        val jumpTarget: String,
        val constraint: Constraint = Constraint("", ">", 0),
        val condition: (item: Item) -> Boolean = { true },
    )

    private sealed class Workflow(val id: String) {

        class Jump(id: String, val instructions: List<Instruction>) : Workflow(id)
        data object Accept : Workflow("A")
        data object Reject : Workflow("R")
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val input = InputLoader(AoCYear.AOC_2023).loadStrings("Day19Input")
        val workflows = processWorkflows(input)
        val items = processItems(input)
        part1(workflows, items)
        part2(workflows)
    }

    private fun part1(workflows: NNMap<String, Workflow>, items: List<Item>) {
        var sum = 0L
        for (item in items) {
            var workflow: Workflow = workflows["in"]
            wf@
            while (true) {
                if (workflow is Workflow.Jump) {
                    val jumpWorkflow = workflow
                    for (instruction in jumpWorkflow.instructions) {
                        if (instruction.condition.invoke(item)) {
                            workflow = workflows[instruction.jumpTarget]
                            break
                        }
                    }
                } else {
                    break@wf
                }
            }
            if (workflow is Workflow.Accept) {
                sum += item.components().sum()
            }
        }
        println(sum)
    }

    private fun part2(workflows: NNMap<String, Workflow>) {
        val accepted = mutableSetOf<State>()
        var states = mutableSetOf(
            State(
                x = IntRange(1, 4000),
                m = IntRange(1, 4000),
                a = IntRange(1, 4000),
                s = IntRange(1, 4000),
                workflow = "in",
                instructionIndex = 0
            )
        )
        while (states.isNotEmpty()) {
            val nextStates = mutableSetOf<State>()
            for (state in states) {
                // Process given instruction
                when (val workflow = workflows[state.workflow]) {
                    is Workflow.Accept -> accepted.add(state)
                    is Workflow.Reject -> continue
                    is Workflow.Jump -> {
                        val instruction = workflow.instructions.getOrNull(state.instructionIndex) ?: continue
                        // Condition valid branch
                        val validBranchState = constrainValidBranch(state, instruction)
                        if (validBranchState != null) {
                            nextStates.add(validBranchState)
                        }
                        // Condition invalid branch
                        val invalidBranchState = constrainInvalidBranch(state, instruction)
                        if (invalidBranchState != null) {
                            nextStates.add(invalidBranchState)
                        }
                    }
                }
            }
            states = nextStates
        }
        val sum = accepted.sumOf { state ->
            state.components().map { it.size().toLong() }.product()
        }
        println(sum)
    }

    private fun constrainValidBranch(state: State, instruction: Instruction): State? {
        return State(
            x = instruction.constraint.constrainValidBranch("x", state.x) ?: return null,
            m = instruction.constraint.constrainValidBranch("m", state.m) ?: return null,
            a = instruction.constraint.constrainValidBranch("a", state.a) ?: return null,
            s = instruction.constraint.constrainValidBranch("s", state.s) ?: return null,
            workflow = instruction.jumpTarget,
            instructionIndex = 0
        )
    }

    private fun constrainInvalidBranch(state: State, instruction: Instruction): State? {
        return State(
            x = instruction.constraint.constrainInvalidBranch("x", state.x) ?: return null,
            m = instruction.constraint.constrainInvalidBranch("m", state.m) ?: return null,
            a = instruction.constraint.constrainInvalidBranch("a", state.a) ?: return null,
            s = instruction.constraint.constrainInvalidBranch("s", state.s) ?: return null,
            workflow = state.workflow,
            instructionIndex = state.instructionIndex + 1
        )
    }

    private fun processItems(input: List<String>): List<Item> {
        val regex = Regex("x=(\\d+),m=(\\d+),a=(\\d+),s=(\\d+)")
        return input.splitByEmptyLine()[1].map { line ->
            val (x, m, a, s) = regex.captureFirstMatch(line) { it.toInt() }
            Item(x, m, a, s)
        }
    }

    private fun processWorkflows(input: List<String>): NNMap<String, Workflow> {
        val regex = Regex("(\\w+)\\{(.*?)}")
        val workflows = mutableListOf(Workflow.Accept, Workflow.Reject)
        for (line in input.splitByEmptyLine()[0]) {
            val (id, instructions) = regex.captureFirstMatch(line)
            workflows.add(Workflow.Jump(id, instructions.split(",").map { processInstruction(it) }))
        }
        return workflows.associateBy { it.id }.nn()
    }

    private fun processInstruction(line: String): Instruction {
        val compareAndJumpRegex = Regex("([xmas])([><])(\\d+):(\\w+)")
        return if (line.matches(compareAndJumpRegex)) {
            val (operand1S, operation, operand2S, jumpTarget) = compareAndJumpRegex.captureFirstMatch(line)
            val operand1 = when (operand1S) {
                "x" -> { item: Item -> item.x }
                "m" -> { item: Item -> item.m }
                "a" -> { item: Item -> item.a }
                "s" -> { item: Item -> item.s }
                else -> error("Invalid operand: $operand1S")
            }
            val operand2 = operand2S.toInt()
            Instruction(jumpTarget, Constraint(operand1S, operation, operand2)) { item ->
                when (operation) {
                    ">" -> operand1.invoke(item) > operand2
                    "<" -> operand1.invoke(item) < operand2
                    else -> error("Invalid operation: $operation")
                }
            }
        } else {
            Instruction(line)
        }
    }
}