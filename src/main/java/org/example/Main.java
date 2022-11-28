package org.example;

import org.example.graph.Vertex;
import org.example.graph.algorithm.RouteAlgorithm;
import org.example.graph.algorithm.exception.RouteAlgorithmException;
import org.example.graph.algorithm.exception.RouteAlgorithmFactoryException;
import org.example.graph.algorithm.exporter.GraphExporter;
import org.example.graph.algorithm.exporter.factory.GraphExporterFactory;
import org.example.graph.algorithm.exporter.factory.exception.GraphExporterFactoryException;
import org.example.graph.algorithm.factory.RouteAlgorithmFactory;
import org.example.graph.algorithm.impl.ant.factory.AntAlgorithmFactory;
import org.example.graph.parser.GraphParser;
import org.example.graph.parser.strategy.GraphParserStrategy;
import org.example.graph.parser.strategy.factory.GraphParserStrategyFactory;
import org.example.graph.parser.strategy.factory.exception.GraphParserStrategyFactoryException;

import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            GraphParserStrategyFactory strategyFactory = GraphParserStrategyFactory.newInstance();
            GraphParserStrategy graphParserStrategy;
            graphParserStrategy = strategyFactory.newStrategy();
            GraphParser graphParser = new GraphParser(graphParserStrategy);

            RouteAlgorithmFactory factory = AntAlgorithmFactory.newInstance();
            factory.setGraphParser(graphParser);

            String action;
            do {
                try {
                    System.out.print("Write start point: ");
                    Vertex start = Vertex.valueOf(new Scanner(System.in).nextInt());
                    System.out.print("Write terminal point: ");
                    Vertex terminal = Vertex.valueOf(new Scanner(System.in).nextInt());
                    RouteAlgorithm routeAlgorithm = factory.createRouteAlgorithm();
                    List<Vertex> path = routeAlgorithm.buildRoute(start, terminal);
                    System.out.println("Result route: " + path);

                    System.out.print("You want to export graph? [yes/no]: ");
                    String choice = new Scanner(System.in).nextLine();
                    if (choice.equalsIgnoreCase("yes")) {
                        GraphExporterFactory exporterFactory = GraphExporterFactory.newInstance();
                        GraphExporter graphExporter = exporterFactory.newGraphExporter();
                        graphExporter.setGraph(routeAlgorithm.getGraph());
                        graphExporter.setPath(new HashSet<>(path));
                        System.out.println("Exporting...");
                        graphExporter.export();
                        System.out.println("Done!");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input");
                } catch (RouteAlgorithmException e) {
                    System.out.println(e.getMessage());
                }
                System.out.print("You want to continue? [yes/no]: ");
                action = new Scanner(System.in).nextLine();
            } while (action.equalsIgnoreCase("yes"));
        } catch (GraphParserStrategyFactoryException
                 | RouteAlgorithmFactoryException
                 | GraphExporterFactoryException e) {
            e.printStackTrace();
        }
    }
}