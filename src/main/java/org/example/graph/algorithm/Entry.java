package org.example.graph.algorithm;

import org.example.graph.Vertex;

public class Entry {
    private final Vertex vertex;
    private final double pheromone;

    private Entry(Vertex vertex, double pheromone) {
        this.vertex = vertex;
        this.pheromone = pheromone;
    }

    public static Entry from(Vertex v, double p) {
        return new Entry(v, p);
    }

    public Vertex getVertex() {
        return vertex;
    }

    public double getPheromone() {
        return pheromone;
    }
}
