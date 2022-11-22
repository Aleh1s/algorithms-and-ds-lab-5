package org.example.graph.algorithm;

import com.google.common.graph.MutableValueGraph;
import org.example.graph.Vertex;

import java.util.Stack;

public class Path {
    private final MutableValueGraph<Vertex, Integer> graph;
    private final Stack<Road> roads;
    private int length;

    public Path(MutableValueGraph<Vertex, Integer> graph) {
        this.roads = new Stack<>();
        this.graph = graph;
    }

    public void addRoad(Road road) {
        length += graph.edgeValue(road.getEdge())
                .orElseThrow(() -> new IllegalStateException("There is no such edge in graph"));
    }

    public Stack<Road> getRoads() {
        return roads;
    }

    public int getLength() {
        return length;
    }

    public double getExtraPheromone(int lMin) {
        return lMin / (double) length;
    }
}
