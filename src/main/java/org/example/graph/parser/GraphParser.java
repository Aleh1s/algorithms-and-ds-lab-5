package org.example.graph.parser;

import com.google.common.graph.MutableValueGraph;
import org.example.graph.Edge;
import org.example.graph.Vertex;
import org.example.graph.parser.strategy.GraphParserStrategy;
import org.example.graph.parser.strategy.exception.GraphParserStrategyException;

public class GraphParser {

    private GraphParserStrategy strategy;

    public GraphParser(GraphParserStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(GraphParserStrategy strategy) {
        this.strategy = strategy;
    }

    public MutableValueGraph<Vertex, Edge> parse() throws GraphParserStrategyException {
        strategy.parse();
        return strategy.getValueGraph();
    }

}
