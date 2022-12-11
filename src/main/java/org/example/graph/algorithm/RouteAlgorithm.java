package org.example.graph.algorithm;

import com.google.common.graph.MutableValueGraph;
import org.example.graph.Edge;
import org.example.graph.Vertex;
import org.example.graph.algorithm.exception.RouteAlgorithmException;
import org.example.graph.parser.GraphParser;

import java.util.List;

import static java.util.Objects.requireNonNull;


public abstract class RouteAlgorithm {
    protected GraphParser graphParser;
    public RouteAlgorithm(GraphParser graphParser) {
        this.graphParser = requireNonNull(graphParser);
    }
    public abstract List<Vertex> buildRoute(Vertex start, Vertex terminal) throws RouteAlgorithmException;
    public abstract MutableValueGraph<Vertex, Edge> getGraph();
}
