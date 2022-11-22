package org.example.graph.parser.strategy.factory;

import org.example.graph.parser.strategy.GraphParserStrategy;
import org.example.graph.parser.strategy.factory.exception.GraphParserStrategyFactoryException;
import org.example.graph.parser.strategy.factory.impl.XmlGraphParserStrategyFactory;

public abstract class GraphParserStrategyFactory {

    public static GraphParserStrategyFactory newInstance() {
        return new XmlGraphParserStrategyFactory();
    }
    public abstract GraphParserStrategy newStrategy() throws GraphParserStrategyFactoryException;
}
