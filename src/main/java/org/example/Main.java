package org.example;

import lombok.SneakyThrows;
import org.example.graph.Vertex;
import org.example.graph.algorithm.RouteAlgorithm;
import org.example.graph.algorithm.factory.RouteAlgorithmFactory;
import org.example.graph.algorithm.impl.ant.factory.AntAlgorithmFactory;
import org.example.graph.parser.GraphParser;
import org.example.graph.parser.strategy.impl.xml.XmlGraphParserStrategy;

import java.util.Stack;

import static org.example.Constants.GRAPH_SOURCE_FILE_NAME;

public class Main {
    @SneakyThrows
    public static void main(String[] args) {
        XmlGraphParserStrategy strategy = new XmlGraphParserStrategy(GRAPH_SOURCE_FILE_NAME);
        GraphParser graphParser = new GraphParser(strategy);
        RouteAlgorithmFactory factory = AntAlgorithmFactory.newInstance();
        factory.setGraphParser(graphParser);
        RouteAlgorithm routeAlgorithm = factory.createRouteAlgorithm();
        Stack<Vertex> vertices = routeAlgorithm.buildRoute(Vertex.from(1), Vertex.from(10));
        System.out.println(vertices);
    }
}