package org.example.graph;

import lombok.Setter;

@Setter
public class Vertex {
    private final int id;
    private float pheromone;
    private float proximity;

    private Vertex(int id) {
        this.id = id;
        this.pheromone = 0.2f;
    }

    public static Vertex from(int id) {
        return new Vertex(id);
    }
}
