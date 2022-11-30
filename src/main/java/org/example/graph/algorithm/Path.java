package org.example.graph.algorithm;

import com.google.common.graph.EndpointPair;
import com.google.common.graph.MutableValueGraph;
import org.example.graph.Edge;
import org.example.graph.Vertex;

import java.util.List;
import java.util.Stack;

import static com.google.common.graph.EndpointPair.*;

public class Path {
    private final MutableValueGraph<Vertex, Edge> graph;
    private final List<EndpointPair<Vertex>> edges;
    private int length;

    private Path(MutableValueGraph<Vertex, Edge> graph) {
        this.edges = new Stack<>();
        this.graph = graph;
    }

    public void addEdge(Vertex v1, Vertex v2) {
        edges.add(unordered(v1, v2));
        length += graph.edgeValue(v1, v2)
                .orElseThrow()
                .getDistance();
    }

    public List<EndpointPair<Vertex>> getEdges() {
        return edges;
    }

    public static Path valueOf(Stack<Vertex> path, MutableValueGraph<Vertex, Edge> graph) {
        Path p = new Path(graph);

        Vertex curr = path.pop(), next;
        while (!path.empty()) {
            next = path.pop();
            p.addEdge(curr, next);
            curr = next;
        }

        return p;
    }

    public int getLength() {
        return length;
    }

    public double getExtraPheromone(int lMin) {
        return lMin / (double) length;
    }


}
