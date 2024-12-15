package net.nooii.adventofcode.helpers.graph

/**
 * Represents the result of a minimum cut operation on a graph.
 *
 * @property graph1 The first subgraph resulting from the cut.
 * @property graph2 The second subgraph resulting from the cut.
 * @property cutters The set of edges that were removed to create the cut.
 * @property weight The total weight of the cut (sum of weights of the cut edges).
 */
class MinCutResult(
    val graph1: Graph,
    val graph2: Graph,
    val cutters: Set<Edge>,
    val weight: Int
)

/**
 * Represents a cut in a graph during the minimum cut algorithm.
 *
 * @property v1 The first vertex of the cut.
 * @property v2 The second vertex of the cut.
 * @property weight The weight of the cut, representing the sum of weights of edges between v1 and v2.
 */
private data class Cut(
    val v1: Vertex,
    val v2: Vertex,
    val weight: Int
)

/**
 * Finds the minimum cut of the graph using the Stoer-Wagner algorithm.
 *
 * This function creates a copy of the original graph and performs the minimum cut algorithm on the copy.
 * It iteratively finds cuts and merges vertices until only one vertex remains, keeping track of the best cut found.
 *
 * @receiver The graph on which to perform the minimum cut algorithm.
 * @return A [MinCutResult] object containing information about the minimum cut, or null if no cut was found.
 */
fun Graph.findMinCut(): MinCutResult? {
    // Copy graph, because cutting will destroy it
    val copy = Graph()
    for (edge in edges.values) {
        copy.addEdge(Edge(edge.v1, edge.v2, edge.weight))
    }

    with(copy) {
        val cuts = mutableListOf<Int>()
        var bestCut: Cut? = null
        while (numberOfVertices() > 1) {
            val internalCut = findCut()
            if (bestCut == null || internalCut.weight < bestCut.weight) {
                bestCut = internalCut
            }
            cuts.add(internalCut.weight)
            mergeVertices(internalCut.v1, internalCut.v2)
        }
        // We need to process the cut
        return bestCut?.let { buildMinCutResult(this@findMinCut, it) }
    }
}

/**
 * Finds a cut in the graph using the Stoer-Wagner algorithm.
 *
 * This function implements a phase of the Stoer-Wagner algorithm to find a cut in the graph.
 * It starts with a random vertex and gradually builds a "potato" (a growing set of vertices)
 * until only one vertex remains outside the potato. The last edge added to the potato
 * represents the cut.
 *
 * @receiver The graph on which to perform the cut-finding operation.
 * @return A [Cut] object representing the found cut, containing the two vertices that define
 *         the cut and the weight of the cut.
 */
private fun Graph.findCut(): Cut {
    // Stoer-Wagner algorithm to find all cuts in the graph

    // Choose random vertex
    val randomVertex = vertices.values.random()

    var remaining = vertices.size

    // Start forming a "Potato" - a big chunk of all the nodes
    val potato = Vertex("_potato")
    addVertex(potato)
    val potatoVertices = mutableListOf(randomVertex)
    val potatoVerticesSet = mutableSetOf(randomVertex) // Set variant for fast checking of "in"

    // Add all edges of the random vertex to the potato
    for (edge in vertexToEdgesMap[randomVertex]!!) {
        addEdge(Edge(potato, edge.getOtherVertex(randomVertex), edge.weight))
    }
    // Now fill the potato
    while (true) {
        // Select the vertex of edges linked with potato with the highest weight
        val nextVertex = getEdges(potato)
            .filter { it.getOtherVertex(potato) !in potatoVertices }
            .maxBy { it.weight }
            .getOtherVertex(potato)
        remaining--
        potatoVertices.add(nextVertex)
        potatoVerticesSet.add(nextVertex)
        relaxate(nextVertex, potato, potatoVerticesSet)
        if (remaining == 1) {
            val cutWeight = edges[EdgeKey(potato, nextVertex)]!!.weight
            removeVertex(potato)
            return Cut(potatoVertices.last(), potatoVertices[potatoVertices.size - 2], cutWeight)
        }
        removeEdge(EdgeKey(potato, nextVertex))
    }
}

/**
 * Merges two vertices in the graph into a single new vertex.
 *
 * This function creates a new vertex that combines the properties of two existing vertices.
 * It handles the edges connected to the both vertices, adjusting weights where necessary,
 * and removes the original vertices from the graph.
 *
 * @param v1 The first vertex to be merged.
 * @param v2 The second vertex to be merged.
 * @return A new [Vertex] that represents the merged result of v1 and v2.
 */
private fun Graph.mergeVertices(v1: Vertex, v2: Vertex): Vertex {
    val out = Vertex("${v1.name}-${v2.name}")
    addVertex(out)
    val edgesToAdd1 = mutableSetOf<Edge>()
    for (v1edge in vertexToEdgesMap[v1]!!) {
        if (v1edge.getOtherVertex(v1) != v2) {
            edgesToAdd1 += Edge(out, v1edge.getOtherVertex(v1), v1edge.weight)
        }
    }
    for (edge in edgesToAdd1) {
        addEdge(edge)
    }
    val edgesToAdd2 = mutableSetOf<Edge>()
    for (v2edge in vertexToEdgesMap[v2]!!) {
        if (v2edge.getOtherVertex(v2) == v1) {
            continue
        }
        edgesToAdd1 += Edge(out, v1, v2edge.weight)
        val existingEdge = edges[EdgeKey(out, v2edge.getOtherVertex(v2))]
        if (existingEdge != null) {
            existingEdge.weight += v2edge.weight
        } else {
            edgesToAdd2 += Edge(out, v2edge.getOtherVertex(v2), v2edge.weight)
        }
    }
    for (edge in edgesToAdd2) {
        addEdge(edge)
    }
    removeVertex(v1)
    removeVertex(v2)
    return out
}

/**
 * Relaxes the edges between a given vertex and a "potato" vertex in the graph.
 *
 * This function is part of the Stoer-Wagner algorithm implementation. It updates the edges
 * connecting the given vertex to the potato vertex, which represents a growing set of vertices.
 * If an edge already exists between the potato and another vertex, its weight is increased.
 * If no such edge exists, a new edge is created.
 *
 * @param vertex The vertex whose edges are being relaxed.
 * @param potato The "potato" vertex representing the growing set of vertices.
 * @param potatoVertices A set of vertices already included in the potato.
 */
private fun Graph.relaxate(vertex: Vertex, potato: Vertex, potatoVertices: Set<Vertex>) {
    for (edge in vertexToEdgesMap[vertex]!!) {
        val otherVertex = edge.getOtherVertex(vertex)
        if (otherVertex == potato || otherVertex in potatoVertices) {
            continue
        }
        // Check if there is edge between that vertex and the potato
        var potatoEdgeFound = false
        for (potatoEdge in vertexToEdgesMap[potato]!!) {
            val o = potatoEdge.getOtherVertex(potato)
            if (o == otherVertex) {
                // There is already such edge, we will add the weight
                potatoEdge.weight += edge.weight
                potatoEdgeFound = true
                break
            }
        }
        if (!potatoEdgeFound) {
            addEdge(Edge(potato, otherVertex, edge.weight))
        }
    }
}

/**
 * Builds a [MinCutResult] object based on the original graph and the found cut.
 *
 * This function takes the original graph and the cut information, and constructs two subgraphs
 * representing the partitions created by the minimum cut. It also identifies the edges that
 * form the cut itself.
 *
 * @param originalGraph The original graph on which the minimum cut was performed.
 * @param cut The [Cut] object representing the minimum cut found in the graph.
 * @return A [MinCutResult] object containing the two partitioned subgraphs, the set of edges
 *         that form the cut, and the weight of the cut.
 */
private fun buildMinCutResult(originalGraph: Graph, cut: Cut): MinCutResult {
    val graph1 = Graph()
    val graph2 = Graph()
    val cutters = mutableSetOf<Edge>()
    val subVerticesNames = cut.v1.name.split("-")
    for (vertex in originalGraph.vertices.values) {
        if (vertex.name in subVerticesNames) {
            graph1.addVertex(vertex)
        } else {
            graph2.addVertex(vertex)
        }
    }
    for (edge in originalGraph.edges.values) {
        when {
            edge.v1 in graph1.vertices.values && edge.v2 in graph1.vertices.values -> {
                graph1.addEdge(edge)
            }
            edge.v1 in graph2.vertices.values && edge.v2 in graph2.vertices.values -> {
                graph2.addEdge(edge)
            }
            else -> {
                cutters.add(edge)
            }
        }
    }
    return MinCutResult(graph1, graph2, cutters, cut.weight)
}