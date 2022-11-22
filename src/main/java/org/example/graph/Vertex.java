package org.example.graph;

import lombok.Setter;

@Setter
public class Vertex {
    private final int id;

    private Vertex(int id) {
        this.id = id;
    }

    public static Vertex from(int id) {
        return new Vertex(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vertex vertex)) return false;

        return id == vertex.id;
    }

    public int getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
