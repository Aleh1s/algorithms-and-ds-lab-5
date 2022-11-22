package org.example.graph.algorithm;

import org.example.graph.Vertex;
import org.example.graph.algorithm.exception.RouteAlgorithmException;
import org.example.graph.parser.GraphParser;

import java.util.Stack;

import static java.util.Objects.requireNonNull;


public abstract class RouteAlgorithm {
    protected GraphParser graphParser;

    public RouteAlgorithm(GraphParser graphParser) {
        this.graphParser = requireNonNull(graphParser);
    }

    public abstract Stack<Vertex> buildRoute(Vertex start, Vertex end) throws RouteAlgorithmException;
}
