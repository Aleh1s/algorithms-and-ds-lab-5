package org.example.graph.algorithm;

import org.example.graph.Vertex;

import java.util.Comparator;

public class Entry implements Comparable<Entry> {
    private final Vertex vertex;
    private final int distance;
    private final double pheromone;

    private Entry(Vertex vertex, double pheromone, int distance) {
        this.vertex = vertex;
        this.pheromone = pheromone;
        this.distance = distance;
    }

    public static Entry from(Vertex vertex, double pheromone, int distance) {
        return new Entry(vertex, pheromone, distance);
    }

    public Vertex getVertex() {
        return vertex;
    }

    public double getPheromone() {
        return pheromone;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public int compareTo(Entry o) {
        return comparator().compare(this, o);
    }

    public Comparator<Entry> comparator() {
        return Comparator.comparing(Entry::getPheromone)
                .reversed().thenComparing(Entry::getDistance);
    }
}
