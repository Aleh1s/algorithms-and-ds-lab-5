package org.example.graph.algorithm.impl.ant;

import com.google.common.graph.MutableValueGraph;
import org.example.graph.Vertex;
import org.example.graph.algorithm.RouteAlgorithm;
import org.example.graph.parser.GraphParser;

import java.util.Stack;

public class AntAlgorithm extends RouteAlgorithm {

    private final int alfa;
    private final int beta;
    private final int lMin;
    private final int numberOfAnts;
    private MutableValueGraph<Vertex, Integer> graph;

    public AntAlgorithm(
            int alfa,
            int beta,
            int lMin,
            int numberOfAnts,
            GraphParser parser) {
        super(parser);
        this.alfa = alfa;
        this.beta = beta;
        this.lMin = lMin;
        this.numberOfAnts = numberOfAnts;
    }


    @Override
    public Stack<Vertex> buildRoute(Vertex start, Vertex end) {
        return null;
    }
}