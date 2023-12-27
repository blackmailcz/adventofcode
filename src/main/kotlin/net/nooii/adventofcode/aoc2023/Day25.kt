package net.nooii.adventofcode.aoc2023

import net.nooii.adventofcode.helpers.AoCYear
import net.nooii.adventofcode.helpers.InputLoader
import net.nooii.adventofcode.helpers.graph.Edge
import net.nooii.adventofcode.helpers.graph.Graph
import net.nooii.adventofcode.helpers.graph.Vertex
import net.nooii.adventofcode.helpers.graph.findMinCut

class Day25 {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2023).loadStrings("Day25Input")
            val edges = processInput(input)
            val graph = Graph()
            for (edge in edges) {
                graph.addEdge(edge)
            }
            // Runtime ~ 193 seconds
            part1(graph)
        }

        private fun part1(graph: Graph) {
            val minCut = graph.findMinCut() ?: error("No solution found")
            check(minCut.cutters.size == 3)
            println(minCut.graph1.numberOfVertices() * minCut.graph2.numberOfVertices())
        }


        private fun processInput(input: List<String>): Set<Edge> {
            val edges = mutableSetOf<Edge>()
            for (line in input) {
                val source = line.substringBefore(": ")
                val destinations = line.substringAfter(": ").split(" ")
                for (destination in destinations) {
                    edges += Edge(setOf(Vertex(source), Vertex(destination)))
                }
            }
            return edges
        }
    }
}
