package org.example.graph.parser.strategy.factory.impl;

import org.example.Constants;
import org.example.graph.parser.strategy.GraphParserStrategy;
import org.example.graph.parser.strategy.factory.GraphParserStrategyFactory;
import org.example.graph.parser.strategy.factory.exception.XmlGraphParserStrategyFactoryException;
import org.example.graph.parser.strategy.impl.xml.XmlGraphParserStrategy;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class XmlGraphParserStrategyFactory extends GraphParserStrategyFactory {

    @Override
    public GraphParserStrategy newStrategy() throws XmlGraphParserStrategyFactoryException {
        String fileName;
        Properties props = new Properties();
        try {
            props.load(new FileReader(Constants.APP_PROPS_FILE_NAME));
            fileName = props.getProperty(Constants.GRAPH_SOURCE_FILE_NAME);
        } catch (IOException e) {
            throw new XmlGraphParserStrategyFactoryException(e);
        }
        return new XmlGraphParserStrategy(fileName);
    }
}
