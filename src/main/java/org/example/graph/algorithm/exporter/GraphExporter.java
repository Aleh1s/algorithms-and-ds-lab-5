package org.example.graph.algorithm.exporter;

import com.google.common.graph.EndpointPair;
import com.google.common.graph.MutableValueGraph;
import org.example.graph.Edge;
import org.example.graph.Vertex;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public class GraphExporter {

    private MutableValueGraph<Vertex, Edge> graph;
    private Set<Vertex> path;
    private List<String> lines;
    private final String fileName;

    public GraphExporter(String fileName) {
        this.fileName = requireNonNull(fileName);
        this.lines = new LinkedList<>();
    }

    public void export() {
        build();
        write();
    }

    private void build() {
        List<String> lines = new LinkedList<>();
        lines.add("graph G {");
        for (Vertex v : graph.nodes()) {
            if (path.contains(v))
                lines.add("  %d [style=filled, color=blue4, fillcolor=yellow, penwidth=3]".formatted(v.getId()));
            else
                lines.add("  %d [style=filled, color=blue4, fillcolor=white, penwidth=3]".formatted(v.getId()));
        }

        lines.add("");

        for (EndpointPair<Vertex> endpointPair : graph.edges())
            lines.add("  %d -- %d [label=\"%d\", penwidth=%s, color=aquamarine3]".formatted(
                    endpointPair.nodeU().getId(),
                    endpointPair.nodeV().getId(),
                    graph.edgeValue(endpointPair).orElseThrow().getDistance(),
                    formatPheromone(graph.edgeValue(endpointPair).orElseThrow().getPheromone())));

        lines.add("}");
        this.lines = lines;
    }

    private String formatPheromone(double pheromone) {
        if (Double.compare(pheromone, 10.0) > 0)
            return String.valueOf(10);
        else if (Double.compare(pheromone, 0.5) < 0)
            return String.valueOf(0.5);
        else {
            String result = String.valueOf(pheromone).replace(',', '.');
            return result.substring(0, 7);
        }
    }

    private void write() {
        try {
            Files.write(Path.of(fileName), lines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setGraph(MutableValueGraph<Vertex, Edge> graph) {
        this.graph = Objects.requireNonNull(graph);
    }

    public void setPath(Set<Vertex> path) {
        this.path = path;
    }
}
