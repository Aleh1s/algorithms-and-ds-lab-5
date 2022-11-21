package org.example.graph.parser.strategy;

import com.google.common.graph.MutableValueGraph;
import org.example.graph.Vertex;
import org.example.graph.parser.strategy.exception.GraphParserStrategyException;

public interface GraphParserStrategy {
    void parse(String fileName) throws GraphParserStrategyException;
    MutableValueGraph<Vertex, Integer> getValueGraph();
}
