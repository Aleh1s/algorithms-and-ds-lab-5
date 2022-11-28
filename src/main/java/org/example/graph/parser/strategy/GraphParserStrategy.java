package org.example.graph.parser.strategy;

import com.google.common.graph.MutableValueGraph;
import org.example.graph.Edge;
import org.example.graph.Vertex;
import org.example.graph.parser.strategy.exception.GraphParserStrategyException;

import static java.util.Objects.requireNonNull;

public abstract class GraphParserStrategy {
    protected String fileName;

    public GraphParserStrategy(String fileName) {
        this.fileName = requireNonNull(fileName);
    }

    public abstract void parse() throws GraphParserStrategyException;
    public abstract MutableValueGraph<Vertex, Edge> getValueGraph();
}
