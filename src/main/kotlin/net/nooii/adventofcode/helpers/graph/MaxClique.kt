package net.nooii.adventofcode.helpers.graph

/**
 * Finds the maximum clique in the graph using Bron-Kerbosch algorithm.
 * (= number of vertices with existing edge from every other vertex)
 */
fun Graph.findMaxClique(): Set<Vertex> {
    var maxClique = emptySet<Vertex>()
    val connections = createVertexToSetOfVerticesMap()

    fun bronKerbosch(
        r: Set<Vertex>,
        p: Set<Vertex>,
        x: Set<Vertex>
    ) {
        if (p.isEmpty() && x.isEmpty()) {
            // Found a maximal clique
            if (r.size > maxClique.size) {
                maxClique = r
            }
            return
        }
        val pivot = (p + x).firstOrNull() ?: return
        val nonNeighbors = p - connections[pivot]
        for (v in nonNeighbors) {
            // Reduce search space by immutably updating `p` and `x`
            bronKerbosch(r + v, p.intersect(connections[v]), x.intersect(connections[v]))
        }
    }

    bronKerbosch(emptySet(), connections.keys, emptySet())
    return maxClique
}