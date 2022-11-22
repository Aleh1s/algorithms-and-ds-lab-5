package org.example;

import lombok.SneakyThrows;
import org.example.graph.Vertex;
import org.example.graph.algorithm.RouteAlgorithm;
import org.example.graph.algorithm.exporter.GraphExporter;
import org.example.graph.algorithm.exporter.factory.GraphExporterFactory;
import org.example.graph.algorithm.factory.RouteAlgorithmFactory;
import org.example.graph.algorithm.impl.ant.factory.AntAlgorithmFactory;
import org.example.graph.parser.GraphParser;
import org.example.graph.parser.strategy.GraphParserStrategy;
import org.example.graph.parser.strategy.factory.GraphParserStrategyFactory;

import java.util.Stack;

public class Main {
    @SneakyThrows
    public static void main(String[] args) {
        GraphParserStrategyFactory strategyFactory = GraphParserStrategyFactory.newInstance();
        GraphParserStrategy graphParserStrategy = strategyFactory.newStrategy();
        GraphParser graphParser = new GraphParser(graphParserStrategy);

        GraphExporterFactory exporterFactory = GraphExporterFactory.newInstance();
        GraphExporter graphExporter = exporterFactory.newGraphExporter();

        RouteAlgorithmFactory factory = AntAlgorithmFactory.newInstance();
        factory.setGraphParser(graphParser);
        factory.setGraphExporter(graphExporter);

        RouteAlgorithm routeAlgorithm = factory.createRouteAlgorithm();
        Stack<Vertex> vertices = routeAlgorithm.buildRoute(Vertex.from(1), Vertex.from(11));
        System.out.println(vertices);
    }
}