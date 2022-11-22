package org.example.graph.algorithm;

import org.example.graph.Vertex;
import org.example.graph.algorithm.exception.RouteAlgorithmException;
import org.example.graph.algorithm.exporter.GraphExporter;
import org.example.graph.parser.GraphParser;

import java.util.Stack;

import static java.util.Objects.requireNonNull;


public abstract class RouteAlgorithm {
    protected GraphParser graphParser;
    protected GraphExporter graphExporter;

    public RouteAlgorithm(GraphParser graphParser, GraphExporter graphExporter) {
        this.graphParser = requireNonNull(graphParser);
        this.graphExporter = requireNonNull(graphExporter);
    }

    public abstract Stack<Vertex> buildRoute(Vertex start, Vertex end) throws RouteAlgorithmException;
}
