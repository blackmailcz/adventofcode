package net.nooii.adventofcode.helpers.graph

/**
 * Represents a vertex in a graph.
 *
 * @property name The unique identifier for this vertex.
 */
data class Vertex(
    val name: String
) {
    override fun toString(): String {
        return name
    }
}

/**
 * Represents a unique key for an edge in a graph.
 *
 * This class is used to identify an edge based on its vertices, regardless of their order.
 * It can be constructed from an existing Edge object or from two Vertex objects.
 *
 * @property vertices A set of vertices that form the edge. The set ensures uniqueness and order-independence.
 */
data class EdgeKey(val vertices: Set<Vertex>) {
    /**
     * Constructs an EdgeKey from an existing Edge object.
     *
     * @param edge The Edge object from which to create the EdgeKey.
     */
    constructor(edge: Edge) : this(edge.vertices)

    /**
     * Constructs an EdgeKey from two Vertex objects.
     *
     * @param v1 The first vertex of the edge.
     * @param v2 The second vertex of the edge.
     */
    constructor(v1: Vertex, v2: Vertex) : this(setOf(v1, v2))
}

/**
 * Represents an edge in a graph, connecting two vertices.
 *
 * @property vertices The set of vertices that this edge connects.
 * @property weight The weight or cost associated with this edge. Defaults to 1.
 */
class Edge(
    val vertices: Set<Vertex>,
    var weight: Int = 1
) {

    /**
     * Constructs an edge from two vertices.
     *
     * @param v1 The first vertex of the edge.
     * @param v2 The second vertex of the edge.
     * @param weight The weight of the edge. Defaults to 1.
     */
    constructor(v1: Vertex, v2: Vertex, weight: Int = 1) : this(setOf(v1, v2), weight)

    val v1 = vertices.first()
    val v2 = vertices.last()

    /**
     * The name of the edge, formed by concatenating the names of its vertices.
     */
    val name: String = v1.name + " - " + v2.name

    /**
     * Gets a vertex of the edge based on a given vertex.
     *
     * @param vertex The vertex to look up.
     * @return The matching vertex from the edge.
     */
    fun getVertex(vertex: Vertex) = getVertex(name)

    /**
     * Gets a vertex of the edge based on its name.
     *
     * @param name The name of the vertex to look up.
     * @return The matching vertex from the edge.
     * @throws IllegalStateException if the vertex is not found in this edge.
     */
    fun getVertex(name: String): Vertex {
        return when (name) {
            v1.name -> v1
            v2.name -> v2
            else -> error("Vertex $name not found in this edge")
        }
    }

    /**
     * Gets the other vertex of the edge given one vertex.
     *
     * @param vertex The known vertex of the edge.
     * @return The other vertex of the edge.
     */
    fun getOtherVertex(vertex: Vertex): Vertex = getOtherVertex(vertex.name)

    /**
     * Gets the other vertex of the edge given the name of one vertex.
     *
     * @param name The name of the known vertex of the edge.
     * @return The other vertex of the edge.
     * @throws IllegalStateException if the given vertex name is not found in this edge.
     */
    fun getOtherVertex(name: String): Vertex {
        return when (name) {
            v1.name -> v2
            v2.name -> v1
            else -> error("Vertex $name not found in this edge")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Edge) return false

        if (vertices != other.vertices) return false

        return true
    }

    override fun hashCode(): Int {
        return vertices.hashCode()
    }

    override fun toString(): String {
        return "$name (w: $weight)"
    }
}

/**
 * Represents a graph defined by [Vertex] and [Edge] objects.
 */
class Graph {

    val vertices = mutableMapOf<String, Vertex>()
    val edges = mutableMapOf<EdgeKey, Edge>()

    val vertexToEdgesMap = mutableMapOf<Vertex, MutableSet<Edge>>()

    /**
     * Returns the number of vertices in the graph.
     *
     * @return The total count of vertices in the graph.
     */
    fun numberOfVertices() = vertices.size

    /**
     * Returns the number of edges in the graph.
     *
     * @return The total count of edges in the graph.
     */
    fun numberOfEdges() = edges.size

    /**
     * Retrieves all edges connected to a given vertex.
     *
     * @param vertex The vertex for which to retrieve the connected edges.
     * @return A set of edges connected to the given vertex, or an empty set if the vertex has no edges.
     */
    fun getEdges(vertex: Vertex): Set<Edge> {
        return vertexToEdgesMap[vertex] ?: emptySet()
    }

    /**
     * Adds a new vertex to the graph.
     *
     * @param vertex The vertex to be added to the graph.
     */
    fun addVertex(vertex: Vertex) {
        vertices.computeIfAbsent(vertex.name) { vertex }
        vertexToEdgesMap.computeIfAbsent(vertex) { mutableSetOf() }
    }

    /**
     * Adds a new edge to the graph.
     *
     * @param edge The edge to be added to the graph.
     */
    fun addEdge(edge: Edge) {
        addVertex(edge.v1)
        addVertex(edge.v2)
        vertexToEdgesMap[edge.v1]?.add(edge)
        vertexToEdgesMap[edge.v2]?.add(edge)
        edges[EdgeKey(edge)] = edge
    }

    /**
     * Removes a vertex and all its connected edges from the graph.
     *
     * @param vertex The vertex to be removed from the graph.
     */
    fun removeVertex(vertex: Vertex) {
        // Remove all connected edges with the vertices
        val edgesToRemove = vertexToEdgesMap[vertex]?.toSet() ?: emptySet()
        for (e in edgesToRemove) {
            removeEdge(e)
        }
        vertexToEdgesMap.remove(vertex)
        vertices.remove(vertex.name)
    }

    /**
     * Removes a vertex by its name and all its connected edges from the graph.
     *
     * @param name The name of the vertex to be removed from the graph.
     */
    fun removeVertex(name: String) = vertices[name]?.let { removeVertex(it) }

    /**
     * Removes an edge from the graph. This does not remove the vertices themselves.
     *
     * @param edge The edge to be removed from the graph.
     */
    fun removeEdge(edge: Edge) {
        for (v in edge.vertices) {
            vertexToEdgesMap[v]?.remove(edge)
        }
        edges.remove(EdgeKey(edge))
    }

    /**
     * Removes an edge from the graph using its EdgeKey. This does not remove the vertices themselves.
     *
     * @param edgeKey The EdgeKey of the edge to be removed from the graph.
     */
    fun removeEdge(edgeKey: EdgeKey) {
        edges[edgeKey]?.let { removeEdge(it) }
    }

    /**
     * Removes all edges connected to a specific vertex. This does not remove the vertex itself.
     *
     * @param vertex The vertex whose connected edges should be removed.
     */
    fun removeEdgesOf(vertex: Vertex) {
        val edgesToRemove = vertexToEdgesMap[vertex]?.toSet() ?: emptySet()
        for (edge in edgesToRemove) {
            removeEdge(edge)
        }
    }
}