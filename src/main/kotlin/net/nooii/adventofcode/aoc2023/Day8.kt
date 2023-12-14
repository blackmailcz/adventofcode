package net.nooii.adventofcode.aoc2023

import net.nooii.adventofcode.helpers.*

class Day8 {

    private class Network(
        val sequence: String,
        val nodes: NNMap<String, Node>
    ) {
        fun getDirection(i: Long) = sequence[(i % sequence.length).toInt()]
    }

    private class Node(
        val left: String,
        val right: String
    )

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2023).loadStrings("Day8Input")
            val network = processInput(input)
            part1(network)
            part2(network)
        }

        private fun part1(network: Network) {
            var node = "AAA"
            var i = 0L
            while (node != "ZZZ") {
                node = getNextNode(i, node, network)
                i++
            }
            println(i)
        }

        private fun part2(network: Network) {
            val startNodes = network.nodes.keys.filter { it[2] == 'A' }
            val ghostSteps = mutableSetOf<Long>()
            for (startNode in startNodes) {
                // We need to find after how many steps will the ghost run in circles.
                // To find it, following conditions have to be met:
                // - Node must end with Z (= the ghost actually reached the end)
                // - Sequence modulo must be 0 (= the ghost will choose the same directions)
                // - The next node after reaching "Z node" must be the same as A's next node (= the ghost will run over the exactly same nodes)
                // Note: For the given input, condition 1 is enough.
                var node = startNode
                var i = 0L
                var modulo = -1L
                var nodeCheckConditionMet = false
                val nodeToCompare = getNextNode(0, startNode, network) // Save the A's next node in the given direction
                while (node[2] != 'Z' || modulo != 0L || !nodeCheckConditionMet) {
                    node = getNextNode(i, node, network)
                    nodeCheckConditionMet = getNextNode(i, node, network) == nodeToCompare
                    i++
                    modulo = i % network.sequence.length
                }
                ghostSteps.add(i)
            }
            // We need to find the first moment, that means LCM of all the ghosts' steps.
            println(lcm(ghostSteps))
        }

        private fun getNextNode(i: Long, node: String, network: Network): String {
            return when (val direction = network.getDirection(i)) {
                'L' -> network.nodes[node].left
                'R' -> network.nodes[node].right
                else -> error("Invalid direction: $direction")
            }
        }

        private fun processInput(input: List<String>): Network {
            val sequence = input.first()
            val regex = Regex("(\\S+) = \\((\\S+), (\\S+)\\)")
            val nodes = mutableMapOf<String, Node>()
            for (line in input.drop(2)) {
                val (value, left, right) = regex.captureFirstMatch(line)
                nodes[value] = Node(left, right)
            }
            return Network(sequence, nodes.toImmutable())
        }
    }
}