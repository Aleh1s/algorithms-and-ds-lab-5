package org.example.graph.algorithm.factory;

import org.example.graph.algorithm.RouteAlgorithm;
import org.example.graph.algorithm.exception.RouteAlgorithmFactoryException;
import org.example.graph.algorithm.exporter.GraphExporter;
import org.example.graph.parser.GraphParser;

public interface RouteAlgorithmFactory {
    RouteAlgorithm createRouteAlgorithm() throws RouteAlgorithmFactoryException;
    void setGraphParser(GraphParser graphParser);
    void setGraphExporter(GraphExporter graphExporter);
}
