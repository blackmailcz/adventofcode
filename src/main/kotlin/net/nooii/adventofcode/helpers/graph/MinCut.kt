package net.nooii.adventofcode.helpers.graph

class MinCutResult(
    val graph1: Graph,
    val graph2: Graph,
    val cutters: Set<Edge>,
    val weight: Int
)

private data class InternalCut(
    val v1: Vertex,
    val v2: Vertex,
    val weight: Int
)

fun Graph.findMinCut(): MinCutResult? {
    // Copy graph, because cutting will destroy it
    val copy = Graph()
    for (edge in edges.values) {
        copy.addEdge(Edge(edge.v1, edge.v2, edge.weight))
    }

    with(copy) {
        val cuts = mutableListOf<Int>()
        var bestCut: InternalCut? = null
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

private fun Graph.findCut(): InternalCut {
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
            return InternalCut(potatoVertices.last(), potatoVertices[potatoVertices.size - 2], cutWeight)
        }
        removeEdge(EdgeKey(potato, nextVertex))
    }
}

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

private fun buildMinCutResult(originalGraph: Graph, internalCut: InternalCut): MinCutResult {
    val graph1 = Graph()
    val graph2 = Graph()
    val cutters = mutableSetOf<Edge>()
    val subVerticesNames = internalCut.v1.name.split("-")
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
    return MinCutResult(graph1, graph2, cutters, internalCut.weight)
}